package org.spoofer.channels.displays.table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.spoofer.channels.R;
import org.spoofer.channels.database.DatabaseConstants;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableView extends HorizontalScrollView {

	private static final String LOG_TAG = TableView.class.getSimpleName();


	private static final int MAX_DISPLAY_ROW_COUNT = 25;
	private static final int PAGE_DISPLAY_ROW_COUNT = 6;

	private static Map<String, CellFormatter> formatters = buildCellFormatters();
	
	private Cursor data;


	public TableView(Context context) {
		super(context);
	}
	public TableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}



	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		// Inflate the table hieracrhy and confirm the required Views are present.

		Context context = getContext();

		if (getChildCount() < 1)  // If no, inline, children set up, inflate our own.
			View.inflate(context, R.layout.table_view, this);

		View v = findViewById(R.id.table_table);
		if (null == v || !(v instanceof TableLayout))
			throw new IllegalStateException("No suitable 'table' view found in " + LOG_TAG);

		final TableLayout table = (TableLayout)v;  // fill out the table to its max rows
		while (table.getChildCount() < MAX_DISPLAY_ROW_COUNT)
			View.inflate(context, R.layout.table_row, table);

		v = findViewById(R.id.table_header);
		if (null == v || !(v instanceof ViewGroup))
			throw new IllegalStateException("No suitable 'table_header' view found in " + LOG_TAG);

		final ObservableScrollView tableScroller;
		v = findViewById(R.id.table_scroll);
		if (null == v || !(v instanceof ScrollView))
			throw new IllegalStateException("No suitable 'table_scroll' view found in " + LOG_TAG);
		else
			tableScroller = (ObservableScrollView)v;

		tableScroller.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {

				//Log.i(VIEW_LOG_TAG, "Scrolling from " + oldx + ", " + oldy + " to " + x + ", " + y );
			}

			@Override
			public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
				if (clampedY) {
					Log.i(LOG_TAG, "on over scroll: scrollX=" + scrollX + " scrollY=" + scrollY + " clampedX=" + clampedX + " clampedY=" + clampedY);
					scrollData(scrollY > 0);

					Log.i(LOG_TAG, "on over scroll: MaxScrollAmount=" + getMaxScrollAmount());
					
					int rowHeight = table.getChildAt(0).getHeight() * PAGE_DISPLAY_ROW_COUNT;
					if (scrollY == 0)
						rowHeight = -rowHeight;
					
					tableScroller.scrollBy(0, rowHeight);
				}
			}
		});

	}


	public Cursor getData() {
		return this.data;
	}
	public void setData(Cursor data) {
		if (null != this.data)
			this.data.close();
		
		this.data = data;

		if (null != data && !data.moveToFirst()) {
			Log.i(LOG_TAG, "Tableview ignoring Cursor as cursor is empty");
			data = null;
		}

		refreshTable();
	}


	public boolean hasData() {
		return null != data && !data.isClosed() && data.getCount() > 0;
	}



	private void scrollData(boolean scrollDown) {

		int originalStart = data.getPosition() - MAX_DISPLAY_ROW_COUNT;
		int newPosition = originalStart + (scrollDown ? PAGE_DISPLAY_ROW_COUNT : -PAGE_DISPLAY_ROW_COUNT);

		// Check new position is within bounds.
		if (newPosition > data.getCount())
			newPosition = data.getCount() - MAX_DISPLAY_ROW_COUNT;

		if (newPosition < 0)
			newPosition = 0;


		if (newPosition != originalStart) {
			Log.v(LOG_TAG, "scrolling table " + (scrollDown ? "down" : "up") + " from position " + originalStart + " to " + newPosition);
			data.moveToPosition(newPosition);
			refreshTable();
		}else
			Log.v(LOG_TAG, "scrolling table ignored as no change to position");

	}	


	private void clearTable() {
		Log.v(LOG_TAG, "clearing table view");

		ViewGroup headerRow = (ViewGroup)findViewById(R.id.table_header);
		headerRow.removeAllViews();
		View.inflate(getContext(), R.layout.table_cell, headerRow);  // Add a single, empty cell.

		hideRows(0);

	}


	private void refreshTable() {
		Log.v(LOG_TAG, "starting refresh Table view");

		TableLayout table = (TableLayout)findViewById(R.id.table_table);

		if (!hasData()) {
			Log.d(LOG_TAG, "Table data is empty, clearing table");
			clearTable();

		}else {
			int dataPosition = data.getPosition();
			Log.v(LOG_TAG, "refreshing table from position " + dataPosition + " with " + data.getCount() + " available records");

			ViewGroup headerRow = null;
			if (0 == dataPosition) {  // If first pass, fresh the header row
				Log.v(LOG_TAG, "refreshing table headers");
				headerRow = (ViewGroup)findViewById(R.id.table_header);
				bindToRow(headerRow, false);
			}
			
			ViewGroup row = null;;
			
			for (int index = 0; index < MAX_DISPLAY_ROW_COUNT; index++) {

				row = (ViewGroup)table.getChildAt(index);
				bindToRow(row, true);
				row.setVisibility(VISIBLE);

				if (!data.moveToNext()) {  // if no more data, hide remaining rows and bust loop
					hideRows(index);
					break;
				}
			}
			
		}

		Log.v(LOG_TAG, "refreshing table completed.  Table now has " + table.getChildCount() + " rows");
	}



	
	private void bindToRow(ViewGroup row, boolean useValues) {

		// Size the row to contain enough cells.
		Context context = getContext();
		int columnCount = data.getColumnCount();

		if (row.getChildCount() > columnCount)  // Trim off any excess Cells left in the row
			row.removeViews(columnCount, row.getChildCount() - columnCount);

		else  // Otherwise, inflate new cells to meet the number of data fields.
			while (columnCount > row.getChildCount())
				View.inflate(context, R.layout.table_cell, row);


		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // iterate each cell in the current row
			TextView cell = (TextView)row.getChildAt(columnIndex);
			
			String colName = data.getColumnName(columnIndex);
			String value;
			if (useValues) {
				value = data.getString(columnIndex);
				if (formatters.containsKey(colName))
					value = formatters.get(colName).formatCell(value);
			}else
				value = colName;
			
			cell.setText(value);
		}

	}




	/**
	 * Hides any surpluss rows so only othe given number of rows are displayed
	 * 
	 * @param rowCount the number of rows that should be remaining after any others have been hidden
	 */
	private void hideRows(int rowCount) {
		TableLayout table = (TableLayout)findViewById(R.id.table_table);
		for (int index = table.getChildCount() - 1; index >= 0 && index >= rowCount; index --)
			table.getChildAt(index).setVisibility(GONE);
	}

	/*
	private void showRows(int rowCount) {
		TableLayout table = (TableLayout)findViewById(R.id.table);
		for (int index = table.getChildCount() - 1; index >= 0 && index >= rowCount; index --)
			table.getChildAt(index).setVisibility(VISIBLE);
	}*/

	
	private static Map<String, CellFormatter> buildCellFormatters() {

		Map<String, CellFormatter> formatters = new HashMap<String, CellFormatter>();
		
		formatters.put(DatabaseConstants.FIELD_TIMESTAMP, new CellFormatter() {
			private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SS";
			DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			
			@Override
			public String formatCell(String value) {
				return formatter.format(new Date(Long.parseLong(value)));
			}
		});
		
		return formatters;
	}
	
	
	private interface CellFormatter {
		public String formatCell(String value);
	}
}
