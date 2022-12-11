package edu.nptu.dllab.sos.view.classic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import edu.nptu.dllab.sos.android.DownloadBitmap
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
		autoImage(bitmap)
	}
	
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, id: String,
	            n: String, bitmap: Bitmap?, money: Double) : super(context, attr, frag, n) {
		itemId = id
		setImage(bitmap)
		setExtra("$ %.2f".format(money))
		autoImage(bitmap)
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, frag: ClassicMenuFragment,
	            id: String, n: String, bitmap: Bitmap?, money: Double) : super(context, attr,
	                                                                           defStyleAttr, frag,
	                                                                           n) {
		itemId = id
		setImage(bitmap)
		setExtra("$ %.2f".format(money))
		autoImage(bitmap)
	}
	
	private fun autoImage(bitmap: Bitmap?) {
		if(bitmap == null && text != null) {
			if(text!!.endsWith("潛艇堡")) {
				setImage(DownloadBitmap.getBitmapFromBuffer(context, "sandwich.png"))
			}
			else if(text!!.endsWith("堡")) {
				setImage(DownloadBitmap.getBitmapFromBuffer(context, "burger.png"))
			}
		}
	}
	
	override fun onItemClick() {
		fragment.clickItem(itemId)
	}
	
}