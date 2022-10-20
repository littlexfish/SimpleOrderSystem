package edu.nptu.dllab.sos.data

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.DownloadKey.*
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to request download
 *
 * @author Little Fish
 * @since 22/10/04
 */
@SOSVersion(since = "0.0")
class DownloadRequestEvent : Event(EVENT_KEY), EventPusher {
	
	private val downloadRes = ArrayList<String>()
	
	/**
	 * Add path to download
	 */
	@SOSVersion(since = "0.0")
	fun addPath(path: String) {
		downloadRes.add(path)
	}
	
	fun addAllPath(paths: Iterable<String>) {
		downloadRes.addAll(paths)
	}
	
	override fun toJson(): JsonElement {
		val json = JsonObject()
		json.addProperty(NET_KEY_EVENT, EVENT_KEY)
		val paths = JsonArray()
		for(p in downloadRes) paths.add(p)
		json.add(PATH.key, paths)
		return json
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		val paths = ArrayList<Value>(downloadRes.size)
		for(p in downloadRes) paths.add(p.toStringValue())
		map[PATH.key.toStringValue()] = ValueFactory.newArray(paths)
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return toValue().toString()
	}
	
	companion object {
		const val EVENT_KEY = "download"
	}
	
}