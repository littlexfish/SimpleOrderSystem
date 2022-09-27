package edu.nptu.dllab.sos.test.net

import android.os.Handler
import java.io.ByteArrayOutputStream
import java.net.URL
import java.net.URLConnection

class HttpConnectTest(private var handler: Handler? = null) {
	
	private var http: URLConnection? = null
	
	fun getData(url: String, write: ByteArray?): ByteArray {
		http = URL(url).openConnection()
		if(http != null) {
			if(write != null) {
				http?.outputStream?.write(write)
			}
			val buffer = ByteArrayOutputStream()
			val bs = ByteArray(65536)
			var b = http?.inputStream?.read(bs)
			while(b != null || b != -1) {
				buffer.write(bs, 0, b ?: 0)
				b = http?.inputStream?.read(bs)
			}
			return buffer.toByteArray()
		}
		return ByteArray(0)
	}
	
	
	
}