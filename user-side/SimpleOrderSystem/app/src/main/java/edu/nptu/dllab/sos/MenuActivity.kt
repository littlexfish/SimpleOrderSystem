package edu.nptu.dllab.sos

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.Resource
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.data.pull.EventMenu
import edu.nptu.dllab.sos.data.pull.ResourceDownload
import edu.nptu.dllab.sos.data.pull.UpdateMenu
import edu.nptu.dllab.sos.data.pull.Error
import edu.nptu.dllab.sos.data.push.DownloadRequestEvent
import edu.nptu.dllab.sos.data.push.OpenMenuEvent
import edu.nptu.dllab.sos.databinding.ActivityMenuBinding
import edu.nptu.dllab.sos.dialog.LoadingDialog
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.fragment.MenuFragment
import edu.nptu.dllab.sos.io.*
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBMenu
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack
import java.net.SocketException

/**
 * The activity show the menu
 */
class MenuActivity : AppCompatActivity() {
	
	private val tag = "MenuActivity"
	
	private lateinit var binding: ActivityMenuBinding
	
	/**
	 * The dialog to avoid user active when loading
	 */
	private lateinit var loadingDialog: LoadingDialog
	
	/**
	 * The shop id
	 */
	private var shopId = -1
	
	private var name = ""
	
	/**
	 * The thread to process network
	 */
	private val handlerThread = HandlerThread("menu-net").also { it.start() }
	
	/**
	 * The handler to process network
	 */
	private val handler = Handler(handlerThread.looper)
	
	/**
	 * The menu fragment now on
	 */
	private var nowFragment: MenuFragment = ClassicMenuFragment.newInstance(-1)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMenuBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		val extra = intent.extras
		if(extra == null) finishActivity(Util.REQUEST_ERROR_EXTRA)
		
		// load shop id from extra
		shopId = extra!!.getInt(EXTRA_SHOP_ID)
		name = extra.getString(EXTRA_NAME) ?: ""
		
		binding.menuTitle.text = name
		
		// start get data
		loadingDialog = LoadingDialog(this)
		
		// auto load on first time
		binding.menuFragment.post {
			loadMenu(true)
		}
		
		onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if(!nowFragment.onBackPressed()) {
					backToList()
				}
			}
		})
		
		// Close activity when touch back button
		binding.menuBack.setOnClickListener {
			backToList()
		}
		
		binding.menuReload.setOnClickListener { loadMenu(false) }
		
		binding.menuCart.setOnClickListener {
			startActivity(Intent(this, OrderActivity::class.java))
		}
		
	}
	
	/**
	 * Let user accept finish this activity
	 */
	private fun backToList() {
		AlertDialog.Builder(this)
			.setMessage(Translator.getString("menu.back.list"))
			.setPositiveButton(Translator.getString("menu.download.confirm")) { _, _ ->
				finish()
			}
			.setNegativeButton(Translator.getString("menu.download.cancel")) { _, _ -> }
			.create().show()
	}
	
	/**
	 * Request load menu and replace it
	 */
	private fun loadMenu(firstLoad: Boolean) {
		/*
		 * FISH NOTE:
		 *   Load test menu when shop is less than 0
		 */
		if(shopId < 0) {
			testLoadMenu(firstLoad)
		}
		else {
			loadFromServer(firstLoad)
		}
	}
	
	/**
	 * Load test menu when shopId is less than 0
	 */
	private fun testLoadMenu(firstLoad: Boolean) {
		loadingDialog.show()
		// Force load menu from assets/testMenu.menu
		val inS = resources.assets.open("testMenu.menu")
		
		val value = MessagePack.newDefaultUnpacker(inS).unpackValue().asMapValue()
		inS.close()
		
		val type = value.map()[Util.UpdateKey.MENU_TYPE.toStringValue()]!!.asString()
		val menuBase =
			MenuBase.buildByType(MenuBase.MenuType.getTypeByString(type), shopId, 0, value)
		changeFragAndBuildMenu(menuBase)
		
		if(firstLoad) {
			/*
			 * FISH NOTE:
			 *   The dialog emulate the download request
			 */
			AlertDialog.Builder(this)
				.setMessage(Translator.getString("menu.download.message"))
				.setPositiveButton(Translator.getString("menu.download.confirm")) { _, _ ->
					val dl = DownloadDialog(this)
					dl.show()
					dl.set(0, 0)
					handler.post {
						val max = (Math.random() * 5).toInt() + 2
						for(i in 0 until max) {
							Thread.sleep((Math.random() * 2000).toLong() + 500)
							runOnUiThread { dl.set(i + 1, max) }
						}
						
						runOnUiThread {
							dl.dismiss()
							loadingDialog.dismiss()
						}
					}
				}
				.setNegativeButton(Translator.getString("menu.download.cancel")) { _, _ ->
					loadingDialog.dismiss()
				}
				.setCancelable(false)
				.create().show()
		}
		else {
			loadingDialog.dismiss()
		}
	}
	
	/**
	 * Load menu from server
	 */
	private fun loadFromServer(firstLoad: Boolean) {
		handler.post {
			runOnUiThread { loadingDialog.show() }
			/*
			 * FISH NOTE:
			 *   Get information on database
			 */
			val db = DBHelper(applicationContext)
			val cur = db.select(DBHelper.TABLE_MENU, where = "${DBColumn.MENU_SHOP_ID.columnName}=$shopId", limit = 1)
			val event = OpenMenuEvent()
			event.shopId = shopId
			if(cur.count <= 0) { // no any menu
				event.menuVersion = -1
			}
			else {
				val menu = DBMenu(cur)
				event.menuVersion = menu.version
			}
			
			/*
			 * FISH NOTE:
			 *   Ensure the socket is connect and force finish
			 *    when any socket error.
			 */
			try {
				StaticData.ensureSocketHandler(this)
			}
			catch(e: SocketException) {
				finish()
				return@post
			}
			
			val handler = StaticData.socketHandler
			handler.pushEventRePush(event)
			
			// update menu
			var needUpdate = false
			var get = handler.waitEvent()
			if(SocketHandler.checkErrorAndThrow(applicationContext, get as Event)) {
				return@post
			}
			
			val resMap = HashMap<String, Resource>()
			/*
			 * FISH NOTE:
			 *   If need update the menu, we will get [UpdateMenu],
			 *    and get [EventMenu] if not need update, than we will
			 *    skip this block
			 */
			run {
				if(get is UpdateMenu) { // need update
					val um = get as UpdateMenu
					needUpdate = true
					val getShopId = um.shopId
					if(getShopId == shopId) { // check id is same
						val newVersion = um.version
						// get resource
						putAllResource(resMap, um.getNeedDownloadResources(applicationContext))
						// save menu
						val menuFile = FileIO.newMenuFile()
						menuFile.setMenuData(um.menu.getMenuData())
						menuFile.write(applicationContext, shopId.toString())
						// build menu
						runOnUiThread {
							changeFragAndBuildMenu(um.menu)
						}
						// update database
						val newDbMenu = DBMenu(shopId, name, newVersion)
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
					val type = value.map()[Util.UpdateKey.MENU_TYPE.toStringValue()]!!.asString()
					val menuBase = MenuBase.buildByType(MenuBase.MenuType.getTypeByString(type), shopId, event.menuVersion, value)
					changeFragAndBuildMenu(menuBase)
					runOnUiThread {
						nowFragment.buildMenu(menuBase)
					}
				}
				Unit
			}
			
			// insert event item
			/*
			 * FISH NOTE:
			 *   If need update, we will wait event again to get [EventMenu].
			 *   Than we process the data what we get from server
			 */
			run {
				if(needUpdate) {
					get = handler.waitEvent()
					if(SocketHandler.checkErrorAndThrow(applicationContext, get as Event)) {
						return@post
					}
				}
				if(get is EventMenu) {
					val em = get as EventMenu
					runOnUiThread {
						nowFragment.insertAllEvent(em.items)
					}
					putAllResource(resMap, em.getNeedDownloadResources(applicationContext, shopId))
				}
			}
			
			if(firstLoad && resMap.isNotEmpty()) {
				runOnUiThread {
					/*
					 * FISH NOTE:
					 *   The dialog let user has choice that not download
					 *    any resource on this menu.
					 *   Request download when user choose confirm, or do nothing
					 *    when user choose cancel
					 */
					// TODO: (FUTURE)Add check box let user has choice
					//  that not show download confirm again when user
					//  had cancel once
					AlertDialog.Builder(this)
						.setMessage(Translator.getString("menu.download.message"))
						.setPositiveButton(Translator.getString("menu.download.confirm")) { _, _ ->
							val dl = DownloadDialog(this)
							dl.show()
							dl.set(0, 0)
							loadingDialog.dismiss()
							this.handler.post {
								val download = DownloadRequestEvent()
								download.addAllPath(resMap.keys)
								handler.pushEventRePush(download)
								
								while(true) {
									get = handler.waitEvent()
									if(get is Error && Error.isErrorEquals(get as Error, Error.ERR_NO_RESOURCE)) {
										break
									}
									if(get is ResourceDownload) {
										val rd = get as ResourceDownload
										runOnUiThread {
											dl.set(rd.fileIndex + 1, rd.fileTotal)
										}
										val res = resMap[rd.path]!!
										if(!ResourceWriter.saveResource(applicationContext, shopId, res.id, res.path, rd.resData, rd.sha256)) {
											Log.w(tag, "resource save error")
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
								
								db.close()
								runOnUiThread {
									dl.dismiss()
								}
							}
						}
						.setNegativeButton(Translator.getString("menu.download.cancel")) { _, _ ->
							db.close()
							loadingDialog.dismiss()
						}
						.setCancelable(false) // let dialog cannot cancel
						.create().show()
				}
			}
			else {
				db.close()
				runOnUiThread { loadingDialog.dismissAtLeast() }
			}
		}
	}
	
	/**
	 * Change fragment and build menu with menu base
	 */
	private fun changeFragAndBuildMenu(menu: MenuBase) {
		runOnUiThread {
			val frag = menu.getMenuFragment()
			val tra = supportFragmentManager.beginTransaction()
			tra.replace(binding.menuFragment.id, frag)
			tra.commit()
			nowFragment = frag
			nowFragment.buildMenu(menu)
		}
	}
	
	/**
	 * Put the resource in map
	 */
	private fun putInResource(resMap: HashMap<String, Resource>, res: Resource) {
		resMap[res.path] = res
	}
	
	/**
	 * Put all resource in map
	 */
	private fun putAllResource(resMap: HashMap<String, Resource>, res: Iterable<Resource>) {
		for(r in res) {
			putInResource(resMap, r)
		}
	}
	
	fun animCart() {
		(binding.menuCart.drawable as AnimatedVectorDrawable).let {
			it.reset()
			it.start()
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		loadingDialog.dismiss()
		handlerThread.quitSafely()
	}
	
	companion object {
		const val EXTRA_SHOP_ID = "menu.extra.shopId"
		const val EXTRA_NAME = "menu.extra.name"
	}
	
	/**
	 * The dialog shows download status
	 */
	class DownloadDialog(context: Context) : ProgressDialog(context) {
		
		init {
			setProgressStyle(STYLE_HORIZONTAL)
			setCanceledOnTouchOutside(false)
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				onBackInvokedDispatcher.registerOnBackInvokedCallback(
					1) {					// do nothing
				}
			}
		}
		
		override fun onBackPressed() {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
			// do nothing
			}
		}
		
		fun set(value: Int, max: Int) {
			this.max = max
			progress = value
			setMessage(Translator.getString("menu.download.progress").format(value, max))
		}
		
	}
	
}