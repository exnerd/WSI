package be.camiel19.android;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Beheer zoveel woordenlijsten als je maar wilt in een ListView: selecteren, aanmaken en verwijderen. 
 */

public class CollectionLists extends ListActivity{

		public static final String ID="id"; // parameter voor het doorgeven van de geselecteerde ID 
		private static final int ADD = Menu.FIRST;
		private static final int DELETE = Menu.FIRST + 1;
		private WSIDB db;

		@Override
		public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		
		    db = new WSIDB(this);
		    Cursor cursor=db.getCollectionListsCursor(1); // cursor over alle entry's, wordt door de ListAdapter beheerd en gesloten
		    setListAdapter(
		    		new SimpleCursorAdapter(this, 
		    			android.R.layout.simple_list_item_1, // lay-out voor lijstentry, hier een simpele TextView
		    			cursor, 
		    			new String[]{WSIDB.TITLE}, new int[]{android.R.id.text1}) // mapping tussen ID in de entry-lay-out en het veld in de database
		    		);
		    registerForContextMenu(getListView()); // laat pop-up-menu zien voor Lijst
		}
		
		@Override
		protected void onResume() {
			((CursorAdapter)getListAdapter()).getCursor().requery(); // lijst verversen    		
			super.onResume();
		}
		
		@Override
		protected void onDestroy() {
			db.close();
			super.onDestroy();
		}

	/**
	 * De gebruiker klikt op een entry in de lijst
	 */
		@Override
		protected void onListItemClick(final ListView l, final View v,
				final int position, final long id) {
			super.onListItemClick(l, v, position, id);
			Intent intent=new Intent();
			intent.putExtra(ID, id);
			setResult(RESULT_OK,intent);
			finish(); // actie succesvol be�indigen
		}

	/**
	 * maak snel(pop-up)-menu, wordt geselecteerd door lang op een lijstentry te tappen. 
	 */
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// DELETE
	        if(v.equals(getListView()))
	    	{
	       	AdapterContextMenuInfo info=(AdapterContextMenuInfo) menuInfo;
	    	if(info.id>=0)
	    		{
	        	menu.setHeaderTitle(db.getCollectionList(info.id).title);
	        	menu.add(0, DELETE, 0, R.string.Delete);
	        	return;
	    		}
	    	}
			super.onCreateContextMenu(menu, v, menuInfo);
		}

	/**
	 * snelmenu voor lijstentry wordt door tap-and-hold geselecteerd
	 */
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			switch(item.getItemId()) {
	    	case DELETE:
	    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    		// hier evt. nog bevestigings-pop-up via AlertBuilder laten zien
	    		
	    		((CursorAdapter)getListAdapter()).getCursor().requery(); // lijst verversen 
	    		break;
			}
			return super.onContextItemSelected(item);
		}
	// normaal menu voor het aanmaken van nieuwe woordenlijsten	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
	    	menu.add(0, ADD, 0, R.string.Add).setIcon(android.R.drawable.ic_menu_add);
			return super.onCreateOptionsMenu(menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()) {
	    	case ADD:
	    		startActivity(new Intent(this,OwnedActivity.class)); // geen ID doorgeven = nieuwe woordenlijst
	    		break;
			}
			return super.onOptionsItemSelected(item);
		}
		
		

}
