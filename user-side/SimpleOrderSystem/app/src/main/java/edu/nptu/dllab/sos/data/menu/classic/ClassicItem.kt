package edu.nptu.dllab.sos.data.menu.classic

import edu.nptu.dllab.sos.data.menu.OrderItem
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.toFloatValue
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

private const val NOTE = "note"

private const val ITEM_CATEGORY = "category"
private const val ITEM_ITEM_ID = "itemId"
private const val ITEM_NAME = "name"
private const val ITEM_DES = "des"
private const val ITEM_PRICE = "price"
private const val ITEM_CURRENCY_CODE = "currencyCode"
private const val ITEM_ADDITION = "addition"
private const val ITEM_TAGS = "tags"
private const val ITEM_ID = "id"

/**
 * The order item of classic type menu
 */
@SOSVersion(since = "0.0")
class ClassicItem(shopId: Int, val cate: String, itemId: String, name: String, val des: String?,
                  price: Double, currencyCode: String, val addition: List<ClassicAddition>,
                  val tags: Array<String>, val resId: Int) : OrderItem(shopId, itemId, name, currencyCode, price) {
	
	/**
	 * Item note
	 */
	@SOSVersion(since = "0.0")
	var note: String = ""
	
	override fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toValue().map())
		map.put(ITEM_ADDITION.toStringValue(), ValueFactory.newArray(addition.map { it.toValue() }))
		map.put(NOTE.toStringValue(), note.toStringValue())
		return map.build()
	}
	
	override fun toMapData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(ITEM_CATEGORY.toStringValue(), cate.toStringValue())
		map.put(ITEM_ITEM_ID.toStringValue(), itemId.toStringValue())
		map.put(ITEM_NAME.toStringValue(), display.toStringValue())
		des?.let { map.put(ITEM_DES.toStringValue(), it.toStringValue()) }
		map.put(ITEM_PRICE.toStringValue(), price.toFloatValue())
		map.put(ITEM_CURRENCY_CODE.toStringValue(), currencyCode.toStringValue())
		map.put(ITEM_ADDITION.toStringValue(), ValueFactory.newArray(addition.map { it.toMenuData() }))
		map.put(ITEM_TAGS.toStringValue(), ValueFactory.newArray(tags.map { it.toStringValue() }))
		map.put(ITEM_ID.toStringValue(), resId.toIntegerValue())
		return map.build()
	}
	
}