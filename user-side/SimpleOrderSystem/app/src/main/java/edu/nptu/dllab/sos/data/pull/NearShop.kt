package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.ShopState
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.NearShopKey
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
	
	fun getShopCopy() = HashMap<Int, Shop>(shops)
	
	override fun fromValue(value: Value) {
		Util.checkMapValue(value)
		val data = value.asMap()[NearShopKey.SHOP.toStringValue()]!!.asArrayValue()
		for(d in data) {
			if(d.isMapValue) {
				val mapData = d.asMapValue().map()
				try {
					val shopId = mapData[NearShopKey.SHOP_SHOP_ID.toStringValue()]!!.asInt()
					val mPosition = mapData[NearShopKey.SHOP_POSITION.toStringValue()]!!.asArrayValue()
					val position = Position(mPosition[0].asDouble(), mPosition[1].asDouble())
					val ts = mapData[NearShopKey.SHOP_TAGS.toStringValue()]!!.asArrayValue().map { it.asString() }
					val n = mapData[NearShopKey.SHOP_NAME.toStringValue()]?.asString() ?: Translator.getString("net.event.shop.noName")
					val jState = mapData[NearShopKey.SHOP_STATE.toStringValue()]!!.asString()
					val state = ShopState.getStateByString(jState)
					
					val shop = Shop(shopId, position, n, ts, state)
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
		map.put(NearShopKey.SHOP.toStringValue(), ValueFactory.newArray(shops.values.map { it.toValue() }))
		return map.build().toString()
	}
	
	/**
	 * Shop class, contains shop id, shop position and shop state
	 * @param shopId - the shop id
	 * @param position - shop position
	 * @param state - [ShopState]
	 */
	@SOSVersion(since = "0.0")
	data class Shop(val shopId: Int, val position: Position, val name: String,
	                val tags: List<String>, val state: ShopState) {
		
		fun toValue(): MapValue {
			val map = ValueFactory.newMapBuilder()
			map.put(NearShopKey.SHOP_SHOP_ID.toStringValue(), shopId.toIntegerValue())
			map.put(NearShopKey.SHOP_POSITION.toStringValue(), position.toValue())
			map.put(NearShopKey.SHOP_NAME.toStringValue(), name.toStringValue())
			map.put(NearShopKey.SHOP_TAGS.toStringValue(), ValueFactory.newArray(tags.map { it.toStringValue() }))
			map.put(NearShopKey.SHOP_STATE.toStringValue(), state.key.toStringValue())
			return map.build()
		}
		
	}
	
	companion object {
		const val EVENT_KEY = "near_shop"
	}
	
}