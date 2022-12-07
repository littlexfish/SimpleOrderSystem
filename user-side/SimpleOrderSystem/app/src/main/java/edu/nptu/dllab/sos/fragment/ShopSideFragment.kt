package edu.nptu.dllab.sos.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.ShopSideActivity
import edu.nptu.dllab.sos.ShopSideOrderActivity
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.data.pull.OrderReceive
import edu.nptu.dllab.sos.data.push.UpdateStatusEvent
import edu.nptu.dllab.sos.data.shop.Order
import edu.nptu.dllab.sos.databinding.FragmentShopSideBinding
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.FileIO
import edu.nptu.dllab.sos.io.SocketHandler
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.view.ShopSideOrder
import kotlinx.coroutines.delay
import org.msgpack.core.MessagePack
import java.net.SocketException
import java.util.LinkedList

/**
 * The fragment main on shop side
 *
 * @author Little Fish
 */
class ShopSideFragment : Fragment() {
	
	private lateinit var binding: FragmentShopSideBinding
	
	/**
	 * The shop id
	 */
	private var shopId = -1
	
	/**
	 * The max of horizon size
	 */
	private var hMaxSize = 2
	
	/**
	 * All of order
	 */
	private val items: HashMap<ULong, Order> = HashMap()
	
	/**
	 * The order wait for process
	 */
	private val orderWait = LinkedList<Order>()
	
	
	/**
	 * The order dialog wait shop side user interact
	 */
	private lateinit var orderDialog: AlertDialog
	
	/**
	 * `true` if dialog is showing
	 */
	private var isDialogShow = false
	
	/**
	 * The launcher launch the order detail
	 */
	private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		if(it.data == null) return@registerForActivityResult
		val oId = it.data!!.extras!!.getInt(ShopSideOrderActivity.EXTRA_ORDER_ID)
		when(it.resultCode) {
			ShopSideOrderActivity.RESULT_REQUEST -> {
				val event = UpdateStatusEvent()
				event.orderId = oId
				event.status = "request"
				pushEvent(event)
			}
			ShopSideOrderActivity.RESULT_DONE -> {
				for(k in items.keys) {
					val v = items[k]!!
					if(v.id == oId) {
						items.remove(k)
						break
					}
				}
				val event = UpdateStatusEvent()
				event.orderId = oId
				event.status = "done"
				pushEvent(event)
			}
			else -> {
				// do nothing
			}
		}
	}
	
	/**
	 * The timer force update view
	 */
	private val forceTimer = object : CountDownTimer(60 * 1000, 0) {
		var isStart = false
		fun cStart() {
			start()
			isStart = true
		}
		override fun onTick(millisUntilFinished: Long) {
			// do nothing
		}
		override fun onFinish() {
			activity?.runOnUiThread {
				buildViews()
			}
		}
	}
	
	/**
	 * The timer for update view when get event
	 */
	private val timer = object : CountDownTimer(1000, 1000) {
		override fun onTick(millisUntilFinished: Long) {
			// do nothing
		}
		override fun onFinish() {
			activity?.runOnUiThread {
				buildViews()
				forceTimer.cancel()
			}
		}
	}
	
	/**
	 * Use for stop all things
	 */
	private var stop = false
	private val socketHandler = SocketHandler()
	private val thread = HandlerThread("network").also { it.start() }
	private val handler = Handler(thread.looper)
	
	/**
	 * The runnable for wait next event
	 */
	private var waitNextEvent = Runnable {
		if(stop) return@Runnable
		/*
		 * FISH NOTE:
		 *   Auto reconnect to server because shop side must
		 *    always wait event from server
		 */
		if(!socketHandler.isConnected()) {
			try {
				socketHandler.link(StaticData.ip, StaticData.shopPort)
			}
			catch(e: SocketException) {
				runNetworkGetEvent(0L)
				return@Runnable
			}
		}
		val e = socketHandler.waitEventOrNull()
		/*
		 * FISH NOTE:
		 *   We wait for 1 second to prevent network thread load
		 *    too frequency because the network thread may need
		 *    send event and the thread not block by receive event.
		 */
		if(e == null) {
			runNetworkGetEvent(1000)
			return@Runnable
		}
		/*
		 * FISH NOTE:
		 *   We consume other event because the shop side
		 *    do not use user side event.
		 */
		if(e is OrderReceive) {
			activity?.let {
				it.runOnUiThread {
					orderWait.push(e.order)
					requestOpenOrderDialog()
				}
			}
		}
		/*
		 * FISH NOTE:
		 *   we receive event immediately when get last event
		 *    because may has next event soon.
		 */
		runNetworkGetEvent(0)
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentShopSideBinding.inflate(inflater)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		getMenu()
		buildViews()
		thread.start()
	}
	
	/**
	 * Request open order dialog
	 */
	private fun requestOpenOrderDialog() {
		if(isDialogShow) return
		if(orderWait.isNotEmpty()) {
			isDialogShow = true
			val order = orderWait.pop()
			orderDialog = AlertDialog.Builder(requireActivity())
				.setCancelable(false)
				.setMessage(Translator.getString("shopSide.order.receive").format(order.id.toString()))
				.setPositiveButton(Translator.getString("shopSide.order.accept")) { _, _ ->
					val ordering = getNowId()
					items[ordering] = order
					timer.start()
					if(!forceTimer.isStart) {
						forceTimer.cStart()
					}
					
					val event = UpdateStatusEvent()
					event.orderId = order.id
					event.status = "request"
					pushEvent(event)
					
					requestOpenOrderDialog()
				}
				.setNegativeButton(Translator.getString("shopSide.order.deny")) { _, _ ->
					val event = UpdateStatusEvent()
					event.orderId = order.id
					event.status = "not_request"
					pushEvent(event)
					
					requestOpenOrderDialog()
				}
				.create()
			orderDialog.show()
		}
		else isDialogShow = false
	}
	
	/**
	 * Finish the fragment
	 */
	private fun finish() {
		stop = true
		(activity as? ShopSideActivity)?.changeFrag(ShopStartFragment())
	}
	
	/**
	 * Get menu
	 */
	private fun getMenu() {
		/*
		 * FISH NOTE:
		 *   Get information on database
		 */
		val db = DBHelper(requireContext())
		val cur = db.select(DBHelper.TABLE_MENU, where = "${DBColumn.MENU_SHOP_ID.columnName}=$shopId", limit = 1)
		if(cur.count <= 0) { // no any menu
			cur.close()
			finish()
		}
		else {
			cur.close()
			val file = FileIO.getMenuFile(requireContext(), shopId)
			val inS = file.inputStream()
			val unpacker = MessagePack.newDefaultUnpacker(inS)
			val value = unpacker.unpackValue()
			
			val m = ClassicMenu(shopId, -1)
			m.buildMenu(Util.checkMapValue(value))
			classicMenu = m
		}
	}
	
	/**
	 * Request run event
	 */
	private fun runNetworkGetEvent(delay: Long = 0L) {
		suspend {
			delay(delay)
			handler.post(this.waitNextEvent)
		}
	}
	
	/**
	 * Push event via socket
	 */
	private fun pushEvent(event: EventPusher) {
		handler.post {
			try {
				if(!socketHandler.isConnected()) {
					socketHandler.link(StaticData.ip, StaticData.shopPort)
				}
				socketHandler.pushEventNoHandler(event)
			}
			catch(e: SocketException) {
				pushEvent(event)
				return@post
			}
		}
	}
	
	/**
	 * Build views
	 */
	private fun buildViews() {
		if(!::binding.isInitialized || classicMenu == null) return
		val wdp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
		hMaxSize = (wdp / 100).toInt()
		
		val values = ArrayList<Order>()
		val keys = ArrayList(items.keys)
		keys.sort()
		
		for(k in keys) values.add(items[k]!!)
		
		val vSize = values.size / hMaxSize + 1
		
		val param = LinearLayout.LayoutParams(-1, -2)
		for(v in 0 until vSize) {
			val hLinear = LinearLayout(requireContext())
			hLinear.layoutParams = param
			binding.shopSideVContainer.addView(hLinear)
			
			for(h in 0 until hMaxSize) {
				val index = v * hMaxSize + h
				if(index >= values.size) {
					val text = TextView(context)
					text.layoutParams = param
					hLinear.addView(text)
				}
				else {
					val order = values[index]
					val orderView = ShopSideOrder(requireContext(), order.id, order.items)
					orderView.layoutParams = param
					orderView.setOnClickListener {
						ShopSideOrderActivity.order = order
						launcher.launch(Intent(requireContext(), ShopSideOrderActivity::class.java))
					}
					hLinear.addView(orderView)
				}
			}
		}
	}
	
	/**
	 * Put back data when rotate screen
	 */
	fun putBackData(savedInstanceState: Bundle) {
		shopId = savedInstanceState.getInt(STATE_SHOP_ID)
		val keys = savedInstanceState.getLongArray(STATE_ITEM_KEYS)!!
		val values = savedInstanceState.getParcelableArrayList<Order>(STATE_ITEM_VALUES)!!
		
		for(k in keys.indices) {
			items[keys[k].toULong()] = values[k]
		}
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		val keys = ArrayList<ULong>(items.keys)
		val values = ArrayList<Order>()
		for(k in keys) values.add(items[k]!!)
		outState.putLongArray(STATE_ITEM_KEYS, keys.map { it.toLong() }.toLongArray())
		outState.putParcelableArrayList(STATE_ITEM_VALUES, values)
	}
	
	companion object {
		var classicMenu: ClassicMenu? = null
		private var ids = 0uL
		private fun getNowId() = ids++
		
		private const val STATE_SHOP_ID = "shopId"
		private const val STATE_ITEM_KEYS = "itemKeys"
		private const val STATE_ITEM_VALUES = "itemValues"
	}
	
}