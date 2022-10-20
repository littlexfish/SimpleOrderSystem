package edu.nptu.dllab.sos.io

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import edu.nptu.dllab.sos.data.*
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toByteArray
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.nio.ByteBuffer

private const val KEY_EVENT = "event"

private const val EVENT_PUSH_LINK = "link"
private const val EVENT_PULL_NEAR_SHOP = "near_shop"
private const val EVENT_PUSH_OPEN_MENU = "open_menu"
private const val EVENT_PULL_UPDATE = "update"
private const val EVENT_PULL_EVENT_MENU = "event_menu"
private const val EVENT_PUSH_DOWNLOAD = "download"
private const val EVENT_PULL_RESOURCE = "resource"

/**
 * A class handle socket that can process event functionally
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class SocketHandler {
	
	/**
	 * Check the connect state
	 */
	private var connected = false
	
	/**
	 * The network socket
	 */
	private var socket: Socket? = null
	
	/**
	 * Uses format, json or msgpack
	 */
	var useFormat = Format.JSON
	
	/**
	 * Uses charset, default UTF-8
	 */
	var charset = Charsets.UTF_8
	
	private val tmpBuffer = ByteArrayOutputStream()
	
	private var holdEvent: EventPuller? = null
	
	/**
	 * Link to server
	 */
	@SOSVersion(since = "0.0")
	fun link(ip: String, port: Int) {
		socket = Socket(ip, port)
		connected = true
	}
	
	/**
	 * @return true if is connected
	 */
	@SOSVersion(since = "0.0")
	fun isConnected() = connected && socket != null
	
	/**
	 * Push event via socket
	 * @param event - event to push
	 */
	@SOSVersion(since = "0.0")
	@Synchronized
	fun pushEvent(event: EventPusher) {
		checkConnectedState()
		val bs = when(useFormat) {
			Format.JSON -> {
				event.toJson().toString().toByteArray(charset)
			}
			Format.MSG_PACK -> {
				tmpBuffer.reset()
				val v = event.toValue()
				val packer = MessagePack.newDefaultPacker(tmpBuffer)
				packer.packValue(v)
				packer.flush()
				tmpBuffer.toByteArray()
			}
		}
		if(socket != null) {
			val os = socket!!.getOutputStream()
			if(useFormat == Format.MSG_PACK) {
				os.write(bs.size.toByteArray())
			}
			os.write(bs)
		}
	}
	
	/**
	 * Get event via socket, will block the thread
	 * @return event socket read
	 */
	@SOSVersion(since = "0.0")
	@Synchronized
	fun waitEvent(): EventPuller {
		checkConnectedState()
		if(socket != null) {
			if(holdEvent != null) {
				val tmp = holdEvent
				holdEvent = null
				return tmp!!
			}
			val ins = socket!!.getInputStream()
			when(useFormat) {
				Format.JSON -> {
					val bos = ByteArrayOutputStream()
					var br = 0
					while(true) {
						val c = ins.read()
						// use '{' and '}' to quick check json format
						if(c == '{'.code) br++
						if(c == '}'.code) br--
						bos.write(c)
						// a whole json, so return
						if(br == 0) break
					}
					val e = getEventJson(JsonParser.parseString(String(bos.toByteArray(), charset)))
					if(e is ResourceDownload) {
						TODO("Read extra bytes that contains resource data")
					}
					return e
				}
				Format.MSG_PACK -> {
					// get the pack size, use 4 bytes
					val intBs = ByteBuffer.allocate(4)
					for(i in 0 until 4) {
						intBs.put(ins.read().toByte())
					}
					val size = intBs.int
					// force read to size of pack
					val bos = ByteArrayOutputStream()
					for(i in 0 until size) {
						bos.write(ins.read())
					}
					// unpack from byte array
					val unpacker = MessagePack.newDefaultUnpacker(ByteArrayInputStream(bos.toByteArray()))
					return getEventValue(unpacker.unpackValue())
				}
			}
		}
		// may not reach here
		throw RuntimeException()
	}
	
	fun holdEvent(event: EventPuller) {
		holdEvent = event
	}
	
	/**
	 * Check the connect state, and throw if not connect
	 */
	@SOSVersion(since = "0.0")
	private fun checkConnectedState() {
		if(connected || socket == null) throw RuntimeException("socket not connect.")
	}
	
	/**
	 * Get event object from json
	 * @param json - [JsonElement]
	 * @return [EventPuller]
	 */
	@SOSVersion(since = "0.0")
	private fun getEventJson(json: JsonElement): EventPuller {
		if(!json.isJsonObject) throw Exceptions.DataFormatException("event not object type")
		val obj = json.asJsonObject
		val e = when(obj[KEY_EVENT].asString) {
			EVENT_PULL_NEAR_SHOP -> NearShop()
			EVENT_PULL_UPDATE -> UpdateMenu()
			EVENT_PULL_EVENT_MENU -> EventMenu()
			EVENT_PULL_RESOURCE -> ResourceDownload()
			else -> throw Exceptions.EventNotFoundException(obj[KEY_EVENT].asString)
		}
		e.fromJson(json)
		return e
	}
	
	/**
	 * Get event object from value
	 * @param value - [Value]
	 * @return [EventPuller]
	 */
	@SOSVersion(since = "0.0")
	private fun getEventValue(value: Value): EventPuller {
		if(!value.isMapValue) throw Exceptions.DataFormatException("event not map type")
		val map = value.asMap()
		val e = when(map[KEY_EVENT.toStringValue()]?.asString()) {
			EVENT_PULL_NEAR_SHOP -> NearShop()
			EVENT_PULL_UPDATE -> UpdateMenu()
			EVENT_PULL_EVENT_MENU -> EventMenu()
			EVENT_PULL_RESOURCE -> ResourceDownload()
			else -> throw Exceptions.EventNotFoundException(map[KEY_EVENT.toStringValue()]?.asString() ?: "null")
		}
		e.fromValue(value)
		return e
	}
	
	/**
	 * The transfer format
	 */
	@SOSVersion(since = "0.0")
	enum class Format {
		JSON, MSG_PACK
	}
	
}