package edu.nptu.dllab.sos

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import edu.nptu.dllab.sos.data.*
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.databinding.ActivityMenuBinding
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.fragment.MenuFragment
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.FileIO
import edu.nptu.dllab.sos.io.ResourceWriter
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBMenu
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack

/**
 * The activity show the menu
 */
@SOSVersion(since = "0.0")
class MenuActivity : AppCompatActivity() {
	
	private val TAG = "MenuActivity"
	
	private lateinit var binding: ActivityMenuBinding
	
	/**
	 * The dialog to avoid user active when loading
	 */
	@SOSVersion(since = "0.0")
	private lateinit var loadingDialog: ProgressDialog
	
	/**
	 * The shop id
	 */
	@SOSVersion(since = "0.0")
	private var shopId = -1
	
	/**
	 * The thread to process network
	 */
	@SOSVersion(since = "0.0")
	private val handlerThread = HandlerThread("menu-net").also { it.start() }
	
	/**
	 * The handler to process network
	 */
	@SOSVersion(since = "0.0")
	private val handler = Handler(handlerThread.looper)
	
	
	/**
	 * The menu fragment now on
	 */
	@SOSVersion(since = "0.0")
	private var nowFragment: MenuFragment = ClassicMenuFragment.newInstance(-1)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMenuBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		val extra = intent.extras
		if(extra == null) finishActivity(Util.REQUEST_ERROR_EXTRA)
		
		// load value from extra
		shopId = extra!!.getInt(EXTRA_SHOP_ID)
		
		// start get data
		loadingDialog = ProgressDialog(this)
		loadingDialog.setMessage("Loading...")
		
		// auto load on first time
		loadFromServer()
		
	}
	
	/**
	 * Load menu from server
	 */
	@SOSVersion(since = "0.0")
	private fun loadFromServer() {
		handler.post {
			runOnUiThread { startLoading() }
			// wait for server
			val db = DBHelper(applicationContext)
			val cur = db.select(DBHelper.TABLE_MENU, where = "${DBColumn.MENU_SHOP_ID}=$shopId", limit = 1)
			val menu = DBMenu(cur)
			val event = OpenMenuEvent()
			event.shopId = menu.shopId
			event.menuVersion = menu.version
			
			val handler = StaticData.socketHandler
			handler.pushEvent(event)
			
			// update menu
			var needUpdate = false
			var get = handler.waitEvent()
			val resMap = HashMap<String, Resource>()
			run {
				if(get is UpdateMenu) { // need update
					val um = get as UpdateMenu
					needUpdate = true
					val getShopId = um.shopId
					if(getShopId == shopId) { // check id is same
						val newVersion = um.version
						checkAndChangeFrag(um.menu)
						// get resource
						putAllResource(resMap, um.getNeedDownloadResources())
						// save menu
						val menuFile = FileIO.newMenuFile()
						menuFile.setMenuData(um.menu.getMenuData())
						menuFile.write(applicationContext, shopId.toString())
						// build menu
						nowFragment.buildMenu(um.menu)
						// update database
						val newDbMenu = DBMenu(shopId, newVersion)
						db.writableDatabase.update(DBHelper.TABLE_MENU, newDbMenu.toContentValues(), "${DBColumn.RES_SHOP_ID.columnName}=$shopId", null)
					}
					else {
						Toast.makeText(applicationContext, Translator.getString("menu.error.shopId"), Toast.LENGTH_SHORT).show()
					}
				}
				else if(get is EventMenu) { // read old menu
					val menuFile = FileIO.getMenuFile(applicationContext, shopId)
					val inS = menuFile.inputStream()
					val value = MessagePack.newDefaultUnpacker(inS).unpackValue().asMapValue()
					inS.close()
					val type = value.map()[Util.UpdateKey.MENU_TYPE.key.toStringValue()]!!.asString()
					val menuBase = MenuBase.buildByType(MenuBase.MenuType.getTypeByString(type), shopId, menu.version, value)
					checkAndChangeFrag(menuBase)
					nowFragment.buildMenu(menuBase)
				}
				Unit
			}
			
			// insert event item
			run {
				if(needUpdate) get = handler.waitEvent()
				if(get is EventMenu) {
					val em = get as EventMenu
					nowFragment.insertAllEvent(em.items)
					putAllResource(resMap, em.getNeedDownloadResources())
				}
			}
			
			// download res from server
			// TODO: let user check that need download
			run {
				if(resMap.isNotEmpty()) {
					val download = DownloadRequestEvent()
					download.addAllPath(resMap.keys)
					
					while(true) {
						get = handler.waitEvent()
						if(get is ResourceDownload) {
							val rd = get as ResourceDownload
							val res = resMap[rd.path]!!
							if(!ResourceWriter.saveResource(applicationContext, shopId, res.id, res.position, rd.resData, rd.sha256)) {
								Log.w(TAG, "resource save error")
							}
							if(rd.fileIndex + 1 >= rd.fileTotal) break
						}
						else {
							handler.holdEvent(get)
							break
						}
					}
					runOnUiThread {
						nowFragment.reloadResource()
					}
				}
			}
			
			db.close()
			// stop loading dialog
			runOnUiThread { stopLoading() }
		}
	}
	
	/**
	 * Check the fragment is now on
	 */
	@SOSVersion(since = "0.0")
	private fun checkAndChangeFrag(menu: MenuBase) {
		if(nowFragment.javaClass != menu.getMenuFragmentClass()) { // change fragment
			val frag = menu.getMenuFragment()
			runOnUiThread {
				val tra = supportFragmentManager.beginTransaction()
				tra.replace(binding.menuFragment.id, frag)
				nowFragment = frag
			}
		}
	}
	
	/**
	 * Put the resource in map
	 */
	@SOSVersion(since = "0.0")
	private fun putInResource(resMap: HashMap<String, Resource>, res: Resource) {
		resMap[res.path] = res
	}
	
	/**
	 * Put all resource in map
	 */
	@SOSVersion(since = "0.0")
	private fun putAllResource(resMap: HashMap<String, Resource>, res: Iterable<Resource>) {
		for(r in res) {
			putInResource(resMap, r)
		}
	}
	
	/**
	 * Request on loading
	 */
	@SOSVersion(since = "0.0")
	private fun startLoading() {
		loadingDialog.show()
	}
	
	/**
	 * Request on not loading
	 */
	@SOSVersion(since = "0.0")
	private fun stopLoading() {
		loadingDialog.dismiss()
	}
	
	companion object {
		const val EXTRA_SHOP_ID = "menu.extra.shopId"
	}
	
}