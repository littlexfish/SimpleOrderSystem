package edu.nptu.dllab.sos

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.nptu.dllab.sos.data.OpenMenuEvent
import edu.nptu.dllab.sos.data.UpdateMenu
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBMenu
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.Util

class MenuActivity : AppCompatActivity() {
	
	private lateinit var loadingDialog: ProgressDialog
	private var shopId = -1
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_menu)
		
		val extra = intent.extras
		if(extra == null) finishActivity(Util.REQUEST_ERROR_EXTRA)
		
		// load value from extra
		shopId = extra!!.getInt(EXTRA_SHOP_ID)
		
		// start get data
		loadingDialog = ProgressDialog(this)
		loadingDialog.setMessage("Loading...")
		
		startLoading()
		
		Thread {
			// wait for server
			val db = DBHelper(applicationContext)
			val cur = db.select(DBHelper.TABLE_MENU, where = "${DBColumn.MENU_SHOP_ID}=$shopId", limit = 1)
			val menu = DBMenu(cur)
			val event = OpenMenuEvent()
			event.shopId = menu.shopId
			event.menuVersion = menu.version
			
			val handler = StaticData.socketHandler
			handler.pushEvent(event)
			
			var needUpdate = false
			val get = handler.waitEvent()
			if(get is UpdateMenu) {
				needUpdate = true
				TODO("Try to update menu")
			}
			
			run {
				//TODO("Update event menu")
			}
			
			run {
				//TODO("Try to get res from server")
			}
			
			run {
				//TODO("Build menu")
			}
			
			// stop loading dialog
			stopLoading()
		}.start()
		
	}
	
	private fun startLoading() {
		loadingDialog.show()
	}
	
	private fun stopLoading() {
		loadingDialog.dismiss()
	}
	
	companion object {
		const val EXTRA_SHOP_ID = "menu.extra.shopId"
	}
	
}