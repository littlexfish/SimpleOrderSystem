package edu.nptu.dllab.sos.data.menu

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.data.pull.EventMenu
import edu.nptu.dllab.sos.fragment.MenuFragment
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.ArrayValue
import org.msgpack.value.MapValue

/**
 * Menu type base
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
abstract class MenuBase(protected val type: MenuType, private val shopId: Int,
                        private val version: Int) {
	
	/**
	 * Get [OrderItem] by item id
	 * @param itemId - the item id
	 */
	@SOSVersion(since = "0.0")
	abstract fun getShopItem(itemId: String): OrderItem
	
	/**
	 * Build menu by json object
	 * @param data - [JsonObject]
	 */
	@SOSVersion(since = "0.0")
	abstract fun buildMenu(data: JsonObject)
	
	/**
	 * Build menu by map value
	 * @param data - [MapValue]
	 */
	@SOSVersion(since = "0.0")
	abstract fun buildMenu(data: MapValue)
	
	/**
	 * Get full msgpack of this menu
	 */
	abstract fun getMenuData(): MapValue
	
	/**
	 * Insert event menu by [EventMenu.EventItem]
	 */
	@SOSVersion(since = "0.0")
	abstract fun insertEvent(item: EventMenu.EventItem)
	
	/**
	 * Insert all event
	 */
	@SOSVersion(since = "0.0")
	fun insertAllEvent(items: Iterable<EventMenu.EventItem>) {
		for(i in items) {
			insertEvent(i)
		}
	}
	
	/**
	 * Get full msgpack of event menu
	 */
	@SOSVersion(since = "0.0")
	abstract fun getEventData(): ArrayValue
	
	/**
	 * Get menu type fragment
	 */
	@SOSVersion(since = "0.0")
	abstract fun getMenuFragment(): MenuFragment
	
	@SOSVersion(since = "0.0")
	abstract fun getMenuFragmentClass(): Class<out MenuFragment>
	
	/**
	 * Get shop id
	 */
	@SOSVersion(since = "0.0")
	fun getShopId() = shopId
	
	/**
	 * Get menu version
	 */
	@SOSVersion(since = "0.0")
	fun getMenuVersion() = version
	
	/**
	 * Get menu type
	 * @see [MenuType]
	 */
	@SOSVersion(since = "0.0")
	fun getMenuType() = type
	
	/**
	 * Menu type
	 */
	@SOSVersion(since = "0.0")
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
		 * @param data - menu data as [JsonObject]
		 */
		@SOSVersion(since = "0.0")
		@Deprecated("use buildByType(MenuType, Int, Int, MapValue): MenuBase")
		fun buildByType(type: MenuType, shopId: Int, version: Int, data: JsonObject): MenuBase {
			val menu = when(type) {
				MenuType.CLASSIC -> ClassicMenu(shopId, version)
				MenuType.CUSTOM -> throw UnsupportedOperationException()
			}
			menu.buildMenu(data)
			return menu
		}
		
		/**
		 * Get menu has been build
		 * @param type - [MenuType]
		 * @param shopId - the shop id
		 * @param version - the menu version
		 * @param data - menu data as [MapValue]
		 */
		@SOSVersion(since = "0.0")
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