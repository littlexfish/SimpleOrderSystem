package edu.nptu.dllab.sos.view.classic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment

/**
 * The classic type item folder
 */
@SuppressLint("ViewConstructor")
class ItemFolder : MenuItem {
	
	/**
	 * The folder name
	 */
	private val name: String
	
	constructor(context: Context, frag: ClassicMenuFragment, n: String)
			: super(context, frag, n) {
		name = n
	}
	
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, n: String)
			: super(context, attr, frag, n) {
		name = n
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, frag: ClassicMenuFragment, n: String)
			: super(context, attr, defStyleAttr, frag, n) {
		name = n
	}
	
	init {
		setImage(ResourcesCompat.getDrawable(resources, R.drawable.folder, null))
		setExtra("")
	}
	
	override fun onItemClick() {
		fragment.inFolder(name)
	}
	
}