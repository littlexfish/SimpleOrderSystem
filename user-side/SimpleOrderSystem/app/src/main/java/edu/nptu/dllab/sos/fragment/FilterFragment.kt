package edu.nptu.dllab.sos.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.nptu.dllab.sos.R
import edu.nptu.dllab.sos.databinding.FragmentFilterBinding
import edu.nptu.dllab.sos.io.Translator
import java.util.Collections

/**
 * The fragment for filter the items
 *
 * @author Little Fish
 */
class FilterFragment : Fragment() {
	
	private lateinit var binding: FragmentFilterBinding
	
	/**
	 * Origin filter
	 */
	private var filterOri = HashSet<String>()
	
	/**
	 * The newest filter
	 */
	private var filterNow = HashSet<String>()
	
	/**
	 * All of filter
	 */
	private val allFilter = HashSet<String>()
	
	/**
	 * The close event listener
	 */
	private var close: ((Set<String>) -> Unit)? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentFilterBinding.inflate(inflater)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		refresh()
		binding.filterNowLabel.text = Translator.getString("filter.now")
		binding.filterAvailableLabel.text = Translator.getString("filter.available")
		binding.filterConfirm.text = Translator.getString("filter.confirm")
		binding.filterCancel.text = Translator.getString("filter.cancel")
		binding.filterConfirm.setOnClickListener {
			filterOri = HashSet(filterNow)
			close?.let { it1 -> it1(Collections.unmodifiableSet(filterNow)) }
		}
		binding.filterCancel.setOnClickListener {
			filterNow = HashSet(filterOri)
			close?.let { it1 -> it1(Collections.unmodifiableSet(filterNow)) }
		}
	}
	
	/**
	 * Set on close listener
	 */
	fun setOnClose(func: ((Set<String>) -> Unit)?) {
		close = func
	}
	
	/**
	 * Set the all filter
	 */
	fun setAllFilter(s: List<String>) {
		allFilter.clear()
		allFilter.addAll(s)
		buildFilter()
	}
	
	/**
	 * Build the filter
	 */
	private fun buildFilter() {
		for(f in filterNow) {
			if(f !in allFilter) {
				filterNow.remove(f)
			}
		}
		
		for(f in filterOri) {
			if(f !in allFilter) {
				filterOri.remove(f)
			}
		}
	}
	
	/**
	 * Refresh the filter view
	 */
	private fun refresh() {
		binding.filterList.removeAllViews()
		for(f in allFilter) {
			if(f !in filterNow) {
				val place = TextView(context)
				place.layoutParams = LinearLayout.LayoutParams(-1, 50)
				binding.filterList.addView(place)
				val v = FilterItem(requireContext(), f)
				v.setOnClickListener {
					allFilter.remove(f)
					filterNow.add(f)
					refresh()
				}
				binding.filterList.addView(v)
			}
		}
		binding.filterNow.removeAllViews()
		for(f in filterNow) {
			val place = TextView(context)
			place.layoutParams = LinearLayout.LayoutParams(50, -1)
			binding.filterNow.addView(place)
			val v = FilterItem(requireContext(), f)
			v.setOnClickListener {
				allFilter.add(f)
				filterNow.remove(f)
				refresh()
			}
			binding.filterNow.addView(v)
		}
	}
	
	/**
	 * The filter item
	 */
	@SuppressLint("ViewConstructor")
	class FilterItem(context: Context, str: String) : FrameLayout(context) {
		
		private val imgPadding = 20
		
		init {
			setPadding(imgPadding * 2, imgPadding, imgPadding * 2, imgPadding)
			setBackgroundResource(R.drawable.round_background)
			
			val text = TextView(context)
			text.text = str
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
			text.layoutParams = LayoutParams(-1, -1)
			text.gravity = Gravity.CENTER
			addView(text)
		}
		
	}
	
}