package edu.nptu.dllab.sos.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.nptu.dllab.sos.R



class ClassicMenuFragment : MenuFragment() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			shopId = it.getInt(SHOP_ID)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_classic_menu, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		
		
	}
	
	companion object {
		fun newInstance(shopId: Int) = ClassicMenuFragment().apply {
			arguments = Bundle().apply {
				putInt(SHOP_ID, shopId)
			}
		}
	}
}