package org.spoofer.channels.database;

import java.util.AbstractList;
import java.util.List;

import org.spoofer.channels.impl.EventDataImpl;

import android.database.Cursor;

/**
 * The Event List represents a series of event data objects from the under lying database
 * The cursor is read and converts each row into a single `eventData object.
 * 
 * @author robgilham
 *
 */
public class EventList extends AbstractList<EventData> implements List<EventData> {

	private final Cursor cursor;
	private final String channelName;
	
	public EventList(String channel, Cursor cursor) {
		super();
		this.cursor = cursor;
		this.channelName = channel;
	}
	
	@Override
	public EventData get(int location) {
		EventData data = null;
		
		if (cursor.moveToPosition(location)) {
			data = parseData();
		}
		
		return data;
	}

	@Override
	public int size() {
		return cursor.getCount();
	}
	
	
	
	protected EventData parseData() {
		
		EventData data = new EventDataImpl(channelName);
		int columnCount = cursor.getColumnCount();
		
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				data.put(cursor.getColumnName(columnIndex), getValue(columnIndex, cursor));
		}
		
		return data;
	}
	
	public void close() {
		
		cursor.close();
	}
	
	
	/**
	 * this should be done in the DataField class!!
	 * 
	 * @param columnIndex
	 * @param cursor
	 * @return
	 */
	private Object getValue(int columnIndex, Cursor cursor) {
		Object result = null;

		if (null != cursor) {
			result = cursor.getFloat(columnIndex);

			if (null == result)
				result = cursor.getDouble(columnIndex);
			if (null == result)
				result = cursor.getInt(columnIndex);
			if (null == result)
				result = cursor.getLong(columnIndex);
			else
				result = cursor.getString(columnIndex);
		}
		
		return result;
	}


}
