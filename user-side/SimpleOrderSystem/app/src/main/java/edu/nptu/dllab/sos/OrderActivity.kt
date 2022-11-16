package edu.nptu.dllab.sos

import android.content.Intent
import android.icu.util.Currency
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import edu.nptu.dllab.sos.data.pull.OrderRequest
import edu.nptu.dllab.sos.data.push.OrderEvent
import edu.nptu.dllab.sos.databinding.ActivityOrderBinding
import edu.nptu.dllab.sos.dialog.LoadingDialog
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.io.db.DBOrder
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.view.CartItem
import kotlin.math.roundToInt
import kotlin.streams.asStream
import kotlin.streams.toList

class OrderActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityOrderBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityOrderBinding.inflate(layoutInflater)
		
		setContentView(binding.root)
		
		// set text
		binding.orderTitle.text = Translator.getString("order.title")
		binding.orderItemsTitle.text = Translator.getString("order.items.title")
		binding.orderMoneyLabel.text = Translator.getString("order.items.moneyLabel")
		binding.orderConfirm.text = Translator.getString("order.confirm")
		
		// on click
		binding.orderBack.setOnClickListener { finish() }
		binding.orderConfirm.setOnClickListener {
			AlertDialog.Builder(this)
				.setMessage(Translator.getString("order.confirm.message").format(StaticData.getItems().map { it.price }.toDoubleArray().sum()))
				.setPositiveButton(Translator.getString("order.confirm.confirm")) { _, _ ->
					val loading = LoadingDialog(this)
					loading.setMessage(Translator.getString("order.process"))
					loading.show()
					
					StaticData.ensureSocketHandler(applicationContext, {
						val evt = OrderEvent()
						evt.addAllItems(StaticData.getItems())
						
						it.pushEvent(evt)
						
						runOnUiThread { loading.setMessage(Translator.getString("order.wait")) }
						it.waitEventAndRun { e ->
							if(e is OrderRequest) {
								val id = e.orderId
								val time = System.currentTimeMillis()
								
								val db = DBHelper(applicationContext)
								val dbO = DBOrder(id, time)
								db.insert(DBHelper.TABLE_ORDER, dbO.toContentValues())
								db.close()
								
								runOnUiThread {
									loading.dismiss()
									AlertDialog.Builder(this@OrderActivity)
										.setMessage(Translator.getString("order.showId").format(id.toString()))
										.setPositiveButton(Translator.getString("order.showId.confirm")) { _, _ ->
											StaticData.clearItems()
											finish()
										}
										.setNegativeButton(Translator.getString("order.showId.check")) { _, _ ->
											startActivity(Intent(this@OrderActivity, OrderCheckActivity::class.java).apply {
												putExtra(OrderCheckActivity.EXTRA_ORDER_ID, id)
											})
										}
										.create().show()
								}
							}
						}
					})
				}
				.setNegativeButton(Translator.getString("order.confirm.cancel")) { _, _ ->
					// do nothing, close dialog because user cancel the action
				}
				.create().show()
		}
		
		// on back
		onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				finish()
			}
		})
		
		buildOrderItems()
		
	}
	
	private fun buildOrderItems() {
		val items = StaticData.getItems()
		
		/*
		 * FISH NOTE:
		 *   We will auto close activity when the cart is empty,
		 *    it will prevent user click button by mistake
		 */
		if(items.isEmpty()) {
			AlertDialog.Builder(this)
				.setCancelable(false)
				.setMessage(Translator.getString("order.empty"))
				.setPositiveButton(Translator.getString("order.empty.confirm")) { _, _ ->
					finish()
				}
				.create().show()
			return
		}
		/*
		 * FISH NOTE:
		 *   We don't process currency code that we didn't process
		 *    any about currency code
		 */
		val mS = items.map { it.price }.toDoubleArray().sum()
		/*
		 * FISH NOTE:
		 *   Show sum of money that ignore any currency code.
		 *   The currency code always NTD
		 */
		val currency = Currency.getInstance("NTD")
		binding.orderMoney.text = "${currency.displayName}$ ${mS.roundToInt()}"
		
		binding.orderList.removeAllViews()
		for(index in items.indices) {
			val it = items[index]
			val show = it.display
			val m = it.price
			val i = CartItem(this).apply {
				text = "${index + 1}: $show"
				money = m
			}
			i.setOnLongClickListener { _ ->
				AlertDialog.Builder(this)
					.setMessage(Translator.getString("order.delete.message").format(show))
					.setPositiveButton(Translator.getString("order.delete.confirm")) { _, _ ->
						StaticData.removeItem(it)
						buildOrderItems()
					}
					.setNegativeButton(Translator.getString("order.delete.cancel")) { _, _ ->
						// do nothing, just close the dialog
					}
					.create().show()
				true
			}
			binding.orderList.addView(i)
		}
		if(items.isNotEmpty()) {
			binding.orderList.post {
				val biggest = binding.orderList.children
					.map { (it as CartItem).getPriceWidth() }
					.asStream().toList().toIntArray().sortedArray().last()
				for(i in binding.orderList.children) {
					(i as CartItem).setPriceWidth(biggest)
				}
			}
		}
		
	}
	
}