package org.spoofer.channels.database;

//import static org.spoofer.cs.database.DatabaseConstants.CreateIndexTable;
import static org.spoofer.channels.database.DatabaseConstants.DATABASE_NAME;
import static org.spoofer.channels.database.DatabaseConstants.DATABASE_VERSION;

import static org.spoofer.channels.database.DatabaseConstants.DropTable;
import static org.spoofer.channels.database.DatabaseConstants.TABLENAMES_QUERY;
import static org.spoofer.channels.database.DatabaseConstants.RECORD_COUNT_QUERY;
import static org.spoofer.channels.database.DatabaseConstants.CREATE_TABLE_QUERY;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database table manager. Base database access layer.  Provides services for sub classes.
 * 
 * @author robgilham
 *
 */
public class DataTables {


	private static final String LOG_TAG = DataTables.class.getSimpleName();

	private static final List<String> SYSTEM_TABLES;
	
	private SQLiteOpenHelper dbHelper;

	static {
		SYSTEM_TABLES = new ArrayList<String>();
		SYSTEM_TABLES.add("sqlite_sequence");
		SYSTEM_TABLES.add("android_metadata");		
	}
	
	
	public DataTables(Context context) {
		
		open(context);
	}





	public void open(Context context) {
		Log.v(LOG_TAG, "Opening Database");

		close();
		dbHelper = new DBHelper(context);
		
	}
	
	public void close() {
		if (null != dbHelper) {
			Log.v(LOG_TAG, "Closing existing Database");

			dbHelper.close();
			dbHelper = null;
		}
	}
	
	
		
	protected SQLiteDatabase getDB(boolean writable) {
		
		return writable ? dbHelper.getWritableDatabase() : dbHelper.getReadableDatabase();
	}




	/**
	 * Clears all the data in the database.
	 */
	public void clearData() {
		Log.d(LOG_TAG, "clearing database.  All tables will be dropped.");

		SQLiteDatabase db = getDB(true);
		List<String> tableNames = getTableNames();

		for (String tableName : tableNames) {
			Log.d(LOG_TAG, "Droppng table " + tableName);
			db.execSQL(DropTable.replace("?", tableName));
		}
		
	}


	/**
	 * Gets a list of all the tables in the database.
	 * 
	 * @return
	 */
	public List<String> getTableNames() {
		
		Log.v(LOG_TAG, "Retrieving table names.");
		
		SQLiteDatabase db = getDB(false);
		
		//Cursor result = db.query(TABLENAMES_TABLE, new String[]{TABLENAMES_FIELD}, TABLENAMES_WHERE, null, null, null, null);
		Cursor tableNamesQuery = db.rawQuery(TABLENAMES_QUERY, null);

		List<String> names = new ArrayList<String>(tableNamesQuery.getCount());

		while (tableNamesQuery.moveToNext()) {
			String name = tableNamesQuery.getString(0);
			if (!SYSTEM_TABLES.contains(name))
				names.add(tableNamesQuery.getString(0));
		}
		
		tableNamesQuery.close();
		
		if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
			StringBuilder tableNames = new StringBuilder();
			for (String name : names) {
				if (tableNames.length() > 0)
					tableNames.append(", ");
				tableNames.append(name);
			}
			Log.v(LOG_TAG, "Retrieved " + names.size() + " table names.\n" + tableNames.toString());
		}
		
		return names;
	}
	
	/**
	 * Check to see if the database has any data in it.
	 * 
	 * @return true is at least one table exists with one record.
	 */
	public boolean hasData() {
		Log.v(LOG_TAG, "Checking for data...");
		
		SQLiteDatabase db = getDB(false);
		
		//Cursor result = db.query(TABLENAMES_TABLE, new String[]{TABLENAMES_FIELD}, TABLENAMES_WHERE, null, null, null, null);
		Cursor tableNamesQuery = db.rawQuery(TABLENAMES_QUERY, null);

		int recordCount = -1;

		while (tableNamesQuery.moveToNext()) {
			String name = tableNamesQuery.getString(0);
			if (!SYSTEM_TABLES.contains(name)) {
				Cursor recordCountQuery = db.rawQuery(DatabaseConstants.getParamateredQuery(RECORD_COUNT_QUERY, name), null);
				recordCount = recordCountQuery.moveToFirst() ? recordCountQuery.getInt(0) : 0;
				recordCountQuery.close();

				if (recordCount > 0)
					break;
			}
		}
		
		tableNamesQuery.close();
		Log.v(LOG_TAG, "Datacheck found at least " + recordCount + " records");
		return recordCount > 0;
	}
	
	/**
	 * Adds a new table to the database, using the given field information
	 * @param tableName	The name of the table
	 * @param fieldInfo The field data, names and types
	 */
	public void addTable(String tableName, List<DataField> fieldInfo) {
		
		Log.v(LOG_TAG, "Adding new table " + tableName);
		
		StringBuilder sql = new StringBuilder(DatabaseConstants.getParamateredQuery(CREATE_TABLE_QUERY, tableName));

		for (DataField field: fieldInfo) {

			sql.append(", '").append(field.name).append('\'').append(' ').append(field.type);
		}
		sql.append(");");

		Log.d(LOG_TAG, "new table SQL '" + sql.toString() + "'");

		SQLiteDatabase db = getDB(true);
		db.execSQL(sql.toString());
		
	}

	public void removeTable(String tableName) {
		Log.v(LOG_TAG, "Removing table " + tableName);
		
		StringBuilder sql = new StringBuilder(DatabaseConstants.DropTable.replaceFirst("\\?", tableName));

		Log.d(LOG_TAG, "drop table SQL '" + sql.toString() + "'");

		SQLiteDatabase db = getDB(true);
		db.execSQL(sql.toString());
		
	}
	
	
	public Cursor getRawQuery(String query) {
		if (!query.toLowerCase().startsWith("select"))
			throw new IllegalArgumentException("query " + query + " must start with SELECT");
		
		SQLiteDatabase db = getDB(false);
		return db.rawQuery(query, null);

	}
	
	public String getDatabaseName() {
		return DatabaseConstants.DATABASE_NAME;
	}

	private class DBHelper extends SQLiteOpenHelper {


		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v(LOG_TAG, "Creating new database. (does nothing)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");

			clearData();
			onCreate(db);
		}
	}
	
	
	
}
