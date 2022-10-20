package edu.nptu.dllab.sos.io

import android.content.Context
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object FileIO {
	
	private const val CRASH_DIR_STRING = "crash/"
	private const val MENU_DIR_STRING = "menu/"
	
	fun listCrashFiles(context: Context): Array<out File> {
		return File("${context.dataDir}/$CRASH_DIR_STRING").listFiles() ?: emptyArray()
	}
	
	fun hasMenu(context: Context, shopId: Int): Boolean {
		val file = File("${context.dataDir}/$MENU_DIR_STRING")
		return file.list()?.contains("$shopId.json") ?: false
	}
	
	fun getMenuFile(context: Context, shopId: Int): File {
		return File("${context.dataDir}/$MENU_DIR_STRING/$shopId.json")
	}
	
	fun newMenuFile() = MenuFile()
	
	fun newCrashFile() = CrashFile()
	
	abstract class SOSFile {
		
		protected open fun preWrite(context: Context) {}
		
		protected abstract fun onWrite(context: Context, fileName: String?): Boolean
		
		protected open fun postWrite(context: Context) {}
		
		fun write(context: Context, fileName: String) {
			postWrite(context)
			write(context, fileName)
			postWrite(context)
		}
		
	}
	
	class MenuFile : SOSFile() {
		
		private var data: Value = ValueFactory.newNil()
		
		fun setMenuData(value: Value) {
			data = value
		}
		
		override fun onWrite(context: Context, fileName: String?): Boolean {
			try {
				checkDir(context)
				val fn = fileName ?: "-1"
				val file = File("${context.dataDir}/$MENU_DIR_STRING/$fn.menu")
				if(!file.exists()) {
					if(file.createNewFile()) return false
				}
				val out = FileOutputStream(file)
				
				val packer = MessagePack.newDefaultPacker(out)
				packer.packValue(data)
				packer.flush()
				packer.close()
				
				out.close()
				return true
			}
			catch(e: IOException) {
				return false
			}
		}
		
		private fun checkDir(context: Context) {
			val d = File("${context.dataDir}/$CRASH_DIR_STRING")
			d.mkdirs()
		}
		
	}
	
	class CrashFile : SOSFile() {
		
		private var crashMessage = ""
		private var crashLine: String? = null
		private val throwable = ArrayList<Throwable>()
		
		fun addThrowable(thr: Throwable) {
			throwable.add(thr)
		}
		
		fun setCrashLine(line: String) {
			crashLine = line
		}
		
		fun setCrashMessage(message: String) {
			crashMessage = message
		}
		
		override fun preWrite(context: Context) {
			val d = File("${context.dataDir}/$CRASH_DIR_STRING")
			if(d.exists()) {
				val size = d.list()?.size ?: 0
				if(size > 5) {
					File(d.list()?.get(0) ?: "").delete()
				}
			}
		}
		
		override fun onWrite(context: Context, fileName: String?): Boolean {
			try {
				checkDir(context)
				val fn = fileName ?: getTimeAsName()
				val file = File("${context.dataDir}/$CRASH_DIR_STRING/$fn.crash")
				if(!file.exists()) {
					if(file.createNewFile()) return false
				}
				val out = FileOutputStream(file)
				
				out.write("\n$crashMessage".toByteArray())
				if(crashLine == null && throwable.isNotEmpty()) {
					out.write((crashLine ?: throwable[0].toString()).toByteArray())
				}
				out.write("\nStackTrace:\n".toByteArray())
				
				for(thr in throwable) {
					out.write(thr.stackTraceToString().toByteArray())
					out.write("\n".toByteArray())
				}
				
				out.close()
				return true
			}
			catch(e: IOException) {
				return false
			}
		}
		
		private fun checkDir(context: Context) {
			val d = File("${context.dataDir}/$CRASH_DIR_STRING")
			d.mkdirs()
		}
		
		private fun getTimeAsName(): String {
			return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault()).format(Date())
		}
		
	}
	
}