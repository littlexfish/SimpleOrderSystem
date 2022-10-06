package edu.nptu.dllab.sos.fragment

import androidx.fragment.app.Fragment

abstract class MenuFragment : Fragment() {
	
	protected var shopId = -1
	private var loading = true
	
	fun isOnLoading() = loading
	
	protected fun setOnLoading(flag: Boolean) {
		loading = flag
	}
	
	companion object {
		const val SHOP_ID = "SHOP_ID"
	}
	
}