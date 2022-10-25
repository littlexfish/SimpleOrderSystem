package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The classic type item folder
 */
@SOSVersion(since = "0.0")
class ItemFolder : MenuItem {
	
	/**
	 * The folder name
	 */
	@SOSVersion(since = "0.0")
	private val name: String
	
	constructor(context: Context, frag: ClassicMenuFragment, n: String) : super(context, frag, n) {
		name = n
	}
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, n: String) :
			super(context, attr, frag, n) {
		name = n
	}
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, frag: ClassicMenuFragment, n: String) :
			super(context, attr, defStyleAttr, frag, n) {
		name = n
	}
	@Deprecated("need menu and path")
	constructor(context: Context) : super(context, ClassicMenuFragment.newInstance(-1), "") {
		name = ""
	}
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?) : super(context, attr, ClassicMenuFragment.newInstance(-1), "") {
		name = ""
	}
	@Deprecated("need menu and path")
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) :
			super(context, attr, defStyleAttr, ClassicMenuFragment.newInstance(-1), "") {
		name = ""
	}
	
	init {
		setImage(ResourcesCompat.getDrawable(resources, R.drawable.folder, null))
	}
	
	override fun onItemClick() {
		fragment.inFolder(name)
	}
	
}