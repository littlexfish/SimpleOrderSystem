package edu.nptu.dllab.sos.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import edu.nptu.dllab.sos.data.*
import edu.nptu.dllab.sos.data.pull.EventMenu
import edu.nptu.dllab.sos.data.pull.NearShop
import edu.nptu.dllab.sos.data.pull.ResourceDownload
import edu.nptu.dllab.sos.data.pull.UpdateMenu
import edu.nptu.dllab.sos.databinding.ActivityTestBinding
import edu.nptu.dllab.sos.test.frag.TestDownload
import edu.nptu.dllab.sos.test.frag.TestLink
import edu.nptu.dllab.sos.test.frag.TestOpenMenu
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toByteArray
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.*


private const val EVENT_PULL_NEAR_SHOP = "near_shop"
private const val EVENT_PULL_UPDATE = "update"
private const val EVENT_PULL_EVENT_MENU = "event_menu"
private const val EVENT_PULL_RESOURCE = "resource"

class TestActivity : AppCompatActivity() {
	
	private val frags = arrayOf(TestLink.newInstance(),
	                            TestOpenMenu.newInstance(),
	                            TestDownload.newInstance())
	
	private lateinit var binding: ActivityTestBinding
	private lateinit var socket: Socket
	private val thread = HandlerThread("net").also { it.start() }
	private val handler = Handler(thread.looper)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityTestBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.spinner.adapter = object : BaseAdapter() {
			val ips = arrayOf("192.168.0.235", "192.168.0.67", "203.64.134.96")
			val names = arrayOf("50872(dllab-lan)", "lf(dllab-lan)", "lf(dllab)")
			override fun getCount() = ips.size
			override fun getItem(position: Int) = ips[position]
			override fun getItemId(position: Int) = position.toLong()
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = TextView(this@TestActivity).also {
				it.text = names[position]
				it.textSize = 20f
			}
		}
		
		binding.button.setOnClickListener {
			if(::socket.isInitialized) return@setOnClickListener
			handler.post {
				socket = Socket(binding.spinner.selectedItem.toString(), 25000)
				Thread {
					while(socket.isConnected) {
						val e = getEvent()
						runOnUiThread {
							binding.textView.text = Objects.toString(e)
						}
						if(e == null) {
							runOnUiThread {
								finish()
							}
							break
						}
					}
				}.start()
			}
		}
		
		binding.spinner2.adapter = object : BaseAdapter() {
			override fun getCount() = frags.size
			override fun getItem(position: Int) = frags[position]
			override fun getItemId(position: Int) = position.toLong()
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = TextView(this@TestActivity).also {
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
	
	private fun getEvent(): EventPuller? {
		try {
			val ins = socket.getInputStream()
			// get the pack size, use 4 bytes
			val intBs = ByteBuffer.allocate(4)
			for(i in 0 until 4) {
				intBs.put(ins.read().toByte())
			}
			val size = intBs.getInt(0)
			// force read to size of pack
			val bos = ByteArrayOutputStream()
			for(i in 0 until size) {
				bos.write(ins.read())
			}
			// unpack from byte array
			val unpacker = MessagePack.newDefaultUnpacker(ByteArrayInputStream(bos.toByteArray()))
			return getEventValue(unpacker.unpackValue())
		}
		catch(e: SocketException) {
			Log.e("Test", "", e)
		}
		return null
	}
	
	fun sendEvent(event: EventPusher) {
		if(!::socket.isInitialized) return
		handler.post {
			try {
				val value = event.toValue()
				val bos = ByteArrayOutputStream()
				val packer = MessagePack.newDefaultPacker(bos)
				packer.packValue(value)
				packer.flush()
				val array = bos.toByteArray()
				val size = array.size.toByteArray()
				
				socket.getOutputStream().write(size)
				socket.getOutputStream().write(array)
			}
			catch(e: SocketException) {
				Log.e("Test", "", e)
			}
		}
	}
	
	private fun getEventValue(value: Value): EventPuller {
		Util.checkMapValue(value)
		val map = value.asMap()
		val evt = map["event".toStringValue()]
		if(evt?.isStringValue != true) throw IllegalStateException("no event or event not string")
		val e = when(evt.asString()) {
			EVENT_PULL_NEAR_SHOP -> NearShop()
			EVENT_PULL_UPDATE -> UpdateMenu()
			EVENT_PULL_EVENT_MENU -> EventMenu()
			EVENT_PULL_RESOURCE -> ResourceDownload()
			else -> throw Exceptions.EventNotFoundException(map["event".toStringValue()]?.asString() ?: "null")
		}
		e.fromValue(value)
		return e
	}
	
	override fun onDestroy() {
		super.onDestroy()
		if(::socket.isInitialized && socket.isConnected) {
			socket.close()
		}
	}
	
}