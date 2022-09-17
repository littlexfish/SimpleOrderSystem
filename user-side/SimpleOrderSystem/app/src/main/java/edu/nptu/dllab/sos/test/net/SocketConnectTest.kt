package edu.nptu.dllab.sos.test.net

import android.os.Handler
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SocketConnectTest(private var handler: Handler? = null) {
	
	private var socket: Socket? = null
	val inputStream: InputStream?
		get() = if(isConnect()) socket?.getInputStream() else null
	val outputStream: OutputStream?
		get() = if(isConnect()) socket?.getOutputStream() else null
	
	fun link(ip: String, port: Int) {
		try {
			socket = Socket(ip, port)
		}
		catch(e: Exception) {
			socket = null
			throw e
		}
	}
	
	fun setHandler(handler: Handler?) {
		this.handler = handler
	}
	
	fun isConnect(): Boolean = socket?.isConnected == true
	
}