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

/**
 * A class to process file of this app
 */
object FileIO {
	
	/**
	 * The directory of crash file
	 */
	private const val CRASH_DIR_STRING = "crash/"
	
	/**
	 * The directory of menu file
	 */
	private const val MENU_DIR_STRING = "menu/"
	
	/**
	 * List crash files
	 */
	fun listCrashFiles(context: Context): Array<out File> {
		return File("${context.dataDir}/$CRASH_DIR_STRING").listFiles() ?: emptyArray()
	}
	
	/**
	 * Check the menu is exists
	 */
	fun hasMenu(context: Context, shopId: Int): Boolean {
		val file = File("${context.dataDir}/$MENU_DIR_STRING")
		return file.list()?.contains("$shopId.json") ?: false
	}
	
	/**
	 * Get menu file
	 */
	fun getMenuFile(context: Context, shopId: Int): File {
		return File("${context.dataDir}/$MENU_DIR_STRING/$shopId.json")
	}
	
	/**
	 * New a menu file
	 */
	fun newMenuFile() = MenuFile()
	
	/**
	 * New a crash file
	 */
	fun newCrashFile() = CrashFile()
	
	/**
	 * A base class of file in this app
	 */
	abstract class SOSFile {
		
		/**
		 * Call before write
		 */
		protected open fun preWrite(context: Context) {}
		
		/**
		 * Call on write
		 */
		protected abstract fun onWrite(context: Context, fileName: String?): Boolean
		
		/**
		 * Call on finished write
		 */
		protected open fun postWrite(context: Context) {}
		
		/**
		 * Call write the file
		 */
		fun write(context: Context, fileName: String) {
			postWrite(context)
			onWrite(context, fileName)
			postWrite(context)
		}
		
	}
	
	/**
	 * The menu file
	 */
	class MenuFile : SOSFile() {
		
		/**
		 * The menu data
		 */
		private var data: Value = ValueFactory.newNil()
		
		/**
		 * Set menu data
		 */
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
		
		/**
		 * Check directory is exists and auto create if not exists
		 */
		private fun checkDir(context: Context) {
			val d = File("${context.dataDir}/$CRASH_DIR_STRING")
			d.mkdirs()
		}
		
	}
	
	/**
	 * The crash file
	 */
	class CrashFile : SOSFile() {
		
		/**
		 * The crash message
		 */
		private var crashMessage = ""
		
		/**
		 * The crash line
		 */
		private var crashLine: String? = null
		
		/**
		 * The list of throwable
		 */
		private val throwable = ArrayList<Throwable>()
		
		/**
		 * Add throwable data
		 */
		fun addThrowable(thr: Throwable) {
			throwable.add(thr)
		}
		
		/**
		 * Set crash line
		 */
		fun setCrashLine(line: String) {
			crashLine = line
		}
		
		/**
		 * Set crash line
		 */
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