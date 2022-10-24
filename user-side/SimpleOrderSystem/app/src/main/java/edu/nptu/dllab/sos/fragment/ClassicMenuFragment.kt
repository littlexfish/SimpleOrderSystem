package edu.nptu.dllab.sos.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * A fragment class of classic type menu
 */
@SOSVersion(since = "0.0")
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
	
	override fun buildMenu(menu: MenuBase) {
		super.buildMenu(menu)
		TODO("Not yet implemented")
	}
	
	companion object {
		fun newInstance(shopId: Int) = ClassicMenuFragment().apply {
			arguments = Bundle().apply {
				putInt(SHOP_ID, shopId)
			}
		}
	}
}