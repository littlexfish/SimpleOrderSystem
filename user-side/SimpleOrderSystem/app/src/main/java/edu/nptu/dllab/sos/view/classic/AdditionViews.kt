package edu.nptu.dllab.sos.view.classic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.method.SingleLineTransformationMethod
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.classic.*
import org.msgpack.value.Value

abstract class AdditionView : FrameLayout {
	
	protected val addition: ClassicAddition
	
	constructor(context: Context, add: ClassicAddition) : super(context) {
		addition = add
	}
	
	constructor(context: Context, attr: AttributeSet?, add: ClassicAddition) : super(context,
	                                                                                 attr) {
		addition = add
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int,
	            add: ClassicAddition) : super(context, attr, defStyleAttr) {
		addition = add
	}
	
	fun getValue(): Value = addition.toValue()
	
	companion object {
		fun buildByAddition(context: Context, add: ClassicAddition): AdditionView {
			return when(add) {
				is IntAddition -> IntAdditionView(context, add)
				is FloatAddition -> FloatAdditionView(context, add)
				is StringAddition -> StringAdditionView(context, add)
				is BoolAddition -> BoolAdditionView(context, add)
				is RadioAddition -> RadioAdditionView(context, add)
				else -> throw IllegalArgumentException("no exists addition")
			}
		}
	}
	
}

@SuppressLint("ViewConstructor")
class IntAdditionView : AdditionView {
	
	constructor(context: Context, add: IntAddition) : super(context, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, add: IntAddition) : super(context, attr, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, add: IntAddition)
			: super(context, attr, defStyleAttr, add) {
		init()
	}
	
//	private var lastPos = Pair(0.0f, 0.0f)
	
	@SuppressLint("ClickableViewAccessibility")
	private fun init() {
		val view = inflate(context, R.layout.view_addition_int, this)
		
		addition as IntAddition
		
		val text = view.findViewById<TextView>(R.id.addIntName)
		text.text = addition.name
		val value = view.findViewById<EditText>(R.id.addIntValue)
		value.text.append(addition.value.toString())
		value.setOnFocusChangeListener { _, hasFocus ->
			/*
			 * FISH NOTE:
			 *   No focus means user leave keyboard
			 */
			if(!hasFocus) {
				addition.value = value.text.toString().toIntOrNull() ?: addition.value
				ensureValueRange()
			}
		}
		val buttonUp = view.findViewById<ImageView>(R.id.addIntUp)
		/*
		 * FISH NOTE:
		 *   We found that the state-list-drawable might be null on normal state,
		 *    so we re-make the event to prevent the drawable not show normally.
		 */
		buttonUp.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				buttonUp.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				buttonUp.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right, null))
			}
			false
		}
		buttonUp.setOnClickListener {
			plus()
		}
		val buttonDown = view.findViewById<ImageView>(R.id.addIntDown)
		/*
		 * FISH NOTE:
		 *   We found that the state-list-drawable might be null on normal state,
		 *    so we re-make the event to prevent the drawable not show normally.
		 */
		buttonDown.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				buttonDown.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				buttonDown.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left, null))
			}
			false
		}
		buttonDown.setOnClickListener {
			minus()
		}
		
	}
	
	private fun plus() {
		addition as IntAddition
		addition.value += addition.step
		ensureValueRange()
	}
	
	private fun minus() {
		addition as IntAddition
		addition.value -= addition.step
		ensureValueRange()
	}
	
	private fun ensureValueRange() {
		addition as IntAddition
		if(addition.value > addition.max) addition.value = addition.max
		else if(addition.value < addition.min) addition.value = addition.min
		val value = findViewById<EditText>(R.id.addIntValue)
		value.text.clear()
		value.text.append(addition.value.toString())
	}
	
}

@SuppressLint("ViewConstructor")
class FloatAdditionView : AdditionView {
	
	constructor(context: Context, add: FloatAddition) : super(context, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, add: FloatAddition) : super(context, attr, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, add: FloatAddition)
			: super(context, attr, defStyleAttr, add) {
		init()
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private fun init() {
		val view = inflate(context, R.layout.view_addition_float, this)
		
		addition as FloatAddition
		
		val text = view.findViewById<TextView>(R.id.addFloatName)
		text.text = addition.name
		val value = view.findViewById<EditText>(R.id.addFloatValue)
		value.text.append("%.2f".format(addition.value))
		value.setOnFocusChangeListener { _, hasFocus ->
			/*
			 * FISH NOTE:
			 *   No focus means user leave keyboard
			 */
			if(!hasFocus) {
				addition.value = value.text.toString().toDoubleOrNull() ?: addition.value
				ensureValueRange()
			}
		}
		val buttonUp = view.findViewById<ImageView>(R.id.addFloatUp)
		/*
		 * FISH NOTE:
		 *   We found that the state-list-drawable might be null on normal state,
		 *    so we re-make the event to prevent the drawable not show normally.
		 */
		buttonUp.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				buttonUp.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				buttonUp.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_right, null))
			}
			false
		}
		buttonUp.setOnClickListener {
			plus()
		}
		val buttonDown = view.findViewById<ImageView>(R.id.addFloatDown)
		/*
		 * FISH NOTE:
		 *   We found that the state-list-drawable might be null on normal state,
		 *    so we re-make the event to prevent the drawable not show normally.
		 */
		buttonDown.setOnTouchListener { _, event ->
			if(event.action == MotionEvent.ACTION_DOWN) {
				buttonDown.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left_pressed, null))
			}
			else if(event.action == MotionEvent.ACTION_UP) {
				buttonDown.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.square_left, null))
			}
			false
		}
		buttonDown.setOnClickListener {
			minus()
		}
		
	}
	
	private fun plus() {
		addition as FloatAddition
		addition.value += addition.step
		ensureValueRange()
	}
	
	private fun minus() {
		addition as IntAddition
		addition.value -= addition.step
		ensureValueRange()
	}
	
	private fun ensureValueRange() {
		addition as FloatAddition
		if(addition.value > addition.max) addition.value = addition.max
		else if(addition.value < addition.min) addition.value = addition.min
		val value = findViewById<EditText>(R.id.addFloatValue)
		value.text.clear()
		value.text.append("%.2f".format(addition.value))
	}
	
}

@SuppressLint("ViewConstructor")
class StringAdditionView : AdditionView {
	
	constructor(context: Context, add: StringAddition) : super(context, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, add: StringAddition) : super(context, attr, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, add: StringAddition)
			: super(context, attr, defStyleAttr, add) {
		init()
	}
	
	private fun init() {
		val view = inflate(context, R.layout.view_addition_string, this)
		
		addition as StringAddition
		
		val text = view.findViewById<TextView>(R.id.addStringName)
		text.text = addition.name
		val value = view.findViewById<EditText>(R.id.addStringValue)
		
		val filters = arrayOf(LengthFilter(addition.length))
		value.filters = filters
		
		value.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable?) {
				if(s != null && s.length <= addition.length) addition.value = s.toString()
			}
		})
		
	}
	
}

@SuppressLint("ViewConstructor")
class BoolAdditionView : AdditionView {
	
	constructor(context: Context, add: BoolAddition) : super(context, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, add: BoolAddition) : super(context, attr, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, add: BoolAddition)
			: super(context, attr, defStyleAttr, add) {
		init()
	}
	
	private fun init() {
		val view = inflate(context, R.layout.view_addition_bool, this)
		
		addition as BoolAddition
		
		val check = view.findViewById<CheckBox>(R.id.addBoolValue)
		check.text = addition.name
		check.isChecked = addition.value
		check.setOnCheckedChangeListener { _, isChecked ->
			addition.value = isChecked
		}
		
	}
	
}

@SuppressLint("ViewConstructor")
class RadioAdditionView : AdditionView {
	
	constructor(context: Context, add: RadioAddition) : super(context, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, add: RadioAddition) : super(context, attr, add) {
		init()
	}
	
	constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int, add: RadioAddition)
			: super(context, attr, defStyleAttr, add) {
		init()
	}
	
	private val radioMap = HashMap<CheckBox, Int>()
	
	private fun init() {
		val view = inflate(context, R.layout.view_addition_radio, this)
		
		addition as RadioAddition
		
		view.findViewById<TextView>(R.id.addRadioName).text = addition.name
		
		val vSize = addition.items.size / 2 + 1
		
		val listener = OnCheckedChangeListener { buttonView, isChecked ->
			if(isChecked) {
				addition.value = radioMap[buttonView]!!
				for(c in radioMap.keys) {
					if(c.isChecked && c != buttonView) c.isChecked = false
				}
			}
			else {
				for(c in radioMap.keys) {
					if(c.isChecked) return@OnCheckedChangeListener
				}
				(buttonView as CheckBox).isChecked = true
			}
		}
		
		val list = view.findViewById<LinearLayout>(R.id.addRadioList)
		list.weightSum = vSize.toFloat()
		
		val param = LinearLayout.LayoutParams(-1, -2, 1f)
		for(v in 0 until vSize) {
			val linear = LinearLayout(context)
			linear.orientation = LinearLayout.HORIZONTAL
			linear.layoutParams = param
			linear.weightSum = 2f
			list.addView(linear)
			for(h in 0..1) {
				val index = v * 2 + h
				if(index >= addition.items.size) break
				val check = CheckBox(context)
				check.setButtonDrawable(R.drawable.radio_button)
				check.text = addition.items[index]
				check.layoutParams = param
				if(index == addition.value) check.isChecked = true
				check.setOnCheckedChangeListener(listener)
				linear.addView(check)
				radioMap[check] = index
			}
		}
	}
	
}