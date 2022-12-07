package edu.nptu.dllab.sos.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import edu.nptu.dllab.sos.ItemOrderActivity
import edu.nptu.dllab.sos.MenuActivity
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.data.menu.MenuBase
import edu.nptu.dllab.sos.data.menu.classic.ClassicItem
import edu.nptu.dllab.sos.data.menu.classic.ClassicMenu
import edu.nptu.dllab.sos.databinding.FragmentClassicMenuBinding
import edu.nptu.dllab.sos.io.ResourceReader
import edu.nptu.dllab.sos.view.classic.CategoryView
import edu.nptu.dllab.sos.view.classic.ItemBlock
import edu.nptu.dllab.sos.view.classic.ItemFolder

/**
 * A fragment class of classic type menu
 */
class ClassicMenuFragment : MenuFragment() {
	
	private lateinit var binding: FragmentClassicMenuBinding
	
	private val requestSelectItem =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			when(it.resultCode) {
				ItemOrderActivity.RESULT_ADD -> {
					(activity as? MenuActivity)?.animCart()
				}
				ItemOrderActivity.RESULT_CANCEL -> {
				
				}
			}
		}
	
	/**
	 * The category path
	 */
	private var nowCate = ArrayList<String>()
	
	/**
	 * The classic menu attach from
	 */
	private var classic: ClassicMenu? = null
	
	/**
	 * The resource data uses
	 */
	private val resData = HashMap<Int, Bitmap?>()
	
	private var refresh = false
	
	private lateinit var filterFrag: FilterFragment
	private var filter: Set<String> = HashSet()
	
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
		
		filterFrag = FilterFragment()
		parentFragmentManager.beginTransaction().replace(R.id.classicFilterFrag, filterFrag).commit()
		filterFrag.setOnClose {
			filter = it
			refreshScreen()
			binding.classicFilterFrag.visibility = View.GONE
		}
		
		binding.classicFilter.setOnClickListener {
			binding.classicFilterFrag.visibility = View.VISIBLE
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
	
	override fun reloadResource() {
		val rs = ResourceReader.getResourcesAsBitmap(requireContext(), shopId)
		resData.clear()
		for((id, bit) in rs) {
			resData[id] = bit
		}
		refreshScreen()
	}
	
	/**
	 * Refresh the screen with current category
	 */
	private fun refreshScreen() {
		if(!::binding.isInitialized || classic == null) {
			refresh = true
			return
		}
		// get data
		val cate = nowCate.joinToString("/")
		val tmpList = classic!!.getListFromCategory(cate)
		val tmpPairList = ArrayList(classic!!.getListFromCategory(cate))
		for(p in tmpList) {
			if(!p.second) {
				val i = p.first as ClassicItem
				for(f in filter) {
					if(f !in i.tags) {
						tmpPairList.remove(p)
						break
					}
				}
			}
		}
		
		val folderSet = HashSet<Pair<Any, Boolean>>()
		val itemSet = HashSet<Pair<Any, Boolean>>()
		
		for(i in tmpPairList) {
			if(i.second) folderSet.add(i)
			else itemSet.add(i)
		}
		
		val pairList = ArrayList<Pair<Any, Boolean>>()
		pairList.addAll(folderSet)
		pairList.addAll(itemSet)
		
		// refresh main category
		binding.menuClassicCate.removeAllViews()
		for(c in classic!!.getListFromCategory("")) {
			if(c.second) {
				val cv = CategoryView(requireContext())
				cv.string = c.first.toString()
				cv.setOnClickListener {
					nowCate = arrayListOf(c.first.toString())
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
		
		// refresh filter
		filterFrag.setAllFilter(classic!!.getContainTags())
		
		// refresh items
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
					val view = if(item.second) ItemFolder(requireContext(), this, item.first.toString())
					else {
						val i = item.first as ClassicItem
						val vi = ItemBlock(requireContext(), this, i.itemId, i.display, resData[i.resId], i.price)
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
	fun inFolder(name: String) {
		nowCate.add(name)
		refreshScreen()
	}
	
	/**
	 * Use for click on a item
	 */
	fun clickItem(id: String) {
		ItemOrderActivity.selectedItem = classic?.getShopItem(id)?.clone()
		requestSelectItem.launch(Intent(requireActivity(), ItemOrderActivity::class.java))
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