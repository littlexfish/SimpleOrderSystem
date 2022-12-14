package edu.nptu.dllab.sos.data.pull

import android.content.Context
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.Resource
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.Util.UpdateKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessageTypeCastException
import org.msgpack.value.Value

/**
 * The event to store menu need to update
 *
 * @author Little Fish
 */
class UpdateMenu : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The shop id
	 */
	var shopId: Int = -1
	
	/**
	 * The menu version
	 */
	var version: Int = -1
	
	/**
	 * The menu object
	 * @see MenuBase
	 */
	var menu: MenuBase = ClassicMenu(shopId, version)
	
	/**
	 * The resources menu needed
	 */
	private val res = ArrayList<Resource>()
	
	/**
	 * Get resources that need download
	 */
	fun getNeedDownloadResources(context: Context): List<Resource> {
		val db = DBHelper(context)
		val cur = db.readableDatabase.rawQuery("SELECT * FROM ${DBHelper.TABLE_RES} WHERE ${DBColumn.MENU_SHOP_ID.columnName}=$shopId", null)
		val ret = ArrayList<Resource>()
		val dbRes = HashMap<Int, DBRes>()
		while(cur.moveToNext()) {
			val d = DBRes(cur)
			dbRes[d.id] = d
		}
		cur.close()
		
		for(r in res) {
			if(r.id in dbRes.keys) {
				val dbR = dbRes[r.id]!!
				if(r.sha256 != dbR.sha256) {
					ret.add(r)
				}
			}
			else {
				ret.add(r)
			}
		}
		
		return ret
	}
	
	override fun fromValue(value: Value) {
		if(!value.isMapValue) throw Exceptions.DataFormatException("data format not map type")
		try {
			val map = value.asMap()
			shopId = map[UpdateKey.SHOP_ID.toStringValue()]!!.asInt()
			version = map[UpdateKey.MENU_VERSION.toStringValue()]!!.asInt()
			val menuData = map[UpdateKey.MENU.toStringValue()]!!.asMapValue()
			val menuType = menuData.map()[UpdateKey.MENU_TYPE.toStringValue()]!!.asString()
			menu = MenuBase.buildByType(MenuBase.MenuType.getTypeByString(menuType), shopId, version, menuData)
			
			val resources = map[UpdateKey.RESOURCE.toStringValue()]!!.asArrayValue()
			for(resource in resources) {
				val d = resource.asMap()
				val path = d[UpdateKey.RES_PATH.toStringValue()]!!.asString()
				val id = d[UpdateKey.RES_ID.toStringValue()]!!.asInt()
				val position = d[UpdateKey.RES_POS.toStringValue()]!!.asString()
				val sha256 = d[UpdateKey.RES_SHA256.toStringValue()]!!.asString()
				
				res.add(Resource(path, id, position, sha256))
			}
		}
		catch(e: MessageTypeCastException) {
			throw Exceptions.DataFormatException(e, "data format error")
		}
	}
	
	override fun toString(): String {
		return "update { shopId=$shopId, version=$version }"
	}
	
	companion object {
		const val EVENT_KEY = "update"
	}
	
}