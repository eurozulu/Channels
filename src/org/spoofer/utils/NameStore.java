package org.spoofer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.spoofer.channels.ChannelManager;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * The Name store keeps lists of string names in the shared preferences store.
 * Every name is unique in that only one insance of an equal name can be stored in teh store.
 * If duplicates are presented, they are discarded..
 * 
 * @author robgilham
 *
 */
public class NameStore extends ArrayList<String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static final String LOG_TAG = ChannelManager.class.getSimpleName();


	private static final String PREF_NAME = LOG_TAG + "_";

	private final SharedPreferences sharedPreferences;

	
	
	public NameStore(Context context) {
		super();

		sharedPreferences = context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
		refresh();
	}

	
	@Override
	public void clear() {
		
		sharedPreferences.edit().clear().commit();
		
		super.clear();
	}


	public NameStore(Context context, int capacity) {
		super(capacity);

		sharedPreferences = context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
		refresh();
	}

	
	
	/**
	 * Gets a List of all the name in the store.
	 * If the store is empty, an empty List is returned (Well, what did you expect?!!!)
	 * 
	 * @return A List containing all the names in the store.
	 */


	@Override
	public boolean add(String name) {

		String key = PREF_NAME + name;
		boolean isNew = false;

		if (!sharedPreferences.contains(key)) {
			Editor editor = sharedPreferences.edit();
			editor.putString(key, name);
			editor.commit();

			isNew = super.add(name);
		}

		return isNew; 
	}

	@Override
	public boolean addAll(Collection<? extends String> collection) {
		Editor editor = sharedPreferences.edit();
		Collection<String> added = new HashSet<String>();

		for (String name : collection) {
			String key = PREF_NAME + name;

			if (!sharedPreferences.contains(key)) {
				editor.putString(key, name);
				added.add(name);
			}
		}

		editor.commit();

		return super.addAll(added);
	}



	@Override
	public String remove(int index) {
		String name = this.get(index);
		return remove(name) ? name : null;
	}

	@Override
	public boolean remove(Object name) {
		String key = PREF_NAME + name.toString();

		if (sharedPreferences.contains(key)) {
			Editor editor = sharedPreferences.edit();
			editor.remove(key);
			editor.commit();

		}

		return super.remove(name);
	}









	public void refresh() {
		
		Map<String, ?> values = sharedPreferences.getAll();
		Collection<String> names = new HashSet<String>();
		
		for (Map.Entry<String, ?> entry : values.entrySet()) {
			if (entry.getKey().startsWith(PREF_NAME)) {
				names.add(entry.getValue().toString());
			}
		}

		super.clear();
		super.addAll(names);
	}


}
