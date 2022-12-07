package edu.nptu.dllab.sos.view.classic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment

/**
 * Classic item block
 */
@SuppressLint("ViewConstructor")
class ItemBlock : MenuItem {
	
	/**
	 * The item id
	 */
	private val itemId: String
	
	constructor(context: Context, frag: ClassicMenuFragment, id: String, n: String, bitmap: Bitmap?, money: Double)
			: super(context, frag, n) {
		itemId = id
		setImage(bitmap)
		setExtra("$ %.2f".format(money))
	}
	
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, id: String,
	            n: String, bitmap: Bitmap?, money: Double) : super(context, attr, frag, n) {
		itemId = id
		setImage(bitmap)
		setExtra("$ %.2f".format(money))
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, frag: ClassicMenuFragment,
	            id: String, n: String, bitmap: Bitmap?, money: Double) : super(context, attr,
	                                                                           defStyleAttr, frag,
	                                                                           n) {
		itemId = id
		setImage(bitmap)
		setExtra("$ %.2f".format(money))
	}
	
	override fun onItemClick() {
		fragment.clickItem(itemId)
	}
	
}