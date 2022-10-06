package edu.nptu.dllab.sos.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.util.SOSVersion
import org.intellij.lang.annotations.Language

/**
 * The helper that connect to sqlite
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	
	/**
	 * Call when database create first time
	 */
	override fun onCreate(db: SQLiteDatabase?) {
		@Language("SQL")
		val resCreate =
				"CREATE TABLE IF NOT EXISTS $TABLE_RES (\n" +
				"   ${DBColumn.RES_ID.columnName} INTEGER,\n" +
				"   ${DBColumn.RES_SHOP_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
				"   ${DBColumn.RES_PATH.columnName} CHAR(100) DEFAULT '',\n" +
				"   ${DBColumn.RES_SHA256.columnName} CHAR(64) DEFAULT NULL,\n" +
				"   ${DBColumn.RES_SIZE.columnName} INTEGER DEFAULT -1 NOT NULL\n" +
				");"
		db?.execSQL(resCreate)
		@Language("SQL")
		val menuCreate =
				"CREATE TABLE IF NOT EXISTS $TABLE_MENU (\n" +
				"   ${DBColumn.RES_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
				"   ${DBColumn.RES_SHOP_ID.columnName} INTEGER DEFAULT -1 NOT NULL\n" +
				");"
		db?.execSQL(menuCreate)
	}
	
	/**
	 * Call on database upgrade
	 */
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		// do nothing because just has 1 version
	}
	
	/**
	 * Execute sql command, equals as `writableDatabase.execSQL(sql)`
	 * @param sql - the sql command
	 */
	@SOSVersion(since = "0.0")
	fun execSQL(sql: String) {
		writableDatabase.execSQL(sql)
	}
	
	/**
	 * Query data with sql command, equals as `readableDatabase.rawQuery(sql, null)`
	 * @param sql - the sql command
	 * @return [Cursor] point to first value
	 */
	@SOSVersion(since = "0.0")
	fun query(sql: String): Cursor {
		return readableDatabase.rawQuery(sql, null)
	}
	
	/**
	 * Insert data with table name and [ContentValues], equals as `writableDatabase.insert(table, null, value)`
	 * @param table - the table name
	 * @param value - the value need insert
	 */
	@SOSVersion(since = "0.0")
	fun insert(table: String, value: ContentValues) {
		writableDatabase.insert(table, null, value)
	}
	
	/**
	 * Update data to specific row with table name and [ContentValues], equals as `writableDatabase.update(table, value, "autoId=$autoId", null)`
	 * @param table - the table name
	 * @param value - the value need update
	 * @param autoId - the auto id that point to specific row
	 */
	@SOSVersion(since = "0.0")
	fun update(table: String, value: ContentValues, resId: Int, shopId: Int) {
		writableDatabase.update(table, value, "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId", null)
	}
	
	/**
	 * Select data with filter
	 * @param table - the table name
	 * @param filter - the column name need get
	 * @param where - the where statement(exclude WHERE), `null` if not needed
	 * @param limit - count of data, less than 0 if not needed
	 * @param orderBy - the column order by, `null` if not needed
	 * @param asc - use asc or desc if has orderBy
	 *
	 * @return [Cursor] point to first value
	 */
	@SOSVersion(since = "0.0")
	fun select(table: String, filter: String = "*", where: String? = null, limit: Int = -1, orderBy: String? = "", asc: Boolean = true): Cursor {
		@Language("SQL")
		var sql = "SELECT $filter FROM $table"
		if(where != null) sql += " WHERE $where"
		if(limit > 0) sql += " LIMIT $limit"
		if(orderBy != null) sql += " ORDER BY $orderBy ${if(asc) "ASC" else "DESC"}"
		return query(sql)
	}
	
	companion object {
		const val DATABASE_NAME = "edu.nptu.dllab.sos.RES_DB"
		const val DATABASE_VERSION = 1
		
		const val TABLE_RES = "res"
		const val TABLE_MENU = "menu"
	}
	
}