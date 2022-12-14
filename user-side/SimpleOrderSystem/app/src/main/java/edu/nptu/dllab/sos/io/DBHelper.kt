package edu.nptu.dllab.sos.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.nptu.dllab.sos.io.db.DBColumn
import org.intellij.lang.annotations.Language

/**
 * The helper that connect to sqlite
 *
 * @author Little Fish
 */
class DBHelper(context: Context?) :
	SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	
	/**
	 * Call when database create first time
	 */
	override fun onCreate(db: SQLiteDatabase?) {
		@Language("SQL") val resCreate =
			"CREATE TABLE IF NOT EXISTS $TABLE_RES (\n" +
			"   ${DBColumn.RES_ID.columnName} INTEGER,\n" +
			"   ${DBColumn.RES_SHOP_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
			"   ${DBColumn.RES_PATH.columnName} CHAR(100) DEFAULT '',\n" +
			"   ${DBColumn.RES_SHA256.columnName} CHAR(64) DEFAULT NULL,\n" +
			"   ${DBColumn.RES_SIZE.columnName} INTEGER DEFAULT -1 NOT NULL\n" +
			");"
		db?.execSQL(resCreate)
		@Language("SQL") val menuCreate =
			"CREATE TABLE IF NOT EXISTS $TABLE_MENU (\n" +
			"   ${DBColumn.MENU_SHOP_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
			"   ${DBColumn.MENU_NAME.columnName} TEXT DEFAULT '' NOT NULL,\n" +
			"   ${DBColumn.MENU_VERSION.columnName} INTEGER DEFAULT -1 NOT NULL\n" +
			");"
		db?.execSQL(menuCreate)
		@Language("SQL") val orderCreate =
			"CREATE TABLE IF NOT EXISTS $TABLE_ORDER (\n" +
			"   ${DBColumn.ORDER_AUTO_ID.columnName} INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
			"   ${DBColumn.ORDER_ID.columnName} INTEGER DEFAULT -1 NOT NULL,\n" +
			"   ${DBColumn.ORDER_TIME.columnName} INTEGER DEFAULT -1,\n" +
			"   ${DBColumn.ORDER_STATUS.columnName} INTEGER DEFAULT -1,\n" +
			"   ${DBColumn.ORDER_REASON.columnName} TEXT DEFAULT NULL\n" +
			");"
		db?.execSQL(orderCreate)
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
	fun execSQL(sql: String) {
		writableDatabase.execSQL(sql)
	}
	
	/**
	 * Query data with sql command, equals as `readableDatabase.rawQuery(sql, null)`
	 * @param sql - the sql command
	 * @return [Cursor] point to first value
	 */
	fun query(sql: String): Cursor {
		return readableDatabase.rawQuery(sql, null)
	}
	
	/**
	 * Insert data with table name and [ContentValues], equals as `writableDatabase.insert(table, null, value)`
	 * @param table - the table name
	 * @param value - the value need insert
	 */
	fun insert(table: String, value: ContentValues): Boolean {
		return writableDatabase.insert(table, null, value) > 0
	}
	
	/**
	 * Update data to specific row with table name and [ContentValues], equals as `writableDatabase.update(table, value, "autoId=$autoId", null)`
	 * @param table - the table name
	 * @param value - the value need update
	 */
	fun updateRes(table: String, value: ContentValues, resId: Int, shopId: Int) {
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
	fun select(table: String, filter: String = "*", where: String? = null, limit: Int = -1, offset: Int = -1, orderBy: String? = null, asc: Boolean = true): Cursor {
		@Language("SQL") var sql = "SELECT $filter FROM $table"
		if(where != null) sql += " WHERE $where"
		if(orderBy != null) sql += " ORDER BY $orderBy ${if(asc) "ASC" else "DESC"}"
		if(limit > 0) sql += " LIMIT $limit"
		if(offset >= 0) sql += " OFFSET $offset"
		return query(sql)
	}
	
	companion object {
		/**
		 * The name of this database
		 */
		const val DATABASE_NAME = "edu.nptu.dllab.sos.db"
		
		/**
		 * The version of this database
		 */
		const val DATABASE_VERSION = 1
		
		
		/**
		 * The name of resource table
		 */
		const val TABLE_RES = "res"
		
		/**
		 * The name of menu table
		 */
		const val TABLE_MENU = "menu"
		
		/**
		 * The name of order
		 */
		const val TABLE_ORDER = "order_map"
	}
	
}