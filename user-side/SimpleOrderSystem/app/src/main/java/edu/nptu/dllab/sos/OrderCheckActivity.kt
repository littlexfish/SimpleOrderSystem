package edu.nptu.dllab.sos

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import edu.nptu.dllab.sos.data.pull.OrderStatus
import edu.nptu.dllab.sos.data.push.TraceEvent
import edu.nptu.dllab.sos.databinding.ActivityOrderCheckBinding
import edu.nptu.dllab.sos.dialog.LoadingDialog
import edu.nptu.dllab.sos.io.Config
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBOrder
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.TimeFormat
import edu.nptu.dllab.sos.view.OrderCheckItemView
import java.text.SimpleDateFormat
import java.util.*

class OrderCheckActivity : AppCompatActivity() {
	
	private val orderMaxSize = 10000
	
	private lateinit var binding: ActivityOrderCheckBinding
	
	private val orders = ArrayList<DBOrder>()
	
	private var limit = 10
	private var offset = 0
	
	private var canNext = true
	private var canPre = false
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityOrderCheckBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.checkTitle.text = Translator.getString("check.title")
		
		val db = DBHelper(applicationContext)
		val cur = db.select(DBHelper.TABLE_ORDER, limit = orderMaxSize, orderBy = DBColumn.ORDER_TIME.columnName, asc = false)
		while(cur.moveToNext()) {
			orders.add(DBOrder(cur))
		}
		
		refreshList()
		
		binding.checkNextPage.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				binding.checkNextPage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				binding.checkNextPage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right, null))
			}
			false
		}
		binding.checkNextPage.setOnClickListener {
			nextPage()
		}
		binding.checkPrePage.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				binding.checkNextPage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				binding.checkNextPage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left, null))
			}
			false
		}
		binding.checkPrePage.setOnClickListener {
			prePage()
		}
		binding.checkBack.setOnClickListener { finish() }
		
		intent.extras?.let {
			if(it.containsKey(EXTRA_ORDER_ID)) {
				val id = it.getInt(EXTRA_ORDER_ID)
				showDefaultId(id)
			}
		}
		
	}
	
	private fun showDefaultId(id: Int) {
		val filtered = orders.filter { it.orderId == id }
		if(filtered.isEmpty()) return
		val order = filtered.first()
		val index = orders.indexOf(order)
		val pos = index % limit
		offset = (index / limit) * limit
		refreshList()
		binding.checkList[pos].callOnClick()
	}
	
	private fun refreshList() {
		buildOrderList(getSubList(offset, limit))
		canPre = offset - limit > -limit
		canNext = offset + limit <= orders.size
		binding.checkNextPage.isEnabled = canNext
		binding.checkPrePage.isEnabled = canPre
		binding.checkPage.text = "${offset / limit + 1} / ${orders.size / limit + 1}"
	}
	
	private fun getSubList(off: Int, len: Int): List<DBOrder> {
		val si = if(off >= orders.size) return emptyList() else off
		val ei = if(off + len >= orders.size) orders.size else off + len
		return ArrayList(orders.subList(si, ei))
	}
	
	private fun nextPage() {
		if(canNext) {
			offset += limit
			refreshList()
		}
	}
	
	private fun prePage() {
		if(canPre) {
			offset -= limit
			if(offset < 0) offset = 0
			refreshList()
		}
	}
	
	private fun buildOrderList(os: List<DBOrder>) {
		if(os.isNotEmpty()) {
			val timeFormat = SimpleDateFormat(TimeFormat.valueOf(Config.getString(Config.Key.TIME_FORMAT)).format, Locale.getDefault())
			binding.checkList.removeAllViews()
			for(o in os) {
				val time = timeFormat.format(Date(o.time))
				val v = OrderCheckItemView(this, o.orderId.toString(), time)
				binding.checkList.addView(v)
				v.setOnClickListener {
					val loadingDialog = LoadingDialog(this, finishOnBack = false)
					loadingDialog.show()
					
					StaticData.ensureSocketHandler(applicationContext, {
						val trace = TraceEvent()
						trace.orderId = o.orderId
						
						it.pushEventRePush(trace)
						
						it.waitEventAndRun { e ->
							if(e is OrderStatus) {
								val db = DBHelper(applicationContext)
								val dbO = DBOrder(o.orderId, o.time, e.status, e.reason.ifBlank { null })
								db.writableDatabase.update(DBHelper.TABLE_ORDER, dbO.toContentValues(), "${DBColumn.ORDER_AUTO_ID.columnName}=${o.id}", null)
								
								runOnUiThread {
									loadingDialog.dismiss()
									showOrder(o.orderId.toString(), timeFormat.format(Date(o.time)), dbO.getStatusString())
								}
							}
						}
						
					}) {
						runOnUiThread {
							loadingDialog.dismiss()
							showOrder(o.orderId.toString(), timeFormat.format(Date(o.time)), o.getStatusString())
						}
					}
					
				}
			}
		}
	}
	
	private fun showOrder(id: String, time: String, status: String) {
		AlertDialog.Builder(this)
			.setMessage(Translator.getString("check.dialog.message").format(id, time, status))
			.setPositiveButton(Translator.getString("check.dialog.confirm")) { _, _ -> }
			.setNegativeButton(Translator.getString("check.dialog.cancel")) { _, _ -> }
			.create().show()
	}
	
	companion object {
		const val EXTRA_ORDER_ID = "orderId"
	}
	
}