package org.spoofer.channels.database;

import java.util.List;
import java.util.Map;

import org.spoofer.utils.Strings;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static org.spoofer.channels.database.DatabaseConstants.SELECT_ENTIRE_TABLE_QUERY;

/**
 * Channels Data Access Object.
 * 
 * @author robgilham
 *
 */
public class EventDatabase extends DataTables {


	private static final String LOG_TAG = EventDatabase.class.getSimpleName();

	public EventDatabase(Context context) {
		super(context);
	}


	
	public void addEvents(List<EventData> data) {
		Log.v(LOG_TAG, "adding event data ");

		
		if (null != data && data.size() > 0) {

			Log.d(LOG_TAG, "adding " + data.size() + " events to database");

			SQLiteDatabase db = getDB(true);
			List<String> tableNames = getTableNames();

			UnTypedContentValues values;

			for (EventData event : data) {

				String tableName = event.getChannel();
				Log.d(LOG_TAG, "inserting " + event.size() + " values for channel " + tableName);

				if (!tableNames.contains(tableName)) {
					addTable(tableName, event.getFieldInfo());
					tableNames.add(tableName);
				}
				
				values = new UnTypedContentValues();

				for (Map.Entry<String, Object> field : event.entrySet())
					values.put(field.getKey(), field.getValue());

				db.insert(tableName, null, values.getContentValues());
			}
			
			db.close();

		}else
			Log.e(LOG_TAG, "Ignoring addEvents as data is null or empty");
	}

	public List<EventData> getEventsWhere(String channelName, String whereClause) {
		
		if (!Strings.hasText(channelName))
			return null;
		
		
		StringBuilder sql = new StringBuilder(DatabaseConstants.getParamateredQuery(SELECT_ENTIRE_TABLE_QUERY, channelName));

		if (Strings.hasText(whereClause))
			sql.append(" WHERE ").append(whereClause);
		
		SQLiteDatabase db = getDB(false);
		Cursor resultCursor = db.rawQuery(sql.toString(), null);

		return new EventList(channelName, resultCursor);
		
	}
	
	public List<EventData> getEvents(String channelName) {
		return getEventsWhere(channelName, null);
	}
	public List<EventData> getEventsAfter(String channelName, long afterTimeStamp) {
		return getEventsWhere(channelName, DatabaseConstants.FIELD_TIMESTAMP + " > " + Long.toString(afterTimeStamp));
	}
	public List<EventData> getEventsBefore(String channelName, long beforeTimeStamp) {
		return getEventsWhere(channelName, DatabaseConstants.FIELD_TIMESTAMP + " < " + Long.toString(beforeTimeStamp));
	}
	
}
