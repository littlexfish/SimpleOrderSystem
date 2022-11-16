package edu.nptu.dllab.sos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.nptu.dllab.sos.databinding.ActivityOrderCheckBinding
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBOrder
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
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityOrderCheckBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		val db = DBHelper(applicationContext)
		val cur = db.select(DBHelper.TABLE_ORDER, limit = orderMaxSize,
		                    orderBy = DBColumn.ORDER_TIME.columnName, asc = false)
		while(cur.moveToNext()) {
			orders.add(DBOrder(cur))
		}
		
		refreshList()
		
		binding.checkNextPage.setOnClickListener {
			nextPage()
		}
		binding.checkPrePage.setOnClickListener {
			prePage()
		}
		binding.checkBack.setOnClickListener { finish() }
		
	}
	
	private fun refreshList() {
		buildOrderList(getSubList(offset, limit))
		canPre = offset - limit > -limit
		canNext = offset + limit <= orders.size
		binding.checkNextPage.isEnabled = canNext
		binding.checkPrePage.isEnabled = canPre
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
			val timeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
			binding.checkList.removeAllViews()
			for(o in os) {
				val v =
					OrderCheckItemView(this, o.orderId.toString(), timeFormat.format(Date(o.time)))
				binding.checkList.addView(v)
			}
		}
	}
	
	companion object {
		const val EXTRA_ORDER_ID = "orderId"
	}
	
}