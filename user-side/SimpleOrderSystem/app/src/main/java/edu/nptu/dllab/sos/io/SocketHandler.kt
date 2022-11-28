package edu.nptu.dllab.sos.io

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.data.pull.*
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toByteArray
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

private const val KEY_EVENT = "event"

private const val EVENT_PUSH_LINK = "link"
private const val EVENT_PULL_NEAR_SHOP = "near_shop"
private const val EVENT_PUSH_OPEN_MENU = "open_menu"
private const val EVENT_PULL_UPDATE = "update"
private const val EVENT_PULL_EVENT_MENU = "event_menu"
private const val EVENT_PUSH_DOWNLOAD = "download"
private const val EVENT_PULL_RESOURCE = "resource"
private const val EVENT_ORDER_REQUEST = "order_request"
private const val EVENT_ORDER_STATUS = "order_status"
private const val EVENT_PULL_ERROR = "error"

/**
 * A class handle socket that can process event functionally
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class SocketHandler {
	
	private val tag = "network"
	
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
	
	private var ip = ""
	private var port = 0
	
	private val threadLink = HandlerThread("net-link").also { it.start() }
	private val threadPush = HandlerThread("net-push").also { it.start() }
	private val threadPull = HandlerThread("net-pull").also { it.start() }
	private val handlerLink = Handler(threadLink.looper)
	private val handlerPush = Handler(threadPush.looper)
	private val handlerPull = Handler(threadPull.looper)
	
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
		this.ip = ip
		this.port = port
		connected = true
	}
	
	fun linkAndRun(ip: String, port: Int, func: ((handler: SocketHandler) -> Unit)?,
	               error: ((e: Exception) -> Unit)? = null) {
		handlerLink.post {
			try {
				link(ip, port)
				func?.let { it(this) }
			}
			catch(e: Exception) {
				if(error == null) {
					throw e
				}
				else {
					error(e)
				}
			}
		}
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
	fun pushEvent(event: EventPusher, error: ((e: Exception) -> Unit)? = null) {
		handlerPush.post {
			try {
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
				packer.close()
				val bs = buffer.toByteArray()
				Log.d(tag, "push ${(event as Event).event}")
				if(socket != null) {
					val os = socket!!.getOutputStream()
					// write byte size first
					os.write(bs.size.toByteArray())
					// than write data
					os.write(bs)
				}
			}
			catch(e: Exception) {
				if(error == null) throw e
				else error(e)
			}
		}
	}
	
	fun pushEventRePush(event: EventPusher) {
		pushEvent(event) {
			if(it is SocketException) {
				Log.w(tag, "error", it)
				Log.w(tag, "re-push")
				linkAndRun(ip, port, { pushEventRePush(event) }) { e ->
					throw e
				}
			}
			else throw it
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
				Log.d(tag, "found hold event: ${(tmp as Event).event}")
				return tmp
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
			val event = getEventValue(unpacker.unpackValue())
			Log.d(tag, "receive ${(event as Event).event}")
			return event
		}
		// may not reach here
		throw RuntimeException()
	}
	
	fun waitEventAndRun(func: (e: EventPuller) -> Unit) {
		waitEventAndRun(func, null)
	}
	
	fun waitEventAndRun(func: (e: EventPuller) -> Unit, error: ((e: Exception) -> Unit)?) {
		handlerPull.post {
			try {
				val e = waitEvent()
				func(e)
			}
			catch(e: Exception) {
				if(error == null) throw e
				else error(e)
			}
		}
	}
	
	/**
	 * Wait event until the event is correct.
	 * This method will block the process.
	 * Auto process error event.
	 *
	 * @param eClass The class of event you want
	 * @param tryTimes The max times this method try
	 *
	 * @throws IllegalStateException When try too many times and got error event
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T : EventPuller> waitEvent(context: Context, eClass: Class<T>, tryTimes: Int = 5): T {
		for(i in 0 until tryTimes) {
			val e = waitEvent()
			if(e.javaClass == eClass) return e as T
			if(checkErrorAndThrow(context, e as Event)) {
				throw IllegalStateException("got error event")
			}
		}
		throw IllegalStateException("event type not found, try count: $tryTimes")
	}
	
	fun runOnNetworkThread(func: () -> Unit) {
		handlerLink.post(func)
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
		if(!connected || socket == null || !socket!!.isConnected) {
			Log.w(tag, "warning", IllegalStateException("socket not connect."))
			link(ip, port)
		}
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
			EVENT_ORDER_REQUEST -> OrderRequest()
			EVENT_ORDER_STATUS -> OrderStatus()
			EVENT_PULL_ERROR -> Error()
			else -> throw Exceptions.EventNotFoundException(map[KEY_EVENT.toStringValue()]?.asString() ?: "null")
		}
		e.fromValue(value)
		return e
	}
	
	fun close() {
		socket?.close()
	}
	
	companion object {
		fun checkErrorAndThrow(context: Context, evt: Event): Boolean {
			if(evt is Error) {
				Toast.makeText(context, Translator.getString("net.event.error.${evt.reason}").format(evt.format), Toast.LENGTH_SHORT).show()
				return true
			}
			return false
		}
	}
	
}