package edu.nptu.dllab.sos.fragment

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.data.pull.EventMenu

/**
 * The base class of menu fragment
 */
abstract class MenuFragment : Fragment() {
	
	/**
	 * The shop id
	 */
	var shopId = -1
	
	/**
	 * Is on loading
	 */
	private var loading = true
	
	/**
	 * The menu of this fragment attach from
	 */
	private var menuBase: MenuBase? = null
	
	/**
	 * Get is now on loading
	 */
	fun isOnLoading() = loading
	
	/**
	 * Set is on loading
	 */
	protected fun setOnLoading(flag: Boolean) {
		loading = flag
	}
	
	/**
	 * Use for build menu to this menu fragment
	 */
	@CallSuper
	open fun buildMenu(menu: MenuBase) {
		menuBase = menu
	}
	
	/**
	 * Insert event to this menu fragment
	 */
	@CallSuper
	open fun insertEvent(item: EventMenu.EventItem) {
		menuBase?.insertEvent(item)
	}
	
	/**
	 * Insert all event data
	 */
	fun insertAllEvent(items: Iterable<EventMenu.EventItem>) {
		for(i in items) {
			insertEvent(i)
		}
	}
	
	/**
	 * Reload the resource of this menu
	 */
	open fun reloadResource() {
	}
	
	/**
	 * Call on back pressed
	 * @return `false` if not consume
	 */
	open fun onBackPressed(): Boolean {
		return false
	}
	
	/**
	 * Get menu
	 */
	fun getMenuBase() = menuBase
	
	companion object {
		const val SHOP_ID = "SHOP_ID"
	}
	
}