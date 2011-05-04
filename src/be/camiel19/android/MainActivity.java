package be.camiel19.android;


import java.io.IOException;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends TabActivity
{
	private static final int SYNC = Menu.FIRST;
	private static final int PREFS = Menu.FIRST+1;
	private WSIDB db;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new WSIDB(this);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, WishlistActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("wishlist").setIndicator("Wishlist",
                          res.getDrawable(R.drawable.icon))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, OwnedActivity.class);
        spec = tabHost.newTabSpec("owned").setIndicator("Owned",
                          res.getDrawable(R.drawable.icon))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        // Do the same for the other tabs
        intent = new Intent().setClass(this, AllActivity.class);
        spec = tabHost.newTabSpec("All").setIndicator("All",
                          res.getDrawable(R.drawable.icon))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
    protected void onResume(){
    	super.onResume();
    }
    protected void onDestroy() {
    	db.close();
    	super.onDestroy();
    }
  
    /**
     * Menu's aanmaken (die via de menu-knop opgeroepen worden)	
     */
    	@Override
    	public boolean onCreateOptionsMenu(android.view.Menu menu) {
    		menu.add(Menu.NONE, SYNC, Menu.NONE, R.string.Sync);
    		menu.add(Menu.NONE, PREFS, Menu.NONE, R.string.Prefs).setIcon(android.R.drawable.ic_menu_preferences);
    		return super.onCreateOptionsMenu(menu);
    	};

    /**
     * Selecteer een menu-entry	
     */
    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    		Runnable viewCollection,returnRes;
    		final ProgressDialog spinner;
    		final Handler mHandler = new Handler();
    		
    		switch (item.getItemId()) {
    		case SYNC: {
    			final Runnable mErrorShow = new Runnable () {
    				public void run () {
    					Toast.makeText(MainActivity.this.getApplicationContext(),"Een fout tijdens connectie",Toast.LENGTH_SHORT).show();
    				}
    			};
    			spinner = ProgressDialog.show(MainActivity.this,"In progress","Retrieving collection ...",true);	
    			viewCollection = new Runnable(){
    					@Override
    					public void run(){
    						try {
    							db.getCollection();
    		    			}
    		    			catch (IOException e){
    		    				mHandler.post(mErrorShow);
    		    				//
    		    				spinner.dismiss();
    		    				System.out.println(e);
    		    			}
    		    		
    					}
    				};
    				
    				Thread getcollectionthread = new Thread (null,viewCollection,"MagentoBackground");
    				getcollectionthread.start();
        			//spinner.dismiss();
    				returnRes = new Runnable() {

    			        @Override
    			        public void run() {
    			        	while (! db.done){
    			            if(db.done){
    			            		spinner.dismiss();
    			            		return;
    			            }
    			        }
    			            //m_adapter.notifyDataSetChanged();
    			        }
    			    };
			        Thread controldonethread = new Thread (null,returnRes,"controldone");
			        controldonethread.start();

    			}
    			//
    			break;
    		case PREFS: 
    			startActivity(new Intent(this,PrefsActivity.class));
    			break;
    		}
    		return super.onOptionsItemSelected(item);
    	}
}
