package edu.nptu.dllab.sos.io.db

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getStringOrNull
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.Exceptions

class DBOrder {
	
	var id: Int = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.ORDER_AUTO_ID.columnName)
			return field
		}
	var orderId: Int = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.ORDER_ID.columnName)
			return field
		}
	var time: Long = -1L
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.ORDER_TIME.columnName)
			return field
		}
	var status: Int = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.ORDER_STATUS.columnName)
			return field
		}
	var reason: String? = null
		get() {
			return field ?: ""
		}
	
	constructor(oi: Int = -1, t: Long = -1L, s: Int, r: String?) {
		orderId = oi
		time = t
		status = s
		reason = r
	}
	
	constructor(oi: Int = -1, t: Long = -1L, s: String, r: String?) {
		orderId = oi
		time = t
		status = getStatusIdByString(s)
		reason = r
	}
	
	constructor(cursor: Cursor) {
		if(cursor.isAfterLast) return
		run {
			val idI = cursor.getColumnIndex(DBColumn.ORDER_AUTO_ID.columnName)
			if(idI >= 0) id = cursor.getInt(idI)
		}
		run {
			val orderI = cursor.getColumnIndex(DBColumn.ORDER_ID.columnName)
			if(orderI >= 0) orderId = cursor.getInt(orderI)
		}
		run {
			val tI = cursor.getColumnIndex(DBColumn.ORDER_TIME.columnName)
			if(tI >= 0) time = cursor.getLong(tI)
		}
		run {
			val sI = cursor.getColumnIndex(DBColumn.ORDER_STATUS.columnName)
			if(sI >= 0) status = cursor.getInt(sI)
		}
		run {
			val rI = cursor.getColumnIndex(DBColumn.ORDER_REASON.columnName)
			if(rI >= 0) reason = cursor.getStringOrNull(rI)
		}
	}
	
	fun toContentValues(values: ContentValues = ContentValues()): ContentValues {
		values.put(DBColumn.ORDER_ID.columnName, orderId)
		values.put(DBColumn.ORDER_TIME.columnName, time)
		values.put(DBColumn.ORDER_STATUS.columnName, status)
		values.put(DBColumn.ORDER_REASON.columnName, reason)
		return values
	}
	
	override fun equals(other: Any?): Boolean {
		if(other == null) return false
		if(other !is DBOrder) return false
		return other.id == id
	}
	
	override fun hashCode(): Int {
		var result = orderId
		result = 31 * result + time.hashCode()
		result = 31 * result + status
		return result
	}
	
	fun getStatusString(): String {
		return when(status) {
			STATUS_NOT_PROCESS -> Translator.getString("check.status.notProcess")
			STATUS_REQUESTING -> Translator.getString("check.status.requesting")
			STATUS_REQUEST -> Translator.getString("check.status.request")
			STATUS_NOT_REQUEST -> Translator.getString("check.status.notRequest")
			STATUS_PROCESSING -> Translator.getString("check.status.processing")
			STATUS_DONE -> Translator.getString("check.status.done")
			STATUS_NOT_FOUND -> Translator.getString("check.status.notFound")
			else -> Translator.getString("check.status.error")
		}
	}
	
	companion object {
		const val STATUS_NOT_PROCESS = 0
		const val STATUS_REQUESTING = 1
		const val STATUS_REQUEST = 2
		const val STATUS_NOT_REQUEST = 3
		const val STATUS_PROCESSING = 4
		const val STATUS_DONE = 5
		const val STATUS_NOT_FOUND = 6
		
		fun getStatusIdByString(str: String): Int {
			return when(str) {
				"not_process" -> STATUS_NOT_PROCESS
				"requesting" -> STATUS_REQUESTING
				"request" -> STATUS_REQUEST
				"not_request" -> STATUS_NOT_REQUEST
				"processing" -> STATUS_PROCESSING
				"done" -> STATUS_DONE
				"not_found" -> STATUS_NOT_FOUND
				else -> -1
			}
		}
	}
	
}