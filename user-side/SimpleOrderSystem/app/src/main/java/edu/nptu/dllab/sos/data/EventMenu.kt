package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.data.item.ItemCondition
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.EventMenuKey.*
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import kotlin.collections.ArrayList

/**
 * The event menu event
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class EventMenu : Event(EVENT_KEY), EventPuller {
	
	val items = ArrayList<EventItem>()
	
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
	
	@Deprecated("use fromValue(Value)")
	override fun fromJson(json: JsonElement) {
		Util.checkJsonObject(json)
		val data = json.asJsonObject
		val menu = data[MENU.key].asJsonArray
		
		for(m in menu) {
			val mo = m.asJsonObject
			val cate = mo[MENU_CATE.key].asString
			val t = mo[MENU_TYPE.key].asString
			val pop = mo[MENU_POPUP.key].asBoolean
			val item = mo[MENU_ITEM.key].asString
			val i = mo[MENU_ID.key].asInt
			val ei = EventItem(cate, t, pop, item, i)
			val conO = mo[MENU_CONDITION.key].asJsonObject
			val con = ItemCondition.getCondition(conO[MENU_CONDITION_TYPE.key].asString)
			ei.addCondition(con)
			items.add(ei)
		}
		
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
	
	override fun fromValue(value: Value) {
		Util.checkMapValue(value)
		val map = value.asMap()
		val menu = map[MENU.key.toStringValue()]!!.asArrayValue()
		
		for(m in menu) {
			val mo = m.asMap()
			val cate = mo[MENU_CATE.key.toStringValue()]!!.asString()
			val t = mo[MENU_TYPE.key.toStringValue()]!!.asString()
			val pop = mo[MENU_POPUP.key.toStringValue()]!!.asBooleanValue().boolean
			val item = mo[MENU_ITEM.key.toStringValue()]!!.asString()
			val i = mo[MENU_ID.key.toStringValue()]!!.asInt()
			val ei = EventItem(cate, t, pop, item, i)
			val conO = mo[MENU_CONDITION.key.toStringValue()]!!.asMap()
			val con = ItemCondition.getCondition(conO[MENU_CONDITION_TYPE.key.toStringValue()]!!.asString())
			ei.addCondition(con)
			items.add(ei)
		}
		
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
	
	override fun toString(): String {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(MENU.key.toStringValue(), ValueFactory.newArray(items.map { it.toValue() }))
		map.put(RESOURCE.key.toStringValue(), ValueFactory.newArray(res.map { it.getValue() }))
		return map.build().toJson()
	}
	
	/**
	 * The event item
	 */
	@SOSVersion(since = "0.0")
	class EventItem(val category: String, val type: String, val popup: Boolean, val itemId: String, val id: Int) {
		
		private val conditions = ArrayList<ItemCondition>()
		
		/**
		 * Add condition
		 */
		@SOSVersion(since = "0.0")
		fun addCondition(con: ItemCondition) {
			conditions.add(con)
		}
		
		/**
		 * Get msgpack of this item
		 */
		@SOSVersion(since = "0.0")
		fun toValue(): MapValue {
			val map = ValueFactory.newMapBuilder()
			map.put(MENU_CATE.key.toStringValue(), category.toStringValue())
			map.put(MENU_TYPE.key.toStringValue(), type.toStringValue())
			map.put(MENU_POPUP.key.toStringValue(), ValueFactory.newBoolean(popup))
			map.put(MENU_ITEM.key.toStringValue(), itemId.toStringValue())
			map.put(MENU_ID.key.toStringValue(), id.toIntegerValue())
			map.put(MENU_CONDITION.key.toStringValue(), ValueFactory.newArray(conditions.map { it.toValue() }))
			return map.build()
		}
		
	}
	
	companion object {
		const val EVENT_KEY = "event_menu"
	}
	
}