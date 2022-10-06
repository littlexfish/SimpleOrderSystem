package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.data.menu.ClassicMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.EventMenuKey.*
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import kotlin.collections.ArrayList

/**
 * The event menu event
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class EventMenu(m: MenuBase = ClassicMenu(-1, -1)) : EventPuller {
	
	/**
	 * The menu to be attach
	 */
	private var menu: MenuBase = m
	
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
		Util.checkJsonObject(json)
		val data = json.asJsonObject
		val m = data[MENU.key]
		menu.insertEvent(m.asJsonArray)
		
		val resources = data[RESOURCE.key].asJsonArray
		for(resource in resources) {
			val r = resource.asJsonObject
			val path = r[RES_PATH.key].asString
			val id = r[RES_ID.key].asInt
			val position = r[RES_POS.key].asString
			val sha256 = r[RES_SHA256.key].asString
			res.add(Resource(path, id, position, sha256))
		}
	}
	
	@SOSVersion(since = "0.0")
	override fun fromValue(value: Value) {
		Util.checkMapValue(value)
		val map = value.asMap()
		val m = map[MENU.key.toStringValue()]!!
		menu.insertEvent(m.asArrayValue())
		
		val resources = map[RESOURCE.key.toStringValue()]!!.asArrayValue()
		for(resource in resources) {
			val r = resource.asMap()
			val path = r[RES_PATH.key.toStringValue()]!!.asString()
			val id = r[RES_ID.key.toStringValue()]!!.asInt()
			val position = r[RES_POS.key.toStringValue()]!!.asString()
			val sha256 = r[RES_SHA256.key.toStringValue()]!!.asString()
			res.add(Resource(path, id, position, sha256))
		}
	}
	
}