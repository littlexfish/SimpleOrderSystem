package edu.nptu.dllab.sos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.databinding.ActivityShopSideBinding
import edu.nptu.dllab.sos.fragment.EmptyFragment
import edu.nptu.dllab.sos.fragment.ShopSideFragment
import edu.nptu.dllab.sos.fragment.ShopStartFragment
import edu.nptu.dllab.sos.io.Translator

class ShopSideActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityShopSideBinding
	private var frag: Fragment = EmptyFragment()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityShopSideBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.shopSideTitle.text = Translator.getString("shopSide.title")
		binding.shopSideBack.setOnClickListener {
			finish()
		}
		
		if(savedInstanceState != null) {
			if(savedInstanceState.containsKey(STATE_SHOP_SIDE_DATA)) {
				val b = savedInstanceState.getBundle(STATE_SHOP_SIDE_DATA)!!
				val f = ShopSideFragment()
				f.putBackData(b)
				changeFrag(f)
			}
		}
		else {
			changeFrag(ShopStartFragment())
		}
	}
	
	fun changeFrag(frag: Fragment) {
		supportFragmentManager.beginTransaction().replace(R.id.shopSideFragment, frag).commit()
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		if(frag is ShopSideFragment) {
			val bundle = Bundle()
			frag.onSaveInstanceState(bundle)
			outState.putBundle(STATE_SHOP_SIDE_DATA, bundle)
		}
	}
	
	companion object {
		private const val STATE_SHOP_SIDE_DATA = "shopSide"
	}
	
}