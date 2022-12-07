package edu.nptu.dllab.sos.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.shop.Item
import edu.nptu.dllab.sos.fragment.ShopSideFragment
import kotlin.math.min

@SuppressLint("ViewConstructor")
class ShopSideOrder(context: Context, id: Int, items: List<Item>) : FrameLayout(context) {
	
	init {
		val density = resources.displayMetrics.density
		
		background = ResourcesCompat.getDrawable(resources, R.drawable.frame, null)
		
		val textView = TextView(context)
		textView.text = id.toString()
		textView.gravity = TEXT_ALIGNMENT_CENTER
		textView.textSize = 20 * density
		textView.layoutParams = LayoutParams(-1, (25 * density).toInt())
		addView(textView)
		
		val menu = ShopSideFragment.classicMenu
		val linear = LinearLayout(context)
		linear.x = 25 * density
		linear.layoutParams = LayoutParams(-1, -2)
		addView(linear)
		for(i in 0 until min(5, items.size)) {
			val t = items[i]
			val tv = TextView(context)
			val shopItem = menu?.getShopItem(t.itemId)
			tv.text = shopItem?.display ?: ""
			tv.gravity = TextView.TEXT_ALIGNMENT_CENTER
			linear.addView(tv)
		}
		if(items.size > 5) {
			val tv = TextView(context)
			tv.text = "..."
			tv.gravity = TextView.TEXT_ALIGNMENT_CENTER
			linear.addView(tv)
		}
	}
	
}