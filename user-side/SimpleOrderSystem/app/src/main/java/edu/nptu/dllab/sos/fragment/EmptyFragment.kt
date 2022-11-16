package edu.nptu.dllab.sos.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

class EmptyFragment : Fragment() {
	
	companion object {
		fun newInstance() = EmptyFragment().apply {
			arguments = Bundle()
		}
	}
	
}