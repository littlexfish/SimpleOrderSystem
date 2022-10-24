package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.databinding.ViewItemBlockBinding
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The classic type of item
 */
@SOSVersion(since = "0.0")
abstract class MenuItem : ConstraintLayout {
	
	private lateinit var binding: ViewItemBlockBinding
	
	/**
	 * The category path
	 */
	@SOSVersion(since = "0.0")
	private val catePath: String
	
	/**
	 * The menu
	 */
	@SOSVersion(since = "0.0")
	private val menu: ClassicMenu
	
	/**
	 * The item bitmap
	 */
	@SOSVersion(since = "0.0")
	var bitmap: Bitmap
		get() = binding.classicItemImg.drawable.toBitmap()
		set(value) {
			setImage(value)
		}
	
	/**
	 * The item drawable
	 */
	@SOSVersion(since = "0.0")
	var drawable: Drawable?
		get() = binding.classicItemImg.drawable
		set(value) {
			setImage(value)
		}
	
	/**
	 * The item name
	 */
	@SOSVersion(since = "0.0")
	var text: String?
		get() = binding.classicItemText.text.toString()
		set(value) {
			setString(value ?: "")
		}
	
	
	constructor(context: Context, m: ClassicMenu, path: String) : super(context) {
		init()
		menu = m
		catePath = path
	}
	constructor(context: Context, attr: AttributeSet?, m: ClassicMenu, path: String) : super(context, attr) {
		init()
		menu = m
		catePath = path
	}
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, m: ClassicMenu, path: String) : super(context, attr, defStyleAttr) {
		init()
		menu = m
		catePath = path
	}
	
	private fun init() {
		val view = inflate(context, R.layout.view_item_block, this)
		binding = ViewItemBlockBinding.bind(view)
		
		setOnClickListener {
			onItemClick()
		}
		
	}
	
	/**
	 * Call on this been clicked
	 */
	@SOSVersion(since = "0.0")
	protected abstract fun onItemClick()
	
	protected fun setImage(bitmap: Bitmap) {
		binding.classicItemImg.setImageBitmap(bitmap)
	}
	
	protected fun setImage(drawable: Drawable?) {
		binding.classicItemImg.setImageDrawable(drawable)
	}
	
	protected fun setString(t: String) {
		binding.classicItemText.text = t
	}
	
}