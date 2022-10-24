package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.ResourceKey.*
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.security.MessageDigest

/**
 * The event to check resource download
 *
 * @author Little Fish
 * @since 20/10/03
 */
@SOSVersion(since = "0.0")
class ResourceDownload : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The file index of this download request
	 */
	@SOSVersion(since = "0.0")
	var fileIndex = -1
	
	/**
	 * The total file count of this download request
	 */
	@SOSVersion(since = "0.0")
	var fileTotal = -1
	
	/**
	 * The server path of this resource
	 */
	@SOSVersion(since = "0.0")
	var path = ""
	
	/**
	 * The sha256 of this resource data
	 */
	@SOSVersion(since = "0.0")
	var sha256 = ""
	
	/**
	 * The resource data length of bytes
	 */
	@SOSVersion(since = "0.0")
	var size = 0
	
	/**
	 * The resource data
	 */
	@SOSVersion(since = "0.0")
	var resData = ByteArray(0)
	
	/**
	 * Use to check sha256 is equal
	 */
	@SOSVersion(since = "0.0")
	fun checkSha256() = getDataSha256() == getCurSha256()
	
	/**
	 * Get sha256 of data
	 * @return sha256 as hex string
	 */
	@SOSVersion(since = "0.0")
	fun getDataSha256(): String {
		val digest = MessageDigest.getInstance("SHA-256")
		val hashBytes = digest.digest(resData)
		return ba2hs(hashBytes)
	}
	
	/**
	 * Get sha256 of server given
	 * @return sha256 as hex string
	 */
	@SOSVersion(since = "0.0")
	fun getCurSha256() = sha256
	
	@Deprecated("use fromValue(Value)")
	override fun fromJson(json: JsonElement) {
		val data = Util.checkJsonObject(json)
		fileIndex = data[FILE_INDEX.key].asInt
		fileTotal = data[FILE_TOTAL.key].asInt
		path = data[PATH.key].asString
		sha256 = data[SHA256.key].asString
		size = data[SIZE.key].asInt
	}
	
	override fun fromValue(value: Value) {
		val data = Util.checkMapValue(value).map()
		fileIndex = data[FILE_INDEX.key.toStringValue()]!!.asInt()
		fileTotal = data[FILE_TOTAL.key.toStringValue()]!!.asInt()
		path = data[PATH.key.toStringValue()]!!.asString()
		sha256 = data[SHA256.key.toStringValue()]!!.asString()
		resData = data[DATA.key.toStringValue()]!!.asBinaryValue().asByteArray()
		size = resData.size
	}
	
	/**
	 * Byte array change to hex string
	 */
	@SOSVersion(since = "0.0")
	private fun ba2hs(byteArray: ByteArray): String {
		val sb = StringBuilder()
		for(i in byteArray) {
			val hex = Integer.toHexString(i.toInt())
			sb.append(hex.substring(hex.length - 2))
		}
		return sb.toString()
	}
	
	override fun toString(): String {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(FILE_INDEX.key.toStringValue(), fileIndex.toIntegerValue())
		map.put(FILE_TOTAL.key.toStringValue(), fileTotal.toIntegerValue())
		map.put(PATH.key.toStringValue(), path.toStringValue())
		map.put(SHA256.key.toStringValue(), sha256.toStringValue())
		map.put(DATA.key.toStringValue(), ValueFactory.newBinary(resData))
		return map.build().toString()
	}
	
	companion object {
		const val EVENT_KEY = "resource"
	}
	
}