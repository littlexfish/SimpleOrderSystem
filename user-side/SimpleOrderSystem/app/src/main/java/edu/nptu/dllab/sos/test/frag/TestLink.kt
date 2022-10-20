package edu.nptu.dllab.sos.test.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.LinkEvent
import edu.nptu.dllab.sos.databinding.FragmentTestLinkBinding
import edu.nptu.dllab.sos.test.TestActivity
import edu.nptu.dllab.sos.util.Position

class TestLink : Fragment() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_test_link, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val binding = FragmentTestLinkBinding.bind(view)
		binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
				binding.textView4.text = progress.toString()
			}
			override fun onStartTrackingTouch(seekBar: SeekBar?) {}
			override fun onStopTrackingTouch(seekBar: SeekBar?) {}
		})
		binding.seekBar2.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
				binding.textView5.text = progress.toString()
			}
			override fun onStartTrackingTouch(seekBar: SeekBar?) {}
			override fun onStopTrackingTouch(seekBar: SeekBar?) {}
		})
		binding.button2.setOnClickListener {
			val e = LinkEvent()
			e.position = Position(binding.seekBar.progress.toDouble(), binding.seekBar2.progress.toDouble())
			(activity as TestActivity).sendEvent(e)
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance() = TestLink()
	}
}