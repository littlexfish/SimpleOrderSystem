package edu.nptu.dllab.sos.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.databinding.ViewShopItemBinding
import edu.nptu.dllab.sos.io.Config
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.Util

class ShopItemView : FrameLayout {
	
	private lateinit var binding: ViewShopItemBinding
	
	private val unitToken = Config.getString(Config.Key.DISTANCE_UNIT)
	private val unit = Translator.getString("dist.unit.$unitToken")
	
	var name: CharSequence
		get() = binding.shopName.text
		set(value) {
			binding.shopName.text = value
		}
	
	var distance: Double = 0.0
		get() = field
		set(value) {
			field = value
			binding.shopDistance.text = "%.2f $unit".format(Util.calDistUnit(value, unitToken))
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
		val view = inflate(context, R.layout.view_shop_item, this)
		binding = ViewShopItemBinding.bind(view)
		
		name = ""
		distance = 0.0
	}
	
	
}