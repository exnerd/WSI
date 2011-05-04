package be.camiel19.android;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class OwnedActivity extends ListActivity {
	private WSIDB db;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		db = new WSIDB(this);
		Cursor cursor=db.getCollectionListsCursor(1);
		setListAdapter(
	    		new SimpleCursorAdapter(this, 
	    			R.layout.owned, // lay-out voor lijstentry, hier een simpele TextView
	    			cursor, 
	    			new String[]{WSIDB.REEKS,WSIDB.TITLE}, new int[]{R.id.Reeks,R.id.Titel}) // mapping tussen ID in de entry-lay-out en het veld in de database
	    		);
	    registerForContextMenu(getListView()); // laat pop-up-menu zien voor Lijst
	}
}
