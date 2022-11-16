package edu.nptu.dllab.sos.io.db

import android.content.ContentValues
import android.database.Cursor
import edu.nptu.dllab.sos.util.Exceptions

class DBOrder {
	
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
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.ORDER_ID.columnName)
			return field
		}
	
	constructor(oi: Int = -1, t: Long = -1L) {
		orderId = oi
		time = t
	}
	
	constructor(cursor: Cursor) {
		if(cursor.isAfterLast) return
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
	}
	
	fun toContentValues(values: ContentValues = ContentValues()): ContentValues {
		values.put(DBColumn.ORDER_ID.columnName, orderId)
		values.put(DBColumn.ORDER_TIME.columnName, time)
		return values
	}
	
	companion object {
		const val STATUS_NOT_PROCESS = 0
		const val STATUS_REQUESTING = 1
		const val STATUS_REQUEST = 2
		const val STATUS_NOT_REQUEST = 3
		const val STATUS_PROCESSING = 4
		const val STATUS_DONE = 5
	}
	
}