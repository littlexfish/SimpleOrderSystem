package edu.nptu.dllab.sos.data.menu.classic

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.data.EventMenu
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.fragment.ClassicMenuFragment
import edu.nptu.dllab.sos.fragment.MenuFragment
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.ArrayValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

private const val ITEM = "item"
private const val ITEM_CATEGORY = "category"
private const val ITEM_ITEM_ID = "itemId"
private const val ITEM_NAME = "name"
private const val ITEM_DES = "des"
private const val ITEM_PRICE = "price"
private const val ITEM_CURRENCY_CODE = "currencyCode"
private const val ITEM_ADDITION = "addition"
private const val ITEM_TAGS = "tags"
private const val ITEM_ID = "id"

private const val DEFAULT_CURRENCY_CODE = "NTD"

/**
 * The classic menu type
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class ClassicMenu(shopId: Int, version: Int) : MenuBase(MenuType.CLASSIC, shopId, version) {
	
	/**
	 * The root of folder node
	 */
	@SOSVersion(since = "0.0")
	private val root = Node("root", null)
	
	/**
	 * All items of this menu
	 */
	@SOSVersion(since = "0.0")
	private val items = HashMap<String, ClassicItem>()
	
	override fun getShopItem(itemId: String): ClassicItem {
		return items[itemId]!!
	}
	
	@Deprecated("use buildMenu(org.msgpack.value.MapValue)")
	override fun buildMenu(data: JsonObject) {
		// now do nothing
	}
	
	override fun buildMenu(data: MapValue) {
		val items = data.map()[ITEM.toStringValue()]!!.asArrayValue()
		
		for(i in items) {
			val item = i.asMap()
			
			val cate = item[ITEM_CATEGORY.toStringValue()]!!.asString()
			val itemId = item[ITEM_ITEM_ID.toStringValue()]!!.asString()
			val name = item[ITEM_NAME.toStringValue()]!!.asString()
			val des = item[ITEM_DES.toStringValue()]?.asString()
			val price = item[ITEM_PRICE.toStringValue()]!!.asDouble()
			val cc = item[ITEM_CURRENCY_CODE.toStringValue()]?.asString() ?: DEFAULT_CURRENCY_CODE
			val additions = item[ITEM_ADDITION.toStringValue()]!!.asArrayValue()
				.map { ClassicAddition.buildByValue(it) }
			val tags = item[ITEM_TAGS.toStringValue()]!!.asArrayValue().map { it.asString() }.toTypedArray()
			val resId = item[ITEM_ID.toStringValue()]!!.asInt()
			
			val ci = ClassicItem(getShopId(), cate, itemId, name, des, price, cc, additions, tags, resId)
			processNode(ci)
		}
		
	}
	
	/**
	 * Init node from classic item
	 */
	@SOSVersion(since = "0.0")
	private fun processNode(item: ClassicItem) {
		items[item.itemId] = item
		val cate = item.cate
		val spl = cate.split("/")
		var lastNode = root
		for(i in spl) {
			val next = lastNode.findPost(i)
			lastNode = if(next == null) {
				val node = Node(i, lastNode)
				lastNode.addNode(node)
				node
			} else next
		}
	}
	
	/**
	 * Get folder name and item id from category
	 * @return name and isFolder
	 */
	@SOSVersion(since = "0.0")
	fun getListFromCategory(cate: String): ArrayList<Pair<String, Boolean>> {
		val ret = ArrayList<Pair<String, Boolean>>()
		val node = if(cate.isEmpty()) {
			root
		}
		else {
			val spl = if(cate.startsWith("/")) cate.substring(1).split("/") else cate.split("/")
			var lastNode = root
			for(i in spl) {
				val next = lastNode.findPost(i)
				if(next != null) lastNode = next
				else return ret
			}
			lastNode
		}
		ret.addAll(node.getPosts().map { Pair(it, true) })
		ret.addAll(items.filterValues { it.cate == cate }.map { Pair(it.value.itemId, false) })
		return ret
	}
	
	override fun getMenuData(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.UpdateKey.MENU_TYPE.key.toStringValue(), type.name.toStringValue())
		map.put(ITEM.toStringValue(), ValueFactory.newArray(items.values.map { it.toMapData() }))
		return map.build()
	}
	
	override fun insertEvent(item: EventMenu.EventItem) {
		// TODO: Now do nothing
		// FIXME: insert event data into menu
	}
	
	override fun getEventData(): ArrayValue {
		// TODO: Now do nothing
		// FIXME: get event data
		return ValueFactory.newArray()
	}
	
	override fun getMenuFragment(): MenuFragment {
		return ClassicMenuFragment.newInstance(getShopId())
	}
	
	override fun getMenuFragmentClass(): Class<out MenuFragment> {
		return ClassicMenuFragment::class.java
	}
	
	override fun toString(): String {
		return "ClassicMenu { node=$root, items=${items.keys.toTypedArray().contentToString()} }"
	}
	
	/**
	 * A node class use for build item folder
	 */
	@SOSVersion(since = "0.0")
	inner class Node(val name: String, var pre: Node?) {
		
		/**
		 * Post nodes
		 */
		@SOSVersion(since = "0.0")
		private val post = HashSet<Node>()
		
		/**
		 * Add node to this node
		 */
		@SOSVersion(since = "0.0")
		fun addNode(node: Node): Boolean {
			node.pre = this
			return post.add(node)
		}
		
		/**
		 * Get post nodes of name
		 */
		@SOSVersion(since = "0.0")
		fun getPosts(): List<String> = post.map { it.name }
		
		/**
		 * Find post node by name
		 * @return node by name from this node, or `null` if not found
		 */
		@SOSVersion(since = "0.0")
		fun findPost(n: String): Node? {
			return post.firstOrNull { it.name == n }
		}
		
		override fun equals(other: Any?): Boolean {
			if(other == null) return false
			if(other !is Node) return false
			return other.name == name
		}
		
		override fun hashCode(): Int {
			return name.hashCode()
		}
		
		override fun toString(): String {
			if(post.isEmpty()) return name
			val sb = StringBuilder(name)
			sb.append("{")
			val postA = post.toList()
			for(i in postA.indices) {
				sb.append(postA[i].toString())
				if(i < postA.size - 1) sb.append(", ")
			}
			sb.append("}")
			return sb.toString()
		}
		
	}
	
}