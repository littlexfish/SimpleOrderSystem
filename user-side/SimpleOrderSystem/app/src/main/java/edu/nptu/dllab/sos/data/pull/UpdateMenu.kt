package edu.nptu.dllab.sos.data.pull

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.Resource
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.UpdateKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessageTypeCastException
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import kotlin.collections.ArrayList

/**
 * The event to store menu need to update
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class UpdateMenu : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The shop id
	@SOSVersion(since = "0.0")
	 */
	var shopId: Int = -1
	
	/**
	 * The menu version
	 */
	@SOSVersion(since = "0.0")
	var version: Int = -1
	
	/**
	 * The menu object
	 * @see MenuBase
	 */
	@SOSVersion(since = "0.0")
	var menu: MenuBase = ClassicMenu(shopId, version)
	
	/**
	 * The resources menu needed
	 */
	@SOSVersion(since = "0.0")
	private val res = ArrayList<Resource>()
	
	/**
	 * Get resources that need download
	 */
	@SOSVersion(since = "0.0")
	fun getNeedDownloadResources(): List<Resource> {
		TODO("Not yet implemented")
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
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(UpdateKey.SHOP_ID.toStringValue(), shopId.toIntegerValue())
		map.put(UpdateKey.MENU_VERSION.toStringValue(), version.toIntegerValue())
		map.put(UpdateKey.MENU.toStringValue(), menu.getMenuData())
		map.put(UpdateKey.RESOURCE.toStringValue(), ValueFactory.newArray(res.map { it.getValue() }))
		return map.build().toString()
	}
	
	companion object {
		const val EVENT_KEY = "update"
	}
	
}