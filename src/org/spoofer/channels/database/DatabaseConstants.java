package org.spoofer.channels.database;

import org.spoofer.utils.Strings;


/**
 * simple container for constant SQL queries.
 * 
 * @author robgilham
 *
 */
public class DatabaseConstants {

	static final String DATABASE_NAME = "channels.db";
	static final int DATABASE_VERSION = 1;

	static final String COL_ID = "_id";	
	public static final String FIELD_TIMESTAMP = "timestamp";

	public static final String TABLE_META_DATA = "channels";
	
	/*
	static final String CreateIndexTable = "create table " + TABLE_NAME + " (" + 
			 COL_ID + " integer primary key autoincrement, " +
			 COL_SENSOR_TYPE + " INT not null, " +
			 COL_SENSOR_NAME + " text" +
			 ");";
	*/
	
	static final String PRIMARY_KEY_TYPE = "integer primary key autoincrement";
			
	public static final String DropTable = "DROP TABLE IF EXISTS ?";
	
	public static final String TABLENAMES_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";
	
	public static final String RECORD_COUNT_QUERY = "SELECT COUNT(*) FROM ?";

	
	public static final String CREATE_TABLE_QUERY = "create table if not exists ? (" + COL_ID + " " + PRIMARY_KEY_TYPE;
	
	public static final String SELECT_ENTIRE_TABLE_QUERY = "select * FROM ? order by " + COL_ID;
			
	public static String getParamateredQuery(String QUERY, String[] params) {
		return Strings.insertParameters(QUERY, params);
	}
	public static String getParamateredQuery(String QUERY, String param) {
		return getParamateredQuery(QUERY, new String[]{param});
	}
}
