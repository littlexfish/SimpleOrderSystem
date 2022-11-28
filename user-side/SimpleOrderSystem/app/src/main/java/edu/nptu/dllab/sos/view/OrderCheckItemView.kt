package edu.nptu.dllab.sos.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.io.Translator

@SuppressLint("ViewConstructor")
class OrderCheckItemView : FrameLayout {
	
	constructor(context: Context, id: String, time: String) : super(context) {
		init(id, time)
	}
	
	constructor(context: Context, attr: AttributeSet?, id: String, time: String) : super(context, attr) {
		init(id, time)
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, id: String, time: String)
			: super(context, attr, defStyleAttr) {
		init(id, time)
	}
	
	private fun init(id: String, time: String) {
		val view = inflate(context, R.layout.view_order_item_check, this)
		
		view.findViewById<TextView>(R.id.checkOrderId).text = "${Translator.getString("check.orderId")}: $id"
		view.findViewById<TextView>(R.id.checkTime).text = time
	}
	
}