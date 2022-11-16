package edu.nptu.dllab.sos

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.databinding.ActivitySettingsBinding
import edu.nptu.dllab.sos.io.Config
import edu.nptu.dllab.sos.io.Translator

class SettingsActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivitySettingsBinding
	private val oriLang = Config.getString(Config.Key.LANG)
	private val oriLOS = Config.getBoolean(Config.Key.LINK_ON_START)
	private val oriShowClose = Config.getBoolean(Config.Key.SHOW_CLOSED_SHOP)
	private val oriDistUnit = Config.getString(Config.Key.DISTANCE_UNIT)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivitySettingsBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		// text
		binding.settingsLangLabel.text = Translator.getString("settings.lang")
		binding.settingsLinkOnStart.text = Translator.getString("settings.linkOnStart")
		binding.settingsShopLabel.text = Translator.getString("settings.shop")
		binding.settingsShowClose.text = Translator.getString("settings.showClosedShop")
		binding.settingsDistUnitLabel.text = Translator.getString("settings.distUnit")
		binding.settingsConfirm.text = Translator.getString("settings.confirm")
		
		// spinner
		val tmpLang = ArrayList(Translator.listLangs(applicationContext))
		binding.settingsLang.adapter = object : BaseAdapter() {
			val langs = tmpLang.toTypedArray()
			override fun getCount(): Int = langs.size
			override fun getItem(position: Int) = langs[position]
			override fun getItemId(position: Int): Long = position.toLong()
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
				TextView(this@SettingsActivity).apply {
					text = Translator.getString("settings.lang.${getItem(position)}")
					setTextSize(TypedValue.COMPLEX_UNIT_PX,
					            resources.getDimensionPixelSize(R.dimen.settings_label_size)
						            .toFloat())
					setTextColor(ResourcesCompat.getColor(resources, R.color.text_black, null))
				}
			
			override fun getDropDownView(position: Int, convertView: View?,
			                             parent: ViewGroup?): View {
				return super.getDropDownView(position, convertView, parent).apply {
					setPadding(50, 10, 50, 10)
				}
			}
		}
		
		val tmpUnit = arrayOf("m", "ft")
		binding.settingsDistUnit.adapter = object : BaseAdapter() {
			val units = tmpUnit.copyOf()
			override fun getCount(): Int = units.size
			override fun getItem(position: Int): Any = units[position]
			override fun getItemId(position: Int): Long = position.toLong()
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
				TextView(this@SettingsActivity).apply {
					text = Translator.getString("dist.unit.${getItem(position)}")
					setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.settings_label_size).toFloat())
					setTextColor(ResourcesCompat.getColor(resources, R.color.text_black, null))
				}
			
			override fun getDropDownView(position: Int, convertView: View?,
			                             parent: ViewGroup?): View {
				return super.getDropDownView(position, convertView, parent).apply {
					setPadding(50, 10, 50, 10)
				}
			}
			
		}
		
		// set default
		binding.settingsLang.setSelection(tmpLang.indexOf(oriLang))
		binding.settingsLinkOnStart.isChecked = oriLOS
		binding.settingsShowClose.isChecked = oriShowClose
		binding.settingsDistUnit.setSelection(tmpUnit.indexOf(oriDistUnit))
		
		// button
		binding.settingsConfirm.setOnClickListener {
			if(anySettingsChange()) saveSettings()
			finish()
		}
		binding.settingsBack.setOnClickListener {
			if(anySettingsChange()) {
				AlertDialog.Builder(this)
					.setMessage(Translator.getString("settings.dialog.msg"))
					.setPositiveButton(Translator.getString("settings.dialog.confirm")) { _, _ ->
						saveSettings()
					}
					.setNegativeButton(Translator.getString("settings.dialog.cancel")) { _, _ ->
						finish()
					}
					.create().show()
			}
			else finish()
		}
	}
	
	private fun anySettingsChange(): Boolean {
		if(binding.settingsLang.selectedItem.toString() != oriLang) return true
		if(binding.settingsLinkOnStart.isChecked != oriLOS) return true
		if(binding.settingsShowClose.isChecked != oriShowClose) return true
		if(binding.settingsDistUnit.selectedItem.toString() != oriDistUnit) return true
		return false
	}
	
	private fun saveSettings() {
		Config.setConfig(Config.Key.LANG, binding.settingsLang.selectedItem.toString())
		Config.setConfig(Config.Key.LINK_ON_START, binding.settingsLinkOnStart.isChecked)
		Config.setConfig(Config.Key.SHOW_CLOSED_SHOP, binding.settingsShowClose.isChecked)
		Config.setConfig(Config.Key.DISTANCE_UNIT, binding.settingsDistUnit.selectedItem.toString())
		Config.saveConfig(applicationContext)
	}
	
}