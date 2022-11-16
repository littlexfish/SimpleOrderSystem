package edu.nptu.dllab.sos.io

import android.content.Context
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toFloatValue
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.io.ByteArrayOutputStream

object Config {
	
	private const val FILE_NAME = "config"
	
	private val settings = HashMap<String, Any?>()
	
	fun init(context: Context) {
		if(!context.getFileStreamPath(FILE_NAME).exists()) {
			copyFile(context)
		}
		val i = context.openFileInput(FILE_NAME)
		val unpacker = MessagePack.newDefaultUnpacker(i)
		val value = unpacker.unpackValue()
		unpacker.close()
		i.close()
		settings.putAll(deepPut(value) as HashMap<String, Any?>)
	}
	
	private fun deepPut(value: Value?): Any? {
		if(value == null) return null
		return if(value.isMapValue) {
			val v = value.asMap()
			val map = HashMap<String, Any?>()
			for(k in v.keys) {
				map[k.asString()] = deepPut(v[k])
			}
			map
		}
		else if(value.isStringValue) value.asString()
		else if(value.isIntegerValue) value.asInt()
		else if(value.isFloatValue) value.asDouble()
		else if(value.isBooleanValue) value.asBooleanValue().boolean
		else null
	}
	
	private fun copyFile(context: Context) {
		val i = context.assets.open(FILE_NAME)
		val o = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
		i.copyTo(o)
		o.close()
		i.close()
	}
	
	fun getConfig(key: String): Any? {
		val keys = key.split('.')
		var v = settings[keys[0]]
		for(i in 1 until keys.size) {
			if(v == null) return null
			if(v is HashMap<*, *>) v = v[keys[i]]
			else return v
		}
		return v
	}
	
	fun getInt(key: String) = getConfig(key)!! as Int
	
	fun getString(key: String) = getConfig(key)!! as String
	
	fun getDouble(key: String) = getConfig(key)!! as Double
	
	fun getBoolean(key: String) = getConfig(key)!! as Boolean
	
	fun setConfig(key: String, value: Any?) {
		val keys = key.split('.')
		var v = settings[keys[0]]
		for(i in 1 until keys.size - 1) {
			if(v == null) return
			if(v is HashMap<*, *>) v = v[keys[i]]
			else return
		}
		if(v != null && v is HashMap<*, *>) (v as HashMap<String, Any?>)[keys.last()] = value
	}
	
	fun saveConfig(context: Context) {
		val buffer = ByteArrayOutputStream()
		val packer = MessagePack.newDefaultPacker(buffer)
		packer.packValue(deepToValue(settings))
		packer.flush()
		packer.close()
		val o = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
		buffer.writeTo(o)
		o.close()
	}
	
	private fun deepToValue(any: Any?): Value {
		return when(any) {
			null -> ValueFactory.newNil()
			is HashMap<*, *> -> {
				any as HashMap<String, Any?>
				val map = ValueFactory.newMapBuilder()
				for(k in any.keys) {
					map.put(k.toStringValue(), deepToValue(any[k]))
				}
				map.build()
			}
			is String -> any.toStringValue()
			is Int -> any.toIntegerValue()
			is Double -> any.toFloatValue()
			is Boolean -> ValueFactory.newBoolean(any)
			else -> ValueFactory.newNil()
		}
	}
	
	object Key {
		const val LANG = "lang"
		const val LINK_ON_START = "link_on_start"
		const val SHOW_CLOSED_SHOP = "shop.show_closed"
		const val DISTANCE_UNIT = "shop.dist_unit"
	}
	
}