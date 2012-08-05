package org.spoofer.channels.displays;

import org.spoofer.channels.R;
import org.spoofer.channels.database.DatabaseConstants;
import org.spoofer.channels.displays.table.TableView;
import org.spoofer.utils.Strings;
import android.database.Cursor;
import static org.spoofer.channels.database.DatabaseConstants.SELECT_ENTIRE_TABLE_QUERY;



public class TableDisplay extends AbstractDisplay {

	protected static final String LOG_TAG = TableDisplay.class.getSimpleName();

	
	@Override
	protected int getLayoutID() {
		return R.layout.display_table;
	}


	@Override
	protected void tableSelected(String tableName) {
		Cursor tableData;
		if (Strings.hasText(tableName)) {
			StringBuilder sql = new StringBuilder(DatabaseConstants.getParamateredQuery(SELECT_ENTIRE_TABLE_QUERY, tableName));
			tableData = database.getRawQuery(sql.toString());
		}else
			tableData = null;
		
		TableView table = (TableView)findViewById(R.id.table);
		table.setData(tableData);
	}





}