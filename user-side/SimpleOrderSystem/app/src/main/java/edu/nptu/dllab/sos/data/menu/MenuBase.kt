package edu.nptu.dllab.sos.data.menu

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.data.pull.EventMenu
import edu.nptu.dllab.sos.fragment.MenuFragment
import org.msgpack.value.ArrayValue
import org.msgpack.value.MapValue

/**
 * Menu type base
 *
 * @author Little Fish
 */
abstract class MenuBase(protected val type: MenuType, private val shopId: Int,
                        private val version: Int) {
	
	/**
	 * Get [OrderItem] by item id
	 * @param itemId - the item id
	 */
	abstract fun getShopItem(itemId: String): OrderItem
	
	/**
	 * Build menu by map value
	 * @param data - [MapValue]
	 */
	abstract fun buildMenu(data: MapValue)
	
	/**
	 * Get full msgpack of this menu
	 */
	abstract fun getMenuData(): MapValue
	
	/**
	 * Insert event menu by [EventMenu.EventItem]
	 */
	abstract fun insertEvent(item: EventMenu.EventItem)
	
	/**
	 * Insert all event
	 */
	fun insertAllEvent(items: Iterable<EventMenu.EventItem>) {
		for(i in items) {
			insertEvent(i)
		}
	}
	
	/**
	 * Get full msgpack of event menu
	 */
	abstract fun getEventData(): ArrayValue
	
	/**
	 * Get menu type fragment
	 */
	abstract fun getMenuFragment(): MenuFragment
	
	/**
	 * Get class of menu type fragment
	 */
	abstract fun getMenuFragmentClass(): Class<out MenuFragment>
	
	/**
	 * Get shop id
	 */
	fun getShopId() = shopId
	
	/**
	 * Get menu version
	 */
	fun getMenuVersion() = version
	
	/**
	 * Get menu type
	 * @see [MenuType]
	 */
	fun getMenuType() = type
	
	/**
	 * Menu type
	 */
	enum class MenuType {
		/**
		 * The classic type menu
		 */
		CLASSIC,
		
		/**
		 * The custom type menu
		 */
		CUSTOM;
		
		companion object {
			/**
			 * Get type by any string
			 */
			fun getTypeByString(str: String) = valueOf(str.uppercase())
		}
	}
	
	companion object {
		/**
		 * Get menu has been build
		 * @param type - [MenuType]
		 * @param shopId - the shop id
		 * @param version - the menu version
		 * @param data - menu data as [MapValue]
		 */
		fun buildByType(type: MenuType, shopId: Int, version: Int, data: MapValue): MenuBase {
			val menu = when(type) {
				MenuType.CLASSIC -> ClassicMenu(shopId, version)
				MenuType.CUSTOM -> ClassicMenu(shopId, version)
			}
			menu.buildMenu(data)
			return menu
		}
	}
}