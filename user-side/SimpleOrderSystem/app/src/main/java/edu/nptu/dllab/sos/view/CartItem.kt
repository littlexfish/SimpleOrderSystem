package edu.nptu.dllab.sos.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.databinding.ViewCartItemBinding

class CartItem : FrameLayout {
	
	private lateinit var binding: ViewCartItemBinding
	
	var text: CharSequence
		get() = binding.itemName.text
		set(value) {
			binding.itemName.text = value
		}
	
	private var currency: Double = 0.0
	private var fractureDigit = 0
	
	var money: Double
		get() = currency
		set(value) {
			currency = value
			binding.itemMoney.text = "$%.${fractureDigit}f".format(value)
		}
	
	constructor(context: Context) : super(context) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int)
			: super(context, attr, defStyleAttr) {
		init()
	}
	
	private fun init() {
		val view = inflate(context, R.layout.view_cart_item, this)
		binding = ViewCartItemBinding.bind(view)
		
		text = ""
		money = 0.0
	}
	
	fun setPriceWidth(w: Int) {
		binding.itemMoney.minWidth = w
	}
	
	fun getPriceWidth() = binding.itemMoney.width
	
}