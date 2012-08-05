package org.spoofer.channels.displays;

import org.spoofer.channels.R;
import org.spoofer.channels.database.EventDatabase;
import org.spoofer.channels.impl.ChannelServiceSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class AbstractDisplay extends Activity {

	protected static final String LOG_TAG = AbstractDisplay.class.getSimpleName();
	protected EventDatabase database; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "creating new Activity " + LOG_TAG);

		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());

		database = new EventDatabase(getApplicationContext());

		// Set up the table selector dropdown
		Log.v(LOG_TAG, "setting up list of tables drop down");
		Spinner tableList = (Spinner)findViewById(R.id.tablesList);
		tableList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String tableName = ((TextView)view).getText().toString();
				tableSelected(tableName);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				tableSelected(null);
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(LOG_TAG, "creating option menu");

		getMenuInflater().inflate(R.menu.preferences_menu, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(LOG_TAG, "Option menu selected " + item.getItemId() + " " + item.toString());

		boolean result = super.onOptionsItemSelected(item);

		if (!result) {
			switch(item.getItemId()) {
			case R.id.menu_settings : {
				Log.d(LOG_TAG, "starting " + ChannelServiceSettings.class.getSimpleName() + " activity");

				this.startActivity(new Intent(getApplicationContext(), ChannelServiceSettings.class));
				result = true;
			}
			}
		}
		return result;
	}



	@Override
	protected void onResume() {
		super.onResume();
		Log.v(LOG_TAG, "Resuming, refereshing " + LOG_TAG);

		refreshTableList();
	}



	/**
	 * Refreshes the contents of the table selction drop down.
	 * The current table list is read from the database and populated in the list.
	 */
	protected void refreshTableList() {
		Log.d(LOG_TAG, "starting refresh Table List");

		Spinner tableList = (Spinner)findViewById(R.id.tablesList);
		ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(getApplicationContext(), R.layout.table_cell, database.getTableNames());
		dataAdapter.setDropDownViewResource(R.layout.select_table);
		tableList.setAdapter(dataAdapter);
	}


	protected abstract void tableSelected(String tableName);

	protected abstract int getLayoutID();



}