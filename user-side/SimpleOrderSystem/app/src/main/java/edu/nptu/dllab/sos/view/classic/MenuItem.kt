package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.databinding.ViewItemBlockBinding
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The classic type of item
 */
@SOSVersion(since = "0.0")
abstract class MenuItem : ConstraintLayout {
	
	private lateinit var binding: ViewItemBlockBinding
	
	/**
	 * The fragment attach to
	 */
	@SOSVersion(since = "0.0")
	protected val fragment: ClassicMenuFragment
	
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
	
	
	constructor(context: Context, frag: ClassicMenuFragment, n: String)
			: super(context) {
		init()
		fragment = frag
		setString(n)
	}
	
	constructor(context: Context, attr: AttributeSet?, frag: ClassicMenuFragment, n: String)
			: super(context, attr) {
		init()
		fragment = frag
		setString(n)
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, frag: ClassicMenuFragment, n: String)
			: super(context, attr, defStyleAttr) {
		init()
		fragment = frag
		setString(n)
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
	
	/**
	 * Set image with bitmap
	 */
	@SOSVersion(since = "0.0")
	protected fun setImage(bitmap: Bitmap?) {
		binding.classicItemImg.setImageBitmap(bitmap)
		if(bitmap == null) binding.classicItemImg.layoutParams.height = resources.getDimensionPixelSize(R.dimen.classic_item_img_size_min)
		else binding.classicItemImg.layoutParams.height = resources.getDimensionPixelSize(R.dimen.classic_item_img_size)
	}
	
	/**
	 * Set image with drawable
	 */
	@SOSVersion(since = "0.0")
	protected fun setImage(drawable: Drawable?) {
		binding.classicItemImg.setImageDrawable(drawable)
		if(drawable == null) binding.classicItemImg.layoutParams.height = resources.getDimensionPixelSize(R.dimen.classic_item_img_size_min)
		else binding.classicItemImg.layoutParams.height = resources.getDimensionPixelSize(R.dimen.classic_item_img_size)
	}
	
	/**
	 * Set string of this
	 */
	@SOSVersion(since = "0.0")
	protected fun setString(t: String) {
		binding.classicItemText.text = t
	}
	
	fun setExtra(str: String) {
		binding.classicItemExtra.text = str
		if(str.isBlank()) binding.classicItemExtra.layoutParams.height = 0
		else binding.classicItemExtra.layoutParams.height = -2
	}
	
}