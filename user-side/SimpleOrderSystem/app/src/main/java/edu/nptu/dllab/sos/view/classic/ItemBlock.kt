package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * Classic item block
 */
@SOSVersion(since = "0.0")
class ItemBlock : MenuItem {
	
	/**
	 * The item id
	 */
	@SOSVersion(since = "0.0")
	private val itemId: String
	
	constructor(context: Context, frag: ClassicMenuFragment, id: String, n: String, bitmap: Bitmap?) : super(context, frag, n) {
		itemId = id
		if(bitmap != null) setImage(bitmap)
	}
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, id: String, n: String, bitmap: Bitmap?) :
			super(context, attr, frag, n) {
		itemId = id
		if(bitmap != null) setImage(bitmap)
	}
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int,
	            frag: ClassicMenuFragment, id: String, n: String, bitmap: Bitmap?) :
			super(context, attr, defStyleAttr, frag, n) {
		itemId = id
		if(bitmap != null) setImage(bitmap)
	}
	@Deprecated("need menu and path")
	constructor(context: Context) : super(context, ClassicMenuFragment.newInstance(-1), "") {
		itemId = ""
	}
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?) :
			super(context, attr, ClassicMenuFragment.newInstance(-1), "") {
		itemId = ""
	}
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) :
			super(context, attr, defStyleAttr, ClassicMenuFragment.newInstance(-1), "") {
		itemId = ""
	}
	
	override fun onItemClick() {
		fragment.clickItem(itemId)
	}
	
}