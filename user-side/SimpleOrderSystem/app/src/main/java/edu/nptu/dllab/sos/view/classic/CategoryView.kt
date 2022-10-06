package edu.nptu.dllab.sos.view.classic

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import edu.nptu.dllab.sos.R

class CategoryView : FrameLayout {
	
	private lateinit var textView: TextView
	private lateinit var imageView: ImageView
	
	var string
		get() = textView.text
		set(value) {
			textView.text = value
		}
	var image
		get() = imageView.drawable
		set(value) {
			imageView.setImageDrawable(value)
		}
	
	constructor(context: Context) : super(context) {
		init(null, 0)
	}
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs, 0)
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(attrs, defStyle)
	}
	
	private fun init(attrs: AttributeSet?, defStyle: Int) { // Load attributes
		val a = context.obtainStyledAttributes(attrs, R.styleable.CategoryView, defStyle, 0)
		
		if(a.hasValue(R.styleable.CategoryView_exampleDrawable)) {
		}
		a.recycle()
		
		inflate(context, R.layout.view_classic_category, this)
		
		textView = findViewById(R.id.classic_cate_text)
		imageView = findViewById(R.id.classic_cate_img)
		
	}
	
}