package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Util.DownloadKey
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to request download
 *
 * @author Little Fish
 */
class DownloadRequestEvent : Event(EVENT_KEY), EventPusher {
	
	private val downloadRes = ArrayList<String>()
	
	/**
	 * Add path to download
	 */
	fun addPath(path: String) {
		downloadRes.add(path)
	}
	
	/**
	 * Add multi-path
	 */
	fun addAllPath(paths: Iterable<String>) {
		downloadRes.addAll(paths)
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		val paths = ArrayList<Value>(downloadRes.size)
		for(p in downloadRes) paths.add(p.toStringValue())
		map[DownloadKey.PATH.toStringValue()] = ValueFactory.newArray(paths)
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return "download { paths=${downloadRes.joinToString(", ", "[", "]")} }"
	}
	
	companion object {
		const val EVENT_KEY = "download"
	}
	
}