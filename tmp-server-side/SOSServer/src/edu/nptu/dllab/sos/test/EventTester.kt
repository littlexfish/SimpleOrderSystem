package edu.nptu.dllab.sos.test

import edu.nptu.dllab.sos.event.*
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.awt.BorderLayout
import java.awt.GridLayout
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import javax.swing.*

fun main() {
	EventTester()
}

class EventTester {
	
	private val frame: TestFrame
	
	init {
		frame = TestFrame()
		frame.isVisible = true
	}
	
	inner class TestFrame : JFrame("Tester") {
		
		init {
			val panel = TestRootPanel(this)
			contentPane = panel
			defaultCloseOperation = EXIT_ON_CLOSE
			pack()
		}
		
	}
	
	class TestRootPanel(private val frame: JFrame) : JPanel(BorderLayout()) {
		
		private var socket: Socket? = null
		private var isConnected = false
		private var nowPanel: EventPanel? = null
		
		private var ip = ""
		private var port = 0
		
		private val info: JTextField
		
		init {
			run {
				val dialog = JDialog(frame, true)
				dialog.contentPane.layout = GridLayout(0, 1)
				val ipInput = JTextField("192.168.0.235")
				dialog.contentPane.add(ipInput)
				val portInput = JTextField("25000")
				dialog.contentPane.add(portInput)
				val b = JButton("Confirm")
				dialog.contentPane.add(b)
				b.addActionListener {
					try {
						ip = ipInput.text
						port = portInput.text.toInt()
						dialog.dispose()
					}
					catch(e: Exception) {
					}
				}
				dialog.pack()
				dialog.isVisible = true
			}
			
			val topP = JPanel(BorderLayout())
			add(topP, BorderLayout.NORTH)
			
			val selectBox = JComboBox<EventPanel>()
			topP.add(selectBox, BorderLayout.CENTER)
			
			selectBox.model = DefaultComboBoxModel(eventPanels)
			selectBox.addItemListener {
				if(nowPanel != null) remove(nowPanel)
				add(it.item as EventPanel, BorderLayout.CENTER)
				nowPanel = it.item as EventPanel
				frame.pack()
			}
			
			val linkBtn = JButton("Connect")
			topP.add(linkBtn, BorderLayout.WEST)
			
			linkBtn.addActionListener {
				if(socket == null || !isConnected) {
					socket?.close()
					socket = Socket(ip, port)
					linkBtn.text = "Close"
					isConnected = true
					startThread()
				}
				else {
					socket?.close()
					linkBtn.text = "Connect"
					socket = null
					isConnected = false
				}
			}
			
			
			info = JTextField()
			info.isEditable = false
			add(info, BorderLayout.SOUTH)
			
			add(eventPanels[0], BorderLayout.CENTER)
			nowPanel = eventPanels[0]
		}
		
		private fun startThread() {
			Thread {
				while(isConnected) {
					val event = receive()
					if(event != null) {
						info.text = event.msgPack?.toJson() ?: "null"
						println(event.msgPack)
					}
				}
			}.start()
		}
		
		fun send(event: EventSender) {
			if(socket != null) {
				val buffer = ByteArrayOutputStream()
				val packer = MessagePack.newDefaultPacker(buffer)
				packer.packValue(event.toMsgPack())
				packer.flush()
				val array = buffer.toByteArray()
				val sizeBuffer = ByteBuffer.allocate(4)
				sizeBuffer.putInt(array.size)
				val size = sizeBuffer.array()
				
				val out = socket!!.getOutputStream()
				out.write(size)
				out.write(array)
			}
		}
		
		fun receive(): Event? {
			if(socket != null) {
				val ins = socket!!.getInputStream()
				val sizeBuffer = ByteArray(4)
				ins.read(sizeBuffer)
				val size = ByteBuffer.wrap(sizeBuffer).int
				
				val buffer = ByteArray(size)
				ins.read(buffer)
				
				val bis = ByteArrayInputStream(buffer)
				val unpacker = MessagePack.newDefaultUnpacker(bis)
				return value2Event(unpacker.unpackValue())
			}
			return null
		}
		
		private fun value2Event(value: Value): Event? {
			val map = value.asMapValue().map()
			val evt: Event? = when(map[ValueFactory.newString("event")]!!.asStringValue().toString()) {
				"link" -> LinkEvent()
				"near_shop" -> NearShopEvent()
				"open_menu" -> OpenMenuEvent()
				"update" -> UpdateEvent()
				"event_menu" -> EventMenuEvent()
				"download" -> DownloadEvent()
				"resource" -> ResourceEvent()
				else -> null
			}
			(evt as? EventReceiver)?.fromMsgPack(value)
			return evt
		}
		
		companion object {
			val eventPanels = arrayOf(
				LinkEventPanel(),
				OpenMenuPanel(),
				DownloadPanel())
		}
		
	}
	
}