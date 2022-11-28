package edu.nptu.dllab.sos.data.pull

import android.content.Context
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.Resource
import edu.nptu.dllab.sos.data.item.ItemCondition
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.EventMenuKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

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
	fun getNeedDownloadResources(context: Context, shopId: Int): List<Resource> {
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
				val dbR = res[r.id]
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
		Util.checkMapValue(value)
		val map = value.asMap()
		val menu = map[EventMenuKey.MENU.toStringValue()]!!.asArrayValue()
		
		for(m in menu) {
			val mo = m.asMap()
			val cate = mo[EventMenuKey.MENU_CATE.toStringValue()]!!.asString()
			val t = mo[EventMenuKey.MENU_TYPE.toStringValue()]!!.asString()
			val pop = mo[EventMenuKey.MENU_POPUP.toStringValue()]!!.asBooleanValue().boolean
			val item = mo[EventMenuKey.MENU_ITEM.toStringValue()]!!.asString()
			val i = mo[EventMenuKey.MENU_ID.toStringValue()]!!.asInt()
			val ei = EventItem(cate, t, pop, item, i)
			val conO = mo[EventMenuKey.MENU_CONDITION.toStringValue()]!!.asMap()
			val con = ItemCondition.getCondition(conO[EventMenuKey.MENU_CONDITION_TYPE.toStringValue()]!!.asString())
			ei.addCondition(con)
			items.add(ei)
		}
		
		val resources = map[EventMenuKey.RESOURCE.toStringValue()]!!.asArrayValue()
		for(resource in resources) {
			val r = resource.asMap()
			val path = r[EventMenuKey.RES_PATH.toStringValue()]!!.asString()
			val id = r[EventMenuKey.RES_ID.toStringValue()]!!.asInt()
			val position = r[EventMenuKey.RES_POS.toStringValue()]!!.asString()
			val sha256 = r[EventMenuKey.RES_SHA256.toStringValue()]!!.asString()
			res.add(Resource(path, id, position, sha256))
		}
	}
	
	override fun toString(): String {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(EventMenuKey.MENU.toStringValue(), ValueFactory.newArray(items.map { it.toValue() }))
		map.put(EventMenuKey.RESOURCE.toStringValue(), ValueFactory.newArray(res.map { it.getValue() }))
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
			map.put(EventMenuKey.MENU_CATE.toStringValue(), category.toStringValue())
			map.put(EventMenuKey.MENU_TYPE.toStringValue(), type.toStringValue())
			map.put(EventMenuKey.MENU_POPUP.toStringValue(), ValueFactory.newBoolean(popup))
			map.put(EventMenuKey.MENU_ITEM.toStringValue(), itemId.toStringValue())
			map.put(EventMenuKey.MENU_ID.toStringValue(), id.toIntegerValue())
			map.put(EventMenuKey.MENU_CONDITION.toStringValue(), ValueFactory.newArray(conditions.map { it.toValue() }))
			return map.build()
		}
		
	}
	
	companion object {
		const val EVENT_KEY = "event_menu"
	}
	
}