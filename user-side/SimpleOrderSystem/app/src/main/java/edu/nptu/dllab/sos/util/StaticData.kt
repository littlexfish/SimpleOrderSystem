package edu.nptu.dllab.sos.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import edu.nptu.dllab.sos.io.SocketHandler
import edu.nptu.dllab.sos.io.Translator
import java.net.ConnectException
import java.net.SocketException

/**
 * A util class use for hold static data
 */
@SOSVersion(since = "0.0")
object StaticData {
	
	/**
	 * The socket handler of now processing
	 */
	@SOSVersion(since = "0.0")
	lateinit var socketHandler: SocketHandler
	
	/**
	 * 50872(dllab-lan): 192.168.0.235
	 * lf(dllab-lan): 192.168.0.67
	 * lf(dllab): 203.64.134.96
	 */
	var ip = "192.168.0.235"
	var port = 25000
	
	fun ensureSocketHandler(context: Context) {
		try {
			if(!::socketHandler.isInitialized) newSocketHandler()
			if(!socketHandler.isConnected()) newSocketHandler()
		}
		catch(e: SocketException) {
			Log.e("network", "error", e)
			if(e is ConnectException) {
				Toast.makeText(context, Translator.getString("net.connect.timeout"), Toast.LENGTH_SHORT).show()
			}
			else if(e.message == "Network is unreachable") {
				Toast.makeText(context, Translator.getString("net.connect.unreachable"), Toast.LENGTH_SHORT).show()
			}
			throw e
		}
	}
	
	private fun newSocketHandler() {
		socketHandler = SocketHandler()
		socketHandler.link(ip, port)
	}
	
}