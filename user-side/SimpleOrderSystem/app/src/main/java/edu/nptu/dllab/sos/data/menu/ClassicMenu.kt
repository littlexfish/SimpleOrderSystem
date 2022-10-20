package edu.nptu.dllab.sos.data.menu

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.data.EventMenu
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.fragment.MenuFragment
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.ArrayValue
import org.msgpack.value.MapValue

/**
 * The classic menu type
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class ClassicMenu(shopId: Int, version: Int) : MenuBase(MenuType.CLASSIC, shopId, version) {
	
	@SOSVersion(since = "0.0")
	override fun getShopItem(itemId: String): Item {
		return Item(getShopId(), getMenuVersion(), itemId)
	}
	
	@SOSVersion(since = "0.0")
	override fun buildMenu(data: JsonObject) {
		TODO("Not yet implemented")
	}
	
	@SOSVersion(since = "0.0")
	override fun buildMenu(data: MapValue) {
		TODO("Not yet implemented")
	}
	
	override fun getMenuData(): MapValue {
		TODO("Not yet implemented")
	}
	
	override fun insertEvent(item: EventMenu.EventItem) {
		TODO("Not yet implemented")
	}
	
	override fun getEventData(): ArrayValue {
		TODO("Not yet implemented")
	}
	
	override fun getMenuFragment(): MenuFragment {
		return ClassicMenuFragment.newInstance(getShopId())
	}
	
	override fun getMenuFragmentClass(): Class<out MenuFragment> {
		return ClassicMenuFragment::class.java
	}
	
	
}