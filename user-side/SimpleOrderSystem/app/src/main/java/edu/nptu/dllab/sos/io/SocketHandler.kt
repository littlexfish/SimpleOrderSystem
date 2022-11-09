package edu.nptu.dllab.sos.io

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import edu.nptu.dllab.sos.data.*
import edu.nptu.dllab.sos.data.pull.EventMenu
import edu.nptu.dllab.sos.data.pull.NearShop
import edu.nptu.dllab.sos.data.pull.ResourceDownload
import edu.nptu.dllab.sos.data.pull.UpdateMenu
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
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
	@SOSVersion(since = "0.0")
	private var connected = false
	
	/**
	 * The network socket
	 */
	@SOSVersion(since = "0.0")
	private var socket: Socket? = null
	
	/**
	 * Uses charset, default UTF-8
	 */
	@SOSVersion(since = "0.0")
	var charset = Charsets.UTF_8
	
	/**
	 * The last event which not process
	 */
	@SOSVersion(since = "0.0")
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
		/*
		 * FISH NOTE:
		 *   To parse data easily,
		 *    we write data size first,
		 *    than write data.
		 */
		checkConnectedState()
		val buffer = ByteArrayOutputStream()
		val v = event.toValue()
		val packer = MessagePack.newDefaultPacker(buffer)
		packer.packValue(v)
		packer.flush()
		val bs = buffer.toByteArray()
		if(socket != null) {
			val os = socket!!.getOutputStream()
			// write byte size first
			os.write(bs.size.toByteArray())
			// than write data
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
		/*
		 * FISH NOTE:
		 *   Because write data size first,
		 *    we read 4 bytes and change to int type,
		 *    than read the "int" size of data
		 */
		checkConnectedState()
		if(socket != null) { // not null when check connect state
			if(holdEvent != null) { // check hold event and return first
				val tmp = holdEvent
				holdEvent = null
				return tmp!!
			}
			val ins = socket!!.getInputStream()
			
			// get the pack size, use 4 bytes
			val intBs = ByteBuffer.allocate(4)
			for(i in 0 until 4) {
				intBs.put(ins.read().toByte())
			}
			val size = intBs.getInt(0)
			
			// force read to size of pack
			val bos = ByteArrayOutputStream()
			for(i in 0 until size) {
				bos.write(ins.read())
			}
			
			// unpack from byte array
			val unpacker = MessagePack.newDefaultUnpacker(ByteArrayInputStream(bos.toByteArray()))
			return getEventValue(unpacker.unpackValue())
		}
		// may not reach here
		throw RuntimeException()
	}
	
	/**
	 * Hold the event
	 */
	@SOSVersion(since = "0.0")
	fun holdEvent(event: EventPuller) {
		holdEvent = event
	}
	
	/**
	 * Check the connect state, and throw if not connect
	 */
	@SOSVersion(since = "0.0")
	private fun checkConnectedState() {
		if(connected || socket == null) throw IllegalStateException("socket not connect.")
	}
	
	/**
	 * Get event object from value
	 * @param value - [Value]
	 * @return [EventPuller]
	 */
	@SOSVersion(since = "0.0")
	private fun getEventValue(value: Value): EventPuller {
		val map = Util.checkMapValue(value).map()
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
	
	fun close() {
		socket?.close()
	}
	
}