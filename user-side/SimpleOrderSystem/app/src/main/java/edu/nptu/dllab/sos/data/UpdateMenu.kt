package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.data.menu.ClassicMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.UpdateKey.*
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessageTypeCastException
import org.msgpack.value.Value
import java.lang.ClassCastException
import kotlin.collections.ArrayList

/**
 * The event to store menu need to update
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class UpdateMenu : EventPuller {
	
	/**
	 * The shop id
	 */
	private var shopId: Int = -1
	
	/**
	 * The menu version
	 */
	private var version: Int = -1
	
	/**
	 * The menu object
	 * @see MenuBase
	 */
	private var menu: MenuBase = ClassicMenu(shopId, version)
	
	/**
	 * The resources menu needed
	 */
	private val res = ArrayList<Resource>()
	
	/**
	 * Get resources that need download
	 */
	@SOSVersion(since = "0.0")
	fun getNeedDownloadResources(): List<Resource> {
		TODO("Not yet implemented")
	}
	
	@SOSVersion(since = "0.0")
	override fun fromJson(json: JsonElement) {
		if(!json.isJsonObject) throw Exceptions.DataFormatException("data format not object type")
		val data = json.asJsonObject
		try {
			shopId = data[SHOP_ID.key].asInt
			version = data[MENU_VERSION.key].asInt
			val menuData = data[MENU.key].asJsonObject
			val menuType = menuData[MENU_TYPE.key].asString
			menu = MenuBase.buildByType(MenuBase.MenuType.getTypeByString(menuType), shopId, version, menuData)
			
			val resources = data[RESOURCE.key].asJsonArray
			for(resource in resources) {
				val d = resource.asJsonObject
				val path = d[RES_PATH.key].asString
				val id = d[RES_ID.key].asInt
				val position = d[RES_POS.key].asString
				val sha256 = d[RES_SHA256.key].asString
				
				res.add(Resource(path, id, position, sha256))
			}
		}
		catch(e: ClassCastException) {
			throw Exceptions.DataFormatException(e, "data format error")
		}
	}
	
	@SOSVersion(since = "0.0")
	override fun fromValue(value: Value) {
		if(!value.isMapValue) throw Exceptions.DataFormatException("data format not map type")
		try {
			val map = value.asMap()
			shopId = map[SHOP_ID.key.toStringValue()]!!.asInt()
			version = map[MENU_VERSION.key.toStringValue()]!!.asInt()
			val menuData = map[MENU.key.toStringValue()]!!.asMapValue()
			val menuType = menuData.map()[MENU_TYPE.key.toStringValue()]!!.asString()
			menu = MenuBase.buildByType(MenuBase.MenuType.getTypeByString(menuType), shopId, version, menuData)
			
			val resources = map[RESOURCE.key.toStringValue()]!!.asArrayValue()
			for(resource in resources) {
				val d = resource.asMap()
				val path = d[RES_PATH.key.toStringValue()]!!.asString()
				val id = d[RES_ID.key.toStringValue()]!!.asInt()
				val position = d[RES_POS.key.toStringValue()]!!.asString()
				val sha256 = d[RES_SHA256.key.toStringValue()]!!.asString()
				
				res.add(Resource(path, id, position, sha256))
			}
		}
		catch(e: MessageTypeCastException) {
			throw Exceptions.DataFormatException(e, "data format error")
		}
	}
	
}