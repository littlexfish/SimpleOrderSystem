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

/**
 * A class handle socket that can process event functionally
 *
 * @author Little Fish
 */
class SocketHandler {
	
	private val tag = "network"
	
	/**
	 * Check the connect state
	 */
	private var connected = false
	
	/**
	 * The network socket
	 */
	private var socket: Socket? = null
	
	/**
	 * The old ip
	 */
	private var ip = ""
	
	/**
	 * The old port
	 */
	private var port = 0
	
	/**
	 * The thread for link and other network
	 */
	private val threadLink = HandlerThread("net-link").also { it.start() }
	
	/**
	 * The thread for push event
	 */
	private val threadPush = HandlerThread("net-push").also { it.start() }
	
	/**
	 * The thread for pull event
	 */
	private val threadPull = HandlerThread("net-pull").also { it.start() }
	
	/**
	 * The handler for link and other network
	 *
	 */
	private val handlerLink = Handler(threadLink.looper)
	
	/**
	 * The handler for push event
	 */
	private val handlerPush = Handler(threadPush.looper)
	
	/**
	 * The handler for pull event
	 */
	private val handlerPull = Handler(threadPull.looper)
	
	/**
	 * Uses charset, default UTF-8
	 */
	var charset = Charsets.UTF_8
	
	/**
	 * The last event which not process
	 */
	private var holdEvent: EventPuller? = null
	
	/**
	 * Link to server
	 */
	fun link(ip: String, port: Int) {
		socket = Socket(ip, port)
		this.ip = ip
		this.port = port
		connected = true
	}
	
	/**
	 * Link to server and run function on network thread, or run error function on got exception
	 */
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
	fun isConnected() = connected && socket != null
	
	/**
	 * Push event via socket
	 * @param event - event to push
	 */
	@Synchronized
	fun pushEvent(event: EventPusher, error: ((e: Exception) -> Unit)? = null) {
		handlerPush.post {
			try {
				pushEventNoHandler(event)
			}
			catch(e: Exception) {
				if(error == null) throw e
				else error(e)
			}
		}
	}
	
	/**
	 * Push event with no handler
	 */
	fun pushEventNoHandler(event: EventPusher) {
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
		Log.d(tag, "push $event")
		if(socket != null) {
			val os = socket!!.getOutputStream()
			// write byte size first
			os.write(bs.size.toByteArray())
			// than write data
			os.write(bs)
		}
	}
	
	/**
	 * Push event and auto re-push when got error
	 */
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
	@Synchronized
	fun waitEvent(): EventPuller {
		while(true) {
			val e = waitEventOrNull()
			if(e != null)
				return e
		}
	}
	
	/**
	 * Get event via socket, return `null` if no new event or some error
	 */
	fun waitEventOrNull(): EventPuller? {
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
				Log.d(tag, "found hold event: $tmp")
				return tmp
			}
			val ins = socket!!.getInputStream()
			if(ins.available() == 0) return null
			
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
			Log.d(tag, "receive $event")
			return event
		}
		// may not reach here
		return null
	}
	
	/**
	 * Get event and run function
	 */
	fun waitEventAndRun(func: (e: EventPuller) -> Unit) {
		waitEventAndRun(func, null)
	}
	
	/**
	 * Get event and run function, or run error function when got event
	 */
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
	
	/**
	 * Run something on network thread
	 */
	fun runOnNetworkThread(func: () -> Unit) {
		handlerLink.post(func)
	}
	
	/**
	 * Hold the event
	 */
	fun holdEvent(event: EventPuller) {
		holdEvent = event
	}
	
	/**
	 * Check the connect state, and throw if not connect
	 */
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
	private fun getEventValue(value: Value): EventPuller {
		val map = Util.checkMapValue(value).map()
		val e = when(val eId = map[Util.NET_KEY_EVENT.toStringValue()]?.asString()) {
			NearShop.EVENT_KEY -> NearShop()
			UpdateMenu.EVENT_KEY -> UpdateMenu()
			EventMenu.EVENT_KEY -> EventMenu()
			ResourceDownload.EVENT_KEY -> ResourceDownload()
			OrderRequest.EVENT_KEY -> OrderRequest()
			OrderStatus.EVENT_KEY -> OrderStatus()
			Error.EVENT_KEY -> Error()
			OrderReceive.EVENT_KEY -> OrderReceive()
			else -> throw Exceptions.EventNotFoundException(if(eId != null) map[eId.toStringValue()]?.asString() ?: "null" else "null")
		}
		e.fromValue(value)
		return e
	}
	
	/**
	 * Close socket
	 */
	fun close() {
		socket?.close()
	}
	
	companion object {
		/**
		 * Check error and throw when got error
		 */
		fun checkErrorAndThrow(context: Context, evt: Event): Boolean {
			if(evt is Error) {
				Toast.makeText(context, Translator.getString("net.event.error.${evt.reason}").format(evt.format), Toast.LENGTH_SHORT).show()
				return true
			}
			return false
		}
	}
	
}