package edu.nptu.dllab.sos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import edu.nptu.dllab.sos.data.menu.OrderItem
import edu.nptu.dllab.sos.data.menu.classic.ClassicItem
import edu.nptu.dllab.sos.databinding.ActivityItemOrderBinding
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.view.classic.AdditionView

class ItemOrderActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityItemOrderBinding
	
	private val additionList = ArrayList<AdditionView>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityItemOrderBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		if(selectedItem == null || selectedItem !is ClassicItem) finish()
		val selected = selectedItem as ClassicItem
		selectedItem = null
		
		binding.orderItemTitle.text = Translator.getString("item.title")
		binding.orderItemNote.hint = Translator.getString("item.note")
		binding.orderItemConfirm.text = Translator.getString("item.confirm").format(selected.price)
		
		binding.orderItemName.text = selected.display
		binding.orderItemNoteLength.text = "0/${resources.getInteger(R.integer.item_note_length)}"
		
		for(add in selected.addition) {
			val v = AdditionView.buildByAddition(this, add)
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
		
		binding.orderItemConfirm.setOnClickListener {
			StaticData.addItem(selected)
			setResult(RESULT_ADD)
			finish()
		}
		
		binding.orderItemBack.setOnClickListener {
			setResult(RESULT_CANCEL)
			finish()
		}
		
	}
	
	companion object {
		var selectedItem: OrderItem? = null
		
		const val RESULT_ADD = 200
		const val RESULT_CANCEL = 201
	}
	
}