package edu.nptu.dllab.sos.io

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
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
	
	private val default = HashMap<String, Any>()
	private lateinit var settings: SharedPreferences
	
	init {
		default[Key.LANG] = "zh_tw"
		default[Key.LINK_ON_START] = false
		default[Key.TIME_FORMAT] = "Y4M2D2_HM"
		default[Key.SHOW_CLOSED_SHOP] = false
		default[Key.DISTANCE_UNIT] = "m"
	}
	
	fun init(context: Context) {
		settings = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
//		settings.edit {
//			var change = false
//			for(k in default.keys) {
//				if(!settings.contains(k)) {
//					change = true
//					putDefault(this, k, default[k]!!)
//				}
//			}
//			if(change) commit()
//		}
	}
	
	private fun putDefault(edit: SharedPreferences.Editor, key: String, value: Any) {
		when(value) {
			is Int -> edit.putInt(key, value)
			is Double -> edit.putFloat(key, value.toFloat())
			is Boolean -> edit.putBoolean(key, value)
			is String -> edit.putString(key, value)
		}
	}
	
	fun getInt(key: String) = settings.getInt(key, default[key] as? Int ?: throwNoDefault(key))
	
	fun getString(key: String) = settings.getString(key, default[key] as? String ?: throwNoDefault(key))!!
	
	fun getDouble(key: String) = settings.getFloat(key, default[key] as? Float ?: throwNoDefault(key)).toDouble()
	
	fun getBoolean(key: String) = settings.getBoolean(key, default[key] as? Boolean ?: throwNoDefault(key))
	
	fun setConfig(key: String, value: Any) {
		settings.edit {
			when(value) {
				is Int -> putInt(key, value)
				is Double -> putFloat(key, value.toFloat())
				is Boolean -> putBoolean(key, value)
				is String -> putString(key, value)
			}
			apply()
		}
	}
	
	fun saveConfig(context: Context) {
	}
	
	private fun <T> throwNoDefault(key: String): T {
		throw IllegalArgumentException("settings key not found: $key")
	}
	
	object Key {
		const val LANG = "lang"
		const val LINK_ON_START = "link_on_start"
		const val TIME_FORMAT = "time_format"
		const val SHOW_CLOSED_SHOP = "shop.show_closed"
		const val DISTANCE_UNIT = "shop.dist_unit"
	}
	
}