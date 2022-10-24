package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.util.AttributeSet
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The classic type item folder
 */
@SOSVersion(since = "0.0")
class ItemFolder : MenuItem {
	
	constructor(context: Context, m: ClassicMenu, path: String) : super(context, m, path)
	constructor(context: Context, attr: AttributeSet?, m: ClassicMenu, path: String) : super(context, attr, m, path)
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, m: ClassicMenu, path: String) : super(context, attr, defStyleAttr, m, path)
	@Deprecated("need menu and path")
	constructor(context: Context) : super(context, ClassicMenu(-1, -1), "")
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?) : super(context, attr, ClassicMenu(-1, -1), "")
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr, ClassicMenu(-1, -1), "")
	
	override fun onItemClick() {
		TODO("Get in folder.")
	}
	
}