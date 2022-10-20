package edu.nptu.dllab.sos.fragment

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.data.EventMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import org.msgpack.value.ArrayValue

abstract class MenuFragment : Fragment() {
	
	var shopId = -1
	private var loading = true
	private var menuBase: MenuBase? = null
	
	fun isOnLoading() = loading
	
	protected fun setOnLoading(flag: Boolean) {
		loading = flag
	}
	
	@CallSuper
	open fun buildMenu(menu: MenuBase) {
		menuBase = menu
	}
	
	@CallSuper
	open fun insertEvent(item: EventMenu.EventItem) {
		menuBase?.insertEvent(item)
	}
	
	fun insertAllEvent(items: Iterable<EventMenu.EventItem>) {
		for(i in items) {
			insertEvent(i)
		}
	}
	
	fun reloadResource() {
	
	}
	
	fun getMenuBase() = menuBase
	
	companion object {
		const val SHOP_ID = "SHOP_ID"
	}
	
}