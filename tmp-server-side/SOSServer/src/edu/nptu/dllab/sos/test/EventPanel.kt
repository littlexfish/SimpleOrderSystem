package edu.nptu.dllab.sos.test

import edu.nptu.dllab.sos.event.DownloadEvent
import edu.nptu.dllab.sos.event.LinkEvent
import edu.nptu.dllab.sos.event.OpenMenuEvent
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.ScrollPane
import javax.swing.*

abstract class EventPanel(val event: String) : JPanel() {
	override fun toString(): String {
		return event
	}
}


class LinkEventPanel : EventPanel("link") {
	
	init {
		layout = GridLayout(0, 1)
		
		val jsX = JSpinner()
		jsX.model = SpinnerNumberModel(0.0, 0.0, 10000.0, 0.5)
		add(jsX)
		
		val jsY = JSpinner()
		jsY.model = SpinnerNumberModel(0.0, 0.0, 10000.0, 0.5)
		add(jsY)
		
		val btn = JButton("send")
		add(btn)
		btn.addActionListener {
			val root = parent as EventTester.TestRootPanel
			
			val event = LinkEvent()
			event.pX = jsX.value as Double
			event.pY = jsY.value as Double
			root.send(event)
		}
	}
	
}

class OpenMenuPanel : EventPanel("open_menu") {
	
	init {
		layout = GridLayout(0, 1)
		
		val shop = JSpinner()
		shop.model = SpinnerNumberModel(0, 0, 10000, 1)
		add(shop)
		
		val ver = JSpinner()
		ver.model = SpinnerNumberModel(0, 0, 10000, 1)
		add(ver)
		
		val btn = JButton("send")
		add(btn)
		btn.addActionListener {
			val root = parent as EventTester.TestRootPanel
			
			val event = OpenMenuEvent()
			event.shopId = shop.value as Int
			event.menuVersion = shop.value as Int
			root.send(event)
		}
	}
	
}

class DownloadPanel : EventPanel("download") {
	
	init {
		layout = BorderLayout()
		
		val text = JTextArea("每一行為一項")
		val scroll = JScrollPane(text)
		add(scroll, BorderLayout.CENTER)
		
		val btn = JButton("send")
		add(btn, BorderLayout.SOUTH)
		btn.addActionListener {
			val root = parent as EventTester.TestRootPanel
			
			val event = DownloadEvent()
			val spl = text.text.split(Regex("[\r\n]"))
			for(s in spl) {
				event.paths.add(s)
			}
			root.send(event)
		}
		
	}
	
}