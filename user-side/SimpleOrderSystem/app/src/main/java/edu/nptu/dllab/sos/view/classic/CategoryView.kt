package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The classic menu type category
 */
@SOSVersion(since = "0.0")
class CategoryView : ConstraintLayout {
	
	/**
	 * The category name
	 */
	@SOSVersion(since = "0.0")
	private lateinit var textView: TextView
	
	/**
	 * The category img
	 */
	@SOSVersion(since = "0.0")
	private lateinit var imageView: ImageView
	
	/**
	 * Set get of category name
	 */
	@SOSVersion(since = "0.0")
	var string
		get() = textView.text
		set(value) {
			textView.text = value
		}
	/**
	 * Set get of category img
	 */
	@SOSVersion(since = "0.0")
	var image
		get() = imageView.drawable
		set(value) {
			imageView.setImageDrawable(value)
		}
	
	constructor(context: Context) : super(context) {
		init()
	}
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init()
	}
	
	private fun init() { // Load attributes
		inflate(context, R.layout.view_classic_category, this)
		
		textView = findViewById(R.id.classic_cate_text)
		imageView = findViewById(R.id.classic_cate_img)
		
	}
	
}