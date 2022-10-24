package edu.nptu.dllab.sos.data.menu.classic

import androidx.annotation.CallSuper
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import edu.nptu.dllab.sos.util.Util.OrderKey.*
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toFloatValue
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue

private const val ID = "id"
private const val NAME = "name"
private const val DES = "des"
private const val TYPE = "type"
private const val MAX = "max"
private const val MIN = "min"
private const val STEP = "step"
private const val DEFAULT = "default"
private const val STRING_LENGTH = "length"
private const val RADIO_ITEM = "item"

private const val TYPE_INT = "int"
private const val TYPE_FLOAT = "float"
private const val TYPE_STRING = "string"
private const val TYPE_BOOL = "bool"
private const val TYPE_RADIO = "radio"

/**
 * The base addition of classic type menu
 */
@SOSVersion(since = "0.0")
abstract class ClassicAddition(var id: String = "", var name: String = "", var des: String? = null, var type: String = "") {
	
	/**
	 * Get addition clone
	 */
	@SOSVersion(since = "0.0")
	abstract fun clone(): ClassicAddition
	
	/**
	 * Get msgpack of this addition current value
	 */
	@SOSVersion(since = "0.0")
	protected abstract fun getValue(): Value
	
	/**
	 * Build addition from msgpack
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun fromValue(v: Value) {
		Util.checkMapValue(v)
		val map = v.asMap()
		id = map[ID.toStringValue()]!!.asString()
		name = map[NAME.toStringValue()]!!.asString()
		des = map[DES.toStringValue()]?.asString()
	}
	
	/**
	 * Get msgpack of this addition use for send
	 */
	@SOSVersion(since = "0.0")
	fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(ITEM_ADDITION_ID.key.toStringValue(), id.toStringValue())
		map.put(ITEM_ADDITION_TYPE.key.toStringValue(), type.toStringValue()) // TODO: check is needed
		map.put(ITEM_ADDITION_VALUE.key.toStringValue(), getValue())
		return map.build()
	}
	
	/**
	 * Get full msgpack of this addition
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(ID.toStringValue(), id.toStringValue())
		map.put(NAME.toStringValue(), name.toStringValue())
		des?.let { map.put(DES.toStringValue(), it.toStringValue()) }
		map.put(TYPE.toStringValue(), type.toStringValue())
		return map.build()
	}
	
	override fun equals(other: Any?): Boolean {
		if(other == null) return false
		if(other !is ClassicAddition) return false
		return other.id == id
	}
	
	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + name.hashCode()
		result = 31 * result + (des?.hashCode() ?: 0)
		result = 31 * result + type.hashCode()
		return result
	}
	
	companion object {
		/**
		 * Build addition by type
		 */
		@SOSVersion(since = "0.0")
		fun buildByValue(value: Value): ClassicAddition {
			Util.checkMapValue(value)
			val map = value.asMap()
			val add = when(map[TYPE.toStringValue()]!!.asString()) {
				TYPE_INT -> IntAddition()
				TYPE_FLOAT -> FloatAddition()
				TYPE_STRING -> StringAddition()
				TYPE_BOOL -> BoolAddition()
				TYPE_RADIO -> RadioAddition()
				else -> throw IllegalArgumentException("addition type not found")
			}
			add.fromValue(value)
			return add
		}
	}
	
}

/**
 * Int type addition
 */
@SOSVersion(since = "0.0")
class IntAddition(id: String = "", name: String = "", des: String? = null, var max: Int = -1, var min: Int = -1, var step: Int = 1, var value: Int = min) :
	ClassicAddition(id, name, des, TYPE_INT) {
	
	override fun clone(): IntAddition =
		IntAddition(id, name, des, max, min, step, value)
	
	override fun getValue(): Value = value.toIntegerValue()
	
	override fun fromValue(v: Value) {
		super.fromValue(v)
		val map = v.asMap()
		max = map[MAX.toStringValue()]!!.asInt()
		min = map[MIN.toStringValue()]!!.asInt()
		step = map[STEP.toStringValue()]?.asInt() ?: 1
		value = map[DEFAULT.toStringValue()]?.asInt() ?: min
	}
	
	override fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toMenuData().map())
		map.put(MAX.toStringValue(), max.toIntegerValue())
		map.put(MIN.toStringValue(), min.toIntegerValue())
		map.put(STEP.toStringValue(), step.toIntegerValue())
		map.put(DEFAULT.toStringValue(), value.toIntegerValue())
		return map.build()
	}
	
}

/**
 * Float type addition
 */
@SOSVersion(since = "0.0")
class FloatAddition(id: String = "", name: String = "", des: String? = null, var max: Double = -1.0, var min: Double = -1.0, var step: Double = 1.0, var value: Double = min) :
	ClassicAddition(id, name, des, TYPE_FLOAT) {
	
	override fun clone(): FloatAddition =
		FloatAddition(id, name, des, max, min, step, value)
	
	override fun getValue(): Value = value.toFloatValue()
	
	override fun fromValue(v: Value) {
		super.fromValue(v)
		val map = v.asMap()
		max = map[MAX.toStringValue()]!!.asDouble()
		min = map[MIN.toStringValue()]!!.asDouble()
		step = map[STEP.toStringValue()]?.asDouble() ?: 1.0
		value = map[DEFAULT.toStringValue()]?.asDouble() ?: min
	}
	
	override fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toMenuData().map())
		map.put(MAX.toStringValue(), max.toFloatValue())
		map.put(MIN.toStringValue(), min.toFloatValue())
		map.put(STEP.toStringValue(), step.toFloatValue())
		map.put(DEFAULT.toStringValue(), value.toFloatValue())
		return map.build()
	}
	
}

/**
 * String type addition
 */
@SOSVersion(since = "0.0")
class StringAddition(id: String = "", name: String = "", des: String? = null, var length: Int = 0, var value: String = "") :
	ClassicAddition(id, name, des, TYPE_STRING) {
	
	override fun clone(): StringAddition =
		StringAddition(id, name, des, length, value)
	
	override fun getValue(): Value = value.toStringValue()
	
	override fun fromValue(v: Value) {
		super.fromValue(v)
		val map = v.asMap()
		length = map[STRING_LENGTH.toStringValue()]!!.asInt()
	}
	
	override fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toMenuData().map())
		map.put(STRING_LENGTH.toStringValue(), length.toIntegerValue())
		return map.build()
	}
	
}

/**
 * Boolean type addition
 */
@SOSVersion(since = "0.0")
class BoolAddition(id: String = "", name: String = "", des: String? = null, var value: Boolean = false) :
	ClassicAddition(id, name, des, TYPE_BOOL) {
	
	override fun clone(): BoolAddition =
		BoolAddition(id, name, des, value)
	
	override fun getValue(): Value = ValueFactory.newBoolean(value)
	
	override fun fromValue(v: Value) {
		super.fromValue(v)
		val map = v.asMap()
		value = map[DEFAULT.toStringValue()]?.asBooleanValue()?.boolean ?: false
	}
	
	override fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toMenuData().map())
		map.put(DEFAULT.toStringValue(), ValueFactory.newBoolean(value))
		return map.build()
	}
	
}

/**
 * Radio(single select) type addition
 */
@SOSVersion(since = "0.0")
class RadioAddition(id: String = "", name: String = "", des: String? = null, var items: Array<String> = emptyArray(), var value: Int = 0) :
	ClassicAddition(id, name, des, TYPE_RADIO) {
	
	override fun clone(): RadioAddition =
		RadioAddition(id, name, des, items, value)
	
	override fun getValue(): Value = value.toIntegerValue()
	
	override fun fromValue(v: Value) {
		super.fromValue(v)
		val map = v.asMap()
		items = map[RADIO_ITEM.toStringValue()]!!.asArrayValue().map { it.asString() }.toTypedArray()
		value = map[DEFAULT.toStringValue()]?.asInt() ?: 0
	}
	
	override fun toMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toMenuData().map())
		map.put(RADIO_ITEM.toStringValue(), ValueFactory.newArray(items.map { it.toStringValue() }))
		map.put(DEFAULT.toStringValue(), value.toIntegerValue())
		return map.build()
	}
	
}