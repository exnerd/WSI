package be.camiel19.android;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;


public class WishlistActivity extends ListActivity {
   	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        WSIDB db = new WSIDB(this);
			Cursor cursor=db.getCollectionListsCursor(0);
			setContentView(R.layout.wish);	
			setListAdapter(
		    		new SimpleCursorAdapter(this, 
		    			R.layout.row,
		    			cursor, 
		    			new String[]{WSIDB.REEKS,WSIDB.TITLE}, new int[]{R.id.ReeksW,R.id.TitelW}) // mapping tussen ID in de entry-lay-out en het veld in de database
		    		);
		    registerForContextMenu(getListView()); // laat pop-up-menu zien voor Lijst
		    
	    }
	    
}
