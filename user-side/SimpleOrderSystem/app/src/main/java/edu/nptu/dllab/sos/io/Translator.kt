package edu.nptu.dllab.sos.io

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
import edu.nptu.dllab.sos.util.Util
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object Translator {
	
	private const val TAG = "Translator"
	
	private const val langDirName = "lang"
	private var langNowLoad: String? = null
	private val langMap = HashMap<String, String>()
	
	fun initTranslator(context: Context, lang: String) {
		if(context.resources.assets.list(langDirName)?.contains("$lang.json") == true) {
			remapTrans(context.resources.assets.open("$langDirName/$lang.json"), lang)
			return
		}
		val dataLangDir = File("${context.dataDir}/$langDirName")
		if(!dataLangDir.exists()) {
			dataLangDir.mkdirs()
		}
		if(dataLangDir.list()?.contains("$lang.json") == true) {
			remapTrans(File("$dataLangDir/$lang.json").inputStream(), lang)
			return
		}
		Log.w(TAG, "language file not found: $lang")
	}
	
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
	
	fun getLang() = langNowLoad
	
	fun getString(key: String): String {
		return langMap[key] ?: key
	}
	
}