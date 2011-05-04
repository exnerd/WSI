package be.camiel19.android;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class AllActivity extends ListActivity{
	public static final String ID="id"; // parameter to pass on ID 
	private WSIDB db;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		db = new WSIDB(this);
		Cursor cursor=db.getCollectionListsCursor(1);
		setListAdapter(
	    		new SimpleCursorAdapter(this, 
	    			android.R.layout.two_line_list_item, // lay-out voor lijstentry, hier een simpele TextView
	    			cursor, 
	    			new String[]{WSIDB.REEKS}, new int[]{android.R.id.text1}) // mapping tussen ID in de entry-lay-out en het veld in de database
	    		);
	    registerForContextMenu(getListView()); // laat pop-up-menu zien voor Lijst
	}

}
