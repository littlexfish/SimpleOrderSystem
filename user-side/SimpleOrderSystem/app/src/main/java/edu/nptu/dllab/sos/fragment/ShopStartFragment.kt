package edu.nptu.dllab.sos.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.ShopSideActivity
import edu.nptu.dllab.sos.databinding.FragmentShopStartBinding
import edu.nptu.dllab.sos.io.Translator

/**
 * The start of shop side fragment
 *
 * @author Little Fish
 */
class ShopStartFragment : Fragment() {
	
	private lateinit var binding: FragmentShopStartBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentShopStartBinding.inflate(inflater)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.shopStartConfirm.text = Translator.getString("shopStart.confirm")
		
		binding.shopStartConfirm.setOnClickListener {
			(activity as? ShopSideActivity)?.changeFrag(ShopSideFragment())
		}
	}

}