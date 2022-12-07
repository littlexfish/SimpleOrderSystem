package edu.nptu.dllab.sos

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import edu.nptu.dllab.sos.data.menu.OrderItem
import edu.nptu.dllab.sos.data.menu.classic.ClassicItem
import edu.nptu.dllab.sos.databinding.ActivityItemOrderBinding
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.view.classic.AdditionView

// TODO: rotate screen
class ItemOrderActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityItemOrderBinding
	
	private val additionList = ArrayList<AdditionView>()
	
	private var oldKeyboardVisibility = false
	private val keyboardDetector = ViewTreeObserver.OnGlobalLayoutListener {
		val root = binding.root
		val rect = Rect()
		root.getWindowVisibleDisplayFrame(rect)
		val screenHeight = root.rootView.height
		
		val keypadHeight = screenHeight - rect.bottom
		
		if(keypadHeight > screenHeight * 0.15) {
			if(!oldKeyboardVisibility) {
				oldKeyboardVisibility = true
				onKeyBoardVisibilityChange(true)
			}
		}
		else {
			if(oldKeyboardVisibility) {
				oldKeyboardVisibility = false
				onKeyBoardVisibilityChange(false)
			}
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityItemOrderBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		if(selectedItem == null || selectedItem !is ClassicItem) {
			selectedItem = null
			finish()
			return
		}
		val selected = selectedItem as ClassicItem
		selectedItem = null
		
		binding.orderItemTitle.text = Translator.getString("item.title")
		binding.orderItemNote.hint = Translator.getString("item.note")
		binding.orderItemConfirm.text = Translator.getString("item.confirm").format(selected.price)
		
		binding.orderItemName.text = selected.display
		binding.orderItemNoteLength.text = "0/${resources.getInteger(R.integer.item_note_length)}"
		
		val param = LinearLayout.LayoutParams(-1, -2)
		for(add in selected.addition) {
			val v = AdditionView.buildByAddition(this, add)
			v.layoutParams = param
			binding.orderItemAddList.addView(v)
			additionList.add(v)
		}
		
		binding.orderItemNote.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable?) {
				if(s != null) binding.orderItemNoteLength.text = "${binding.orderItemNote.text.length}/${resources.getInteger(R.integer.item_note_length)}"
				selected.note = binding.orderItemNote.text.toString()
			}
		})
		binding.orderItemNote.setOnFocusChangeListener { _, hasFocus ->
			if(!hasFocus && oldKeyboardVisibility) {
				binding.orderItemNote.visibility = View.GONE
				binding.orderItemNoteLength.visibility = View.GONE
				binding.orderItemShowNote.visibility = View.VISIBLE
			}
		}
		
		val onClickOutside = OnClickListener {
			val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
			binding.orderItemNote.clearFocus()
		}
		
		binding.orderItemBgOver.setOnClickListener(onClickOutside)
		binding.orderItemAddList.setOnClickListener(onClickOutside)
		
		binding.orderItemConfirm.setOnClickListener {
			StaticData.addItem(selected)
			setResult(RESULT_ADD)
			finish()
		}
		
		binding.orderItemBack.setOnClickListener {
			setResult(RESULT_CANCEL)
			finish()
		}
		
		binding.orderItemShowNote.text = Translator.getString("item.note.show")
		binding.orderItemShowNote.setOnClickListener {
			binding.orderItemNote.visibility = View.VISIBLE
			binding.orderItemNoteLength.visibility = View.VISIBLE
			binding.orderItemShowNote.visibility = View.GONE
			binding.orderItemNote.requestFocus()
		}
		
		binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardDetector)
		
	}
	
	private fun onKeyBoardVisibilityChange(visible: Boolean) {
		if(visible) {
			if(!binding.orderItemNote.isFocused) {
				binding.orderItemNote.visibility = View.GONE
				binding.orderItemNoteLength.visibility = View.GONE
				binding.orderItemShowNote.visibility = View.VISIBLE
			}
			else {
				binding.orderItemNote.visibility = View.VISIBLE
				binding.orderItemNoteLength.visibility = View.VISIBLE
				binding.orderItemShowNote.visibility = View.GONE
			}
		}
		else {
			binding.orderItemNote.visibility = View.VISIBLE
			binding.orderItemNoteLength.visibility = View.VISIBLE
			binding.orderItemShowNote.visibility = View.GONE
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardDetector)
	}
	
	companion object {
		var selectedItem: OrderItem? = null
		
		const val RESULT_ADD = 200
		const val RESULT_CANCEL = 201
	}
	
}