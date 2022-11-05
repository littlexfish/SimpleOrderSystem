package edu.nptu.dllab.sos.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.databinding.FragmentClassicMenuBinding
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.view.classic.CategoryView
import edu.nptu.dllab.sos.view.classic.ItemBlock
import edu.nptu.dllab.sos.view.classic.ItemFolder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A fragment class of classic type menu
 */
@SOSVersion(since = "0.0")
class ClassicMenuFragment : MenuFragment() {
	
	private lateinit var binding: FragmentClassicMenuBinding
	
	/**
	 * The category path
	 */
	@SOSVersion(since = "0.0")
	private var nowCate = ArrayList<String>()
	
	/**
	 * The classic menu attach from
	 */
	@SOSVersion(since = "0.0")
	private var classic: ClassicMenu? = null
	
	/**
	 * The resource data uses
	 */
	private val resData = HashMap<Int, Bitmap?>()
	
	private var refresh = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			shopId = it.getInt(SHOP_ID)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View { // Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_classic_menu, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding = FragmentClassicMenuBinding.bind(view)
		
		binding.classicFilter.setOnClickListener {
			// TODO: open filter
		}
		
		if(refresh) {
			refreshScreen()
			refresh = false
		}
	}
	
	override fun buildMenu(menu: MenuBase) {
		super.buildMenu(menu)
		classic = menu as ClassicMenu
		
		refreshScreen()
	}
	
	/**
	 * Refresh the screen with current category
	 */
	@SOSVersion(since = "0.0")
	private fun refreshScreen() {
		// refresh main category
		if(!::binding.isInitialized || classic == null) {
			refresh = true
			return
		}
		binding.menuClassicCate.removeAllViews()
		val mc = classic!!.getListFromCategory("")
		for(cate in mc) {
			if(cate.second) {
				val cv = CategoryView(requireContext())
				cv.string = cate.first
				cv.setOnClickListener {
					nowCate = arrayListOf(cate.first)
					refreshScreen()
				}
				binding.menuClassicCate.addView(cv)
			}
		}
		val place = TextView(context)
		place.text = ""
		val dimen = resources.getDimensionPixelSize(R.dimen.menu_classic_category)
		place.layoutParams = LinearLayout.LayoutParams(dimen, dimen)
		binding.menuClassicCate.addView(place)
		
		// refresh items
		val cate = nowCate.joinToString("/")
		val pairList = classic!!.getListFromCategory(cate)
		val vCount = pairList.size / 2 + if(pairList.size % 2 == 0) 0 else 1
		val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
		
		binding.menuClassicItems.removeAllViews()
		
		for(v in 0 until vCount) {
			val hLayout = LinearLayout(context)
			hLayout.orientation = LinearLayout.HORIZONTAL
			hLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			
			for(h in 0..1) {
				val index = v * 2 + h
				if(index >= pairList.size) {
					val empty = TextView(context)
					empty.text = ""
					empty.layoutParams = param
					hLayout.addView(empty)
				}
				else {
					val item = pairList[index]
					val view = if(item.second) ItemFolder(requireContext(), this, item.first)
					else  {
						val i = classic!!.getShopItem(item.first)
						val vi = ItemBlock(requireContext(), this, i.itemId, i.name, resData[i.resId])
						vi
					}
					view.layoutParams = param
					hLayout.addView(view)
				}
			}
			
			binding.menuClassicItems.addView(hLayout)
			
		}
		refresh = false
	}
	
	/**
	 * Use for get in folder
	 */
	@SOSVersion(since = "0.0")
	fun inFolder(name: String) {
		nowCate.add(name)
		refreshScreen()
	}
	
	/**
	 * Use for click on a item
	 */
	@SOSVersion(since = "0.0")
	fun clickItem(id: String) {
		// TODO: get in item detail
	}
	
	override fun onBackPressed(): Boolean {
		if(nowCate.isNotEmpty()) {
			nowCate.removeAt(nowCate.lastIndex)
			refreshScreen()
			return true
		}
		return false
	}
	
	companion object {
		fun newInstance(shopId: Int) = ClassicMenuFragment().apply {
			arguments = Bundle().apply {
				putInt(SHOP_ID, shopId)
			}
		}
	}
}