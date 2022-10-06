package edu.nptu.dllab.sos

import java.net.Socket
import java.net.URL
import java.util.*

fun main() {
	TestClient().runTcpClient()
}

class TestClient {
	
	fun runTcpClient() {
		val socket = Socket("192.168.0.235", 25000)
		val sc = Scanner(System.`in`)
		Thread {
			val buffer = ByteArray(Int.MAX_VALUE shr 2)
			while(true) {
				val size = socket.getInputStream().read(buffer)
				println("From server: ${String(buffer, 0, size, Charsets.UTF_8)}")
			}
		}.start()
		while(true) {
			val line = sc.nextLine()
			socket.getOutputStream().write(line.toByteArray(Charsets.UTF_8))
		}
	}
	
	fun runHttpClient() {
		val sc = Scanner(System.`in`)
		val buffer = ByteArray(65536)
		while(true) {
			val line = sc.nextLine()
			val http = URL("http://127.0.0.1:25000/").openConnection()
			http.doOutput = true
			http.getOutputStream().write(line.toByteArray(Charsets.UTF_8))
			val size = http.getInputStream().read(buffer)
			println(String(buffer, 0, size, Charsets.UTF_8))
		}
	}
	
}