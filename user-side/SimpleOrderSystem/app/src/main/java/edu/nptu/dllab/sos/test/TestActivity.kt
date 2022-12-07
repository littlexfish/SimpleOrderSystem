package edu.nptu.dllab.sos.test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.nptu.dllab.sos.MenuActivity
import edu.nptu.dllab.sos.OrderActivity
import edu.nptu.dllab.sos.OrderCheckActivity
import edu.nptu.dllab.sos.ShopSideActivity
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.data.menu.classic.ClassicItem
import edu.nptu.dllab.sos.data.pull.*
import edu.nptu.dllab.sos.databinding.ActivityTestBinding
import edu.nptu.dllab.sos.databinding.ActivityTestLinkBinding
import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.io.SocketHandler
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.test.frag.TestDownload
import edu.nptu.dllab.sos.test.frag.TestLink
import edu.nptu.dllab.sos.test.frag.TestOpenMenu
import edu.nptu.dllab.sos.util.StaticData
import org.intellij.lang.annotations.Language
import java.net.SocketException
import java.util.*

class TestActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityTestBinding
	
	private val chars = 32..126
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityTestBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.testConnect.setOnClickListener {
			startActivity(Intent(this, TestLinkActivity::class.java))
		}
		
		binding.testMenu.setOnClickListener {
			startActivity(Intent(this, MenuActivity::class.java).apply {
				putExtra(MenuActivity.EXTRA_SHOP_ID, -1)
			})
		}
		
		binding.testOrder.setOnClickListener {
			StaticData.clearItems()
			val count = (Math.random() * 30).toInt()
			for(i in 0 until count) {
				val name = getRandomName()
				val item = ClassicItem(-1, "0", name, name, null,
					            getRandomPrice(), "NTD", emptyList(), emptyArray(), -1)
				item.note = getRandomName(50, 10)
				StaticData.addItem(item)
			}
			startActivity(Intent(this, OrderActivity::class.java))
		}
		
		binding.testCheck.setOnClickListener {
			startActivity(Intent(this, OrderCheckActivity::class.java).apply {
//				putExtra(OrderCheckActivity.EXTRA_ORDER_ID, 2)
			})
		}
		
		binding.testClearOrder.setOnClickListener {
			@Language("SQL")
			val orderCreate =
				"CREATE TABLE IF NOT EXISTS ${DBHelper.TABLE_ORDER} (\n" +
				"   ${DBColumn.ORDER_AUTO_ID.columnName} INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
				"   ${DBColumn.ORDER_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
				"   ${DBColumn.ORDER_TIME.columnName} INTEGER DEFAULT -1,\n" +
				"   ${DBColumn.ORDER_STATUS.columnName} INTEGER DEFAULT -1,\n" +
				"   ${DBColumn.ORDER_REASON.columnName} TEXT DEFAULT NULL\n" +
				");"
			val db = DBHelper(applicationContext).writableDatabase
			db.execSQL("DROP TABLE ${DBHelper.TABLE_ORDER};")
			db.execSQL(orderCreate)
		}
		
		binding.testShopSide.setOnClickListener {
			startActivity(Intent(this, ShopSideActivity::class.java))
		}
		
	}
	
	private fun getRandomName(max: Int = 13, min: Int = 3): String {
		val randLength = (Math.random() * (max - min) + min).toInt()
		val sb = StringBuilder()
		for(i in 0 until randLength) {
			sb.append(chars.random().toChar())
		}
		return sb.toString()
	}
	
	private fun getRandomPrice(): Double {
		val randP = Math.random() * 500 * 100
		return randP.toInt().toDouble() / 100
	}
	
	class TestLinkActivity : AppCompatActivity() {
		
		private val frags =
			arrayOf(TestLink.newInstance(), TestOpenMenu.newInstance(), TestDownload.newInstance())
		
		private lateinit var binding: ActivityTestLinkBinding
		private var threadS = false
		
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			
			binding = ActivityTestLinkBinding.inflate(layoutInflater)
			setContentView(binding.root)
			
			binding.spinner.adapter = object : BaseAdapter() {
				val ips = arrayOf("192.168.0.235", "192.168.0.67", "203.64.134.96")
				val names = arrayOf("50872(dllab-lan)", "lf(dllab-lan)", "lf(dllab)")
				override fun getCount() = ips.size
				override fun getItem(position: Int) = ips[position]
				override fun getItemId(position: Int) = position.toLong()
				override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
					TextView(this@TestLinkActivity).also {
						it.text = names[position]
						it.textSize = 20f
					}
			}
			
			binding.button.setOnClickListener {
				if(StaticData.socketHandler.isConnected()) return@setOnClickListener
				StaticData.ensureSocketHandler(applicationContext, {
//					Thread {
//						while(!threadS) {
//							try {
//								val e = it.waitEvent()
//								runOnUiThread {
//									SocketHandler.checkErrorAndThrow(applicationContext, e as Event)
//									if(e is EventMenu) return@runOnUiThread
//									binding.textView.text = Objects.toString(e)
//								}
//							}
//							catch(e: SocketException) {
//								runOnUiThread {
//									finish()
//								}
//								break
//							}
//						}
//					}.start()
				})
			}
			
			binding.spinner2.adapter = object : BaseAdapter() {
				override fun getCount() = frags.size
				override fun getItem(position: Int) = frags[position]
				override fun getItemId(position: Int) = position.toLong()
				override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
					TextView(this@TestLinkActivity).also {
						it.text = frags[position]::class.simpleName
						it.textSize = 20f
					}
			}
			
			binding.spinner2.onItemSelectedListener = object : OnItemSelectedListener {
				override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
				                            id: Long) {
					val tra = supportFragmentManager.beginTransaction()
					tra.replace(binding.fragmentContainerView3.id, frags[position])
					tra.commit()
				}
				
				override fun onNothingSelected(parent: AdapterView<*>?) {}
			}
			
		}
		
		fun sendEvent(event: EventPusher) {
			StaticData.ensureSocketHandler(applicationContext, {
				it.pushEventRePush(event)
				
				it.waitEventAndRun { e ->
					runOnUiThread {
						SocketHandler.checkErrorAndThrow(applicationContext, e as Event)
						binding.textView.text = Objects.toString(e)
					}
					if(e is UpdateMenu) {
						it.waitEvent()
					}
				}
			})
		}
		
		override fun onDestroy() {
			super.onDestroy()
			threadS = true
		}
		
	}
	
}