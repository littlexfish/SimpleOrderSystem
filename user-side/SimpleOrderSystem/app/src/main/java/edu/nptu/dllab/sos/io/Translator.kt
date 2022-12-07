package edu.nptu.dllab.sos.io

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
import edu.nptu.dllab.sos.util.Util
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

/**
 * The translator of this app
 *
 * @author Little Fish
 */
object Translator {
	
	private const val TAG = "Translator"
	
	/**
	 * The language directory
	 */
	private const val langDirName = "lang"
	
	/**
	 * The language name now load
	 */
	private var langNowLoad: String? = null
	
	/**
	 * The language map of this language
	 */
	private val langMap = HashMap<String, String>()
	
	/**
	 * Initialize language
	 */
	fun initTranslator(context: Context, lang: String) {
		if(context.resources.assets.list(langDirName)?.contains("$lang.json") == true) {
			remapTrans(context.resources.assets.open("$langDirName/$lang.json"), lang)
			return
		}
		val dataLangDir = context.getFileStreamPath(langDirName)
		if(!dataLangDir.exists()) {
			dataLangDir.mkdirs()
		}
		if(dataLangDir.list()?.contains("$lang.json") == true) {
			remapTrans(File("$dataLangDir/$lang.json").inputStream(), lang)
			return
		}
		Log.w(TAG, "language file not found: $lang")
	}
	
	fun listLangs(context: Context): Set<String> {
		val set = HashSet<String>()
		val assFiles = context.resources.assets.list(langDirName) ?: emptyArray()
		for(f in assFiles) {
			set.add(f.removeSuffix(".json"))
		}
		val dataFile = context.getFileStreamPath(langDirName)
		if(!dataFile.exists()) {
			dataFile.mkdirs()
		}
		val dataFiles = dataFile.list() ?: emptyArray()
		for(f in dataFiles) {
			set.add(f.removeSuffix(".json"))
		}
		return set
	}
	
	/**
	 * Process the language
	 */
	private fun remapTrans(inStream: InputStream, lang: String) {
		val bos = ByteArrayOutputStream()
		while(inStream.available() != 0) {
			bos.write(inStream.read())
		}
		inStream.close()
		val json = JsonParser.parseString(bos.toString(Util.CHARSET_GLOBAL.name()))
		val map = json.asJsonObject
		val remap = HashMap<String, String>()
		for((k, v) in map.entrySet()) {
			remap[k] = v.asString
		}
		langMap.clear()
		langMap.putAll(remap)
		langNowLoad = lang
	}
	
	/**
	 * Get language now load
	 */
	fun getLang() = langNowLoad
	
	/**
	 * Get translate string from key
	 */
	fun getString(key: String): String {
		return langMap[key] ?: key
	}
	
}