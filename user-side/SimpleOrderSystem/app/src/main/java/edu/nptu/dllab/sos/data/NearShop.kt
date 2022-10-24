package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.NearShopKey.*
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessageTypeCastException
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.lang.ClassCastException

/**
 * The event to store near shop
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class NearShop : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The [Shop]s that is near shop receive from server
	 */
	@SOSVersion(since = "0.0")
	private val shops = HashMap<Int, Shop>()
	
	/**
	 * Get shop by shopId
	 * @param shopId - the shop id
	 * @return [Shop] if has shop id, null if not exists
	 */
	@SOSVersion(since = "0.0")
	fun getShop(shopId: Int) = shops[shopId]
	
	/**
	 * Get amount of shops that saves here
	 */
	@SOSVersion(since = "0.0")
	fun getShopCount() = shops.size
	
	@Deprecated("use fromValue(Value)")
	override fun fromJson(json: JsonElement) {
		Util.checkJsonObject(json)
		val data = json.asJsonObject[SHOP.key].asJsonArray
		for(d in data) {
			if(d.isJsonObject) {
				val objData = d.asJsonObject
				try {
					val shopId = objData[SHOP_SHOP_ID.key].asInt
					val jPosition = objData[SHOP_POSITION.key].asJsonArray
					val position = Position(jPosition[0].asDouble, jPosition[1].asDouble)
					val ts = objData[SHOP_TAGS.key].asJsonArray.map { it.asString }
					val jState = objData[SHOP_STATE.key]
					val state = ShopState.getStateByString(jState.asString)
					
					val shop = Shop(shopId, position, ts, state)
					shops[shopId] = shop
				}
				catch(e: ClassCastException) {
					throw Exceptions.DataFormatException(e, "data format error: ${data.indexOf(d)}")
				}
			}
			else {
				throw Exceptions.DataFormatException("data not json object: ${data.indexOf(d)}")
			}
		}
	}
	
	override fun fromValue(value: Value) {
		Util.checkMapValue(value)
		val data = value.asMap()[SHOP.key.toStringValue()]!!.asArrayValue()
		for(d in data) {
			if(d.isMapValue) {
				val mapData = d.asMapValue().map()
				try {
					val shopId = mapData[SHOP_SHOP_ID.key.toStringValue()]!!.asInt()
					val mPosition = mapData[SHOP_POSITION.key.toStringValue()]!!.asArrayValue()
					val position = Position(mPosition[0].asDouble(), mPosition[1].asDouble())
					val ts = mapData[SHOP_TAGS.key.toStringValue()]!!.asArrayValue().map { it.asString() }
					val jState = mapData[SHOP_STATE.key.toStringValue()]!!.asString()
					val state = ShopState.getStateByString(jState)
					
					val shop = Shop(shopId, position, ts, state)
					shops[shopId] = shop
				}
				catch(e: MessageTypeCastException) {
					throw Exceptions.DataFormatException(e, "data format error: ${data.indexOf(d)}")
				}
			}
			else {
				throw Exceptions.DataFormatException("data not map value: ${data.indexOf(d)}")
			}
		}
	}
	
	override fun toString(): String {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(SHOP.key.toStringValue(), ValueFactory.newArray(shops.values.map { it.toValue() }))
		return map.build().toString()
	}
	
	/**
	 * Shop class, contains shop id, shop position and shop state
	 * @param shopId - the shop id
	 * @param position - shop position
	 * @param state - [ShopState]
	 */
	@SOSVersion(since = "0.0")
	data class Shop(val shopId: Int, val position: Position, val tags: List<String>, val state: ShopState) {
		
		fun toValue(): MapValue {
			val map = ValueFactory.newMapBuilder()
			map.put(SHOP_SHOP_ID.key.toStringValue(), shopId.toIntegerValue())
			map.put(SHOP_POSITION.key.toStringValue(), position.toValue())
			map.put(SHOP_TAGS.key.toStringValue(), ValueFactory.newArray(tags.map { it.toStringValue() }))
			map.put(SHOP_STATE.key.toStringValue(), state.key.toStringValue())
			return map.build()
		}
		
	}
	
	companion object {
		const val EVENT_KEY = "near_shop"
	}
	
}