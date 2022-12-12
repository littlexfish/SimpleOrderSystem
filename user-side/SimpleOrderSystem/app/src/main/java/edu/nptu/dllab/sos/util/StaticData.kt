package edu.nptu.dllab.sos.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import edu.nptu.dllab.sos.data.menu.OrderItem
import edu.nptu.dllab.sos.io.SocketHandler
import edu.nptu.dllab.sos.io.Translator
import java.net.ConnectException

/**
 * A util class use for hold static data
 */
object StaticData {
	
	/**
	 * The socket handler of now processing
	 */
	var socketHandler: SocketHandler = SocketHandler()
	
	/**
	 * 50872(dllab-lan): 192.168.0.235
	 * 50872(dllab): 203.64.134.152
	 * lf(dllab-lan): 192.168.0.67
	 * lf(dllab): 203.64.134.96
	 * 10.0.35.52
	 */
	var ip = "10.0.35.52"
	var port = 25000
	var shopPort = 25001
	
	private val itemList = ArrayList<OrderItem>()
	
	/**
	 * Ensure the socket handler is connected.
	 * If not connect, it will auto use [ip] and [port]
	 *  to create socket handler
	 */
	fun ensureSocketHandler(context: Context, func: ((handler: SocketHandler) -> Unit)? = null, error: ((e: Exception) -> Unit)? = null) {
		if(!socketHandler.isConnected()) {
			newSocketHandler(func) { e ->
				error?.let { it(e) }
				Log.e("network", "error", e)
				if(e is ConnectException) {
					Toast.makeText(context, Translator.getString("net.connect.timeout"), Toast.LENGTH_SHORT).show()
				}
				else if(e.message == "Network is unreachable") {
					Toast.makeText(context, Translator.getString("net.connect.unreachable"), Toast.LENGTH_SHORT).show()
				}
				else {
					throw e
				}
			}
		}
		else func?.let { socketHandler.runOnNetworkThread { it(socketHandler) } }
	}
	
	/**
	 * New socket handler and link to remote server.
	 * It will throw any exception when it error on any time
	 */
	private fun newSocketHandler(func: ((handler: SocketHandler) -> Unit)? = null, error: ((e: Exception) -> Unit)?) {
		socketHandler = SocketHandler()
		socketHandler.linkAndRun(ip, port, func, error)
	}
	
	/**
	 * Clear items in item list
	 */
	fun clearItems() {
		itemList.clear()
	}
	
	/**
	 * Add item into item list
	 */
	fun addItem(item: OrderItem) {
		itemList.add(item)
	}
	
	fun removeItem(item: OrderItem) {
		itemList.remove(item)
	}
	
	/**
	 * Get items duplicate from item list
	 */
	fun getItems() = ArrayList<OrderItem>(itemList)
	
}