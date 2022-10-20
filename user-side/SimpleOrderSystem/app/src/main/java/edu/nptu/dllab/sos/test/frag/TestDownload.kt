package edu.nptu.dllab.sos.test.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.DownloadRequestEvent
import edu.nptu.dllab.sos.databinding.FragmentTestDownloadBinding
import edu.nptu.dllab.sos.test.TestActivity

class TestDownload : Fragment() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_test_download, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val binding = FragmentTestDownloadBinding.bind(view)
		
		binding.button3.setOnClickListener {
			val e = DownloadRequestEvent()
			val t = binding.testText.text.toString()
			val spl = t.split(Regex("[\r\n]"))
			for(s in spl) e.addPath(s)
			(activity as TestActivity).sendEvent(e)
		}
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance() = TestDownload()
	}
}