package org.spoofer.channels.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.spoofer.channels.Channel;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ExportAgent {


	private static final String LOCATION_DEFAULT = Channel.class.getSimpleName().toLowerCase() + "_export";

	private static final long EXPORT_MIN_SIZE = 1024 * 1024 * 2000;

	private static final String LOG_TAG = ExportAgent.class.getSimpleName();

	private final Context context;

	public ExportAgent(Context context) {
		super();
		this.context = context;
	}

	/**
	 * Exports the given database to the Agents target location.
	 * Any existing data in the database is erased once the export is complete.
	 * 
	 * @param database  The database to export.
	 * @throws IOException
	 */
	public void exportDatabase(DataTables database) throws IOException {
		exportDatabase(database, false);
	}

	/**
	 * Exports the given database to the Agents target location.
	 * Any existing data in the database can be erased or not by using the retain flag.
	 * 
	 * @param database  The database to export.
	 * @param retainData flag to indicate if the database data should be erased after the export is complete.
	 * When false, the database is erased, when true it is left unchanged.
	 * @throws IOException
	 */
	public void exportDatabase(DataTables database, boolean retainData) throws IOException {
		String dbName = database.getDatabaseName();
		Log.v(LOG_TAG, "exporting " + dbName);
		
		if (!database.hasData()) {
			String err = "aborting export of " + dbName + " as database is empty";
			Log.d(LOG_TAG, err);
			Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
			return;
		}
			
		File dbFile = context.getDatabasePath(dbName);
		File trgFile = getTargetLocation(dbName);
		
		if (null != trgFile) {
			synchronized(database) {
				database.close();
				copyFile(dbFile, trgFile);

				if (!retainData)
					dbFile.delete();

				database.open(context);
			}
		}

	}



	private AutoExporter autoExporter = null;

	public synchronized void startExport(DataTables database) {

		Log.v(LOG_TAG, "starting export requested");

		if (null == autoExporter) {
			Log.d(LOG_TAG, "starting new Auto exporter");
			autoExporter = new AutoExporter(database);
			new Thread(autoExporter).start();
		}		
	}

	public synchronized void stopExport(DataTables database) {
		if (null != autoExporter) {
			autoExporter.isRunning = false;
			autoExporter.notifyAll();
		}
	}


	/**
	 * Gets a target location to export data to.
	 * The result is located in on the External storeage folder, (Usually the sdcard)
	 * under the directory channel_export.
	 * The File name
	 * @return
	 */
	public File getTargetLocation(String databaseName) {

		// Ensure the target directory exists
		File path = new File(Environment.getExternalStorageDirectory(), LOCATION_DEFAULT);
		if (!path.exists() && !path.mkdirs())
			Log.e(LOG_TAG, "Failed to create export file path " + path.getAbsolutePath());


		File targetFile = new File(path, databaseName);
		int index = 0;
		while (targetFile.exists()) // Loop to find a Filename that doesn't already exist
			targetFile = new File(path, getIndexedFileName(databaseName, index++));

		try {
			targetFile.createNewFile();

		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to create export file " + targetFile.getAbsolutePath(), e);
		}

		return targetFile;
	}



	private String getIndexedFileName(String fileName, int index) {
		int pointPos = fileName.lastIndexOf('.');
		String szIndex = Integer.toString(index);

		if (pointPos < 0)  // no point in the name, simply tack on index to the end
			fileName += szIndex;
		else
			fileName = new StringBuilder(fileName.substring(0, pointPos)).append('_').append(szIndex).append(fileName.substring(pointPos)).toString();

		return fileName;

	}
	/**
	 * Copy the given source file to the given target file.
	 * Any existing file named in the target will be overwritten.
	 * 
	 * @param src The source file to copy
	 * @param trg The target file to copy to
	 * @throws IOException it's gone tits up!
	 */
	private void copyFile(File src, File trg) throws IOException {
		FileChannel fileSrc = null;
		FileChannel fileTrg = null;

		try {
			fileSrc = new FileInputStream(src).getChannel();
			fileTrg = new FileOutputStream(trg).getChannel();

			fileSrc.transferTo(0, fileSrc.size(), fileTrg);

		}finally{
			if (null != fileSrc)
				fileSrc.close();

			if (null != fileTrg)
				fileTrg.close();
		}

	}



	class AutoExporter implements Runnable {

		private static final long EXPORT_POLL_TIME = 1000 * 60;  // One Minute
		private final DataTables database;

		public boolean isRunning = false;

		public AutoExporter(DataTables database) {
			this.database = database;
		}


		public void run() {
			isRunning = true;

			String dbName = database.getDatabaseName();
			File dbFile = context.getDatabasePath(dbName);

			while (isRunning) {

				if (dbFile.length() > EXPORT_MIN_SIZE) {
					try {
						exportDatabase(database);

					} catch (IOException e) {
						e.printStackTrace();
						String err = "Export of database " + dbName + " failed\n" + e.getMessage();
						Toast.makeText(context, err, Toast.LENGTH_LONG).show();
						Log.e(LOG_TAG, err, e);
					}
				}

				if (isRunning)
					synchronized (this) {
						try {
							this.wait(EXPORT_POLL_TIME);
						} catch (InterruptedException e) {
							isRunning = false;
							break;
						}
					}
			}

			isRunning = false;  // No real need, but no harm. 
		}

	}
}

