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
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessageTypeCastException
import org.msgpack.value.Value
import java.lang.ClassCastException

/**
 * The event to store near shop
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class NearShop : EventPuller {
	
	/**
	 * The [Shop]s that is near shop receive from server
	 */
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
	
	/**
	 * Shop class, contains shop id, shop position and shop state
	 * @param shopId - the shop id
	 * @param position - shop position
	 * @param state - [ShopState]
	 */
	@SOSVersion(since = "0.0")
	data class Shop(val shopId: Int, val position: Position, val state: ShopState)
	
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
					val jState = objData[STATE.key]
					val state = ShopState.getStateByString(jState.asString)
					
					val shop = Shop(shopId, position, state)
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
					val jState = mapData[STATE.key.toStringValue()]!!.asString()
					val state = ShopState.getStateByString(jState)
					
					val shop = Shop(shopId, position, state)
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
	
}