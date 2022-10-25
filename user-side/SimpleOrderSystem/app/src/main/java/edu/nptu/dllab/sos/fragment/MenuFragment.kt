package edu.nptu.dllab.sos.fragment

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.data.EventMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.ArrayValue

/**
 * The base class of menu fragment
 */
@SOSVersion(since = "0.0")
abstract class MenuFragment : Fragment() {
	
	/**
	 * The shop id
	 */
	@SOSVersion(since = "0.0")
	var shopId = -1
	
	/**
	 * Is on loading
	 */
	@SOSVersion(since = "0.0")
	private var loading = true
	
	/**
	 * The menu of this fragment attach from
	 */
	@SOSVersion(since = "0.0")
	private var menuBase: MenuBase? = null
	
	/**
	 * Get is now on loading
	 */
	@SOSVersion(since = "0.0")
	fun isOnLoading() = loading
	
	/**
	 * Set is on loading
	 */
	@SOSVersion(since = "0.0")
	protected fun setOnLoading(flag: Boolean) {
		loading = flag
	}
	
	/**
	 * Use for build menu to this menu fragment
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun buildMenu(menu: MenuBase) {
		menuBase = menu
	}
	
	/**
	 * Insert event to this menu fragment
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun insertEvent(item: EventMenu.EventItem) {
		menuBase?.insertEvent(item)
	}
	
	/**
	 * Insert all event data
	 */
	@SOSVersion(since = "0.0")
	fun insertAllEvent(items: Iterable<EventMenu.EventItem>) {
		for(i in items) {
			insertEvent(i)
		}
	}
	
	/**
	 * Reload the resource of this menu
	 * TODO: reload resource
	 */
	@SOSVersion(since = "0.0")
	fun reloadResource() {
		TODO("Reload resource")
	}
	
	/**
	 * Call on back pressed
	 * @return `false` if not consume
	 */
	@SOSVersion(since = "0.0")
	open fun onBackPressed(): Boolean { return false }
	
	/**
	 * Get menu
	 */
	@SOSVersion(since = "0.0")
	fun getMenuBase() = menuBase
	
	companion object {
		const val SHOP_ID = "SHOP_ID"
	}
	
}