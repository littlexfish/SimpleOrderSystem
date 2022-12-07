package edu.nptu.dllab.sos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.nptu.dllab.sos.data.shop.Order
import edu.nptu.dllab.sos.databinding.ActivityShopSideItemBinding

class ShopSideOrderActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityShopSideItemBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityShopSideItemBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		if(order == null) finish()
		val tmpOrder = order!!
		order = null
		
		
		// TODO: Finish activity need send orderId
	}
	
	companion object {
		var order: Order? = null
		
		const val RESULT_NONE = 6000
		const val RESULT_REQUEST = 6001
		const val RESULT_NOT_REQUEST = 6002
		const val RESULT_DONE = 6003
		
		const val EXTRA_ORDER_ID = "orderId"
	}
}