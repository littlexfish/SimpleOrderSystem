package edu.nptu.dllab.sos.data.shop

import android.os.Parcel
import android.os.Parcelable
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import kotlin.experimental.and

private const val KEY_ID = "id"
private const val KEY_TYPE = "type"
private const val KEY_VALUE = "value"

/**
 * A class of one addition in item
 *
 * @author Little Fish
 */
abstract class Addition: Parcelable {
	
	/**
	 * The addition id
	 */
	val id: String
	
	/**
	 * The addition type
	 */
	val type: String
	
	constructor(i: String, t: String) {
		id = i
		type = t
	}
	
	constructor(parcel: Parcel) {
		id = parcel.readString() ?: ""
		type = parcel.readString() ?: ""
	}
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(type)
	}
	
	companion object {
		fun fromMapValue(value: MapValue): Addition {
			val map = value.map()
			val id = map[KEY_ID.toStringValue()]!!.asString()
			val type = map[KEY_TYPE.toStringValue()]!!.asString()
			val v = map[KEY_VALUE.toStringValue()]!!
			return when(type) {
				"int" -> IntAddition(id, v.asInt())
				"float" -> FloatAddition(id, v.asDouble())
				"string" -> StringAddition(id, v.asString())
				"bool" -> BoolAddition(id, v.asBooleanValue().boolean)
				"radio" -> RadioAddition(id, v.asInt())
				else -> throw IllegalArgumentException("type error: $type")
			}
		}
	}
	
	/**
	 * Int type addition
	 */
	class IntAddition : Addition {
		
		val value: Int
		
		constructor(id: String, v: Int) : super(id, "int") {
			value = v
		}
		
		constructor(parcel: Parcel) : super(parcel) {
			value = parcel.readInt()
		}
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			super.writeToParcel(parcel, flags)
			parcel.writeInt(value)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<IntAddition> {
			override fun createFromParcel(parcel: Parcel): IntAddition {
				return IntAddition(parcel)
			}
			
			override fun newArray(size: Int): Array<IntAddition?> {
				return arrayOfNulls(size)
			}
		}
	}
	
	/**
	 * Float type addition
	 */
	class FloatAddition : Addition {
		
		val value: Double
		
		constructor(parcel: Parcel) : super(parcel) {
			value = parcel.readDouble()
		}
		
		constructor(id: String, v: Double) : super(id, "float") {
			value = v
		}
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			super.writeToParcel(parcel, flags)
			parcel.writeDouble(value)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<FloatAddition> {
			override fun createFromParcel(parcel: Parcel): FloatAddition {
				return FloatAddition(parcel)
			}
			
			override fun newArray(size: Int): Array<FloatAddition?> {
				return arrayOfNulls(size)
			}
		}
	}
	
	/**
	 * String type addition
	 */
	class StringAddition : Addition {
		
		val value: String
		
		constructor(parcel: Parcel) : super(parcel) {
			value = parcel.readString() ?: ""
		}
		
		constructor(id: String, v: String) : super(id, "string") {
			value = v
		}
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			super.writeToParcel(parcel, flags)
			parcel.writeString(value)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<StringAddition> {
			override fun createFromParcel(parcel: Parcel): StringAddition {
				return StringAddition(parcel)
			}
			
			override fun newArray(size: Int): Array<StringAddition?> {
				return arrayOfNulls(size)
			}
		}
	}
	
	/**
	 * Boolean type addition
	 */
	class BoolAddition : Addition {
		val value: Boolean
		
		constructor(parcel: Parcel) : super(parcel) {
			value = parcel.readByte() and 1 == 0b1.toByte()
		}
		
		constructor(id: String, v: Boolean) : super(id, "bool") {
			value = v
		}
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			super.writeToParcel(parcel, flags)
			parcel.writeByte(if(value) 1 else 0)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<BoolAddition> {
			override fun createFromParcel(parcel: Parcel): BoolAddition {
				return BoolAddition(parcel)
			}
			
			override fun newArray(size: Int): Array<BoolAddition?> {
				return arrayOfNulls(size)
			}
		}
	}
	
	/**
	 * Radio type addition
	 */
	class RadioAddition : Addition {
		val value: Int
		
		constructor(parcel: Parcel) : super(parcel) {
			value = parcel.readInt()
		}
		
		constructor(id: String, v: Int) : super(id, "radio") {
			value = v
		}
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			super.writeToParcel(parcel, flags)
			parcel.writeInt(value)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<RadioAddition> {
			override fun createFromParcel(parcel: Parcel): RadioAddition {
				return RadioAddition(parcel)
			}
			
			override fun newArray(size: Int): Array<RadioAddition?> {
				return arrayOfNulls(size)
			}
		}
	}

}