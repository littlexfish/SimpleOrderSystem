package edu.nptu.dllab.sos

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.ServerSocket

fun main() {
	TestServer().runTcpServer()
}

class TestServer {
	
	fun runTcpServer() {
		val socketServer = ServerSocket(25000)
		val socket = socketServer.accept()
		val buffer = ByteArray(65536)
		while(true) {
			val size = socket.getInputStream().read(buffer)
			println(String(buffer, 0, size, Charsets.UTF_8))
		}
	}
	
	fun runHttpServer() {
		val http = HttpServer.create(InetSocketAddress("127.0.0.1", 25000), 0)
		http.createContext("/").setHandler {
			val inS = it.requestBody
			val buffer = ByteArray(65536)
			val size = inS.read(buffer)
			println(String(buffer, 0, size, Charsets.UTF_8))
			it.sendResponseHeaders(200, size.toLong())
			it.responseBody.write(buffer.copyOfRange(0, size))
			it.close()
		}
		http.start()
	}
	
}