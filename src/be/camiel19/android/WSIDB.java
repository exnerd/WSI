package be.camiel19.android;


import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import be.camiel19.android.StripinfoPrefs;

public class WSIDB extends SQLiteOpenHelper{
	public final static String DATABASE="stripinfo.db"; // databasename
	public final static String MAIN_TABLE="collection";
	
	// databasevelden
	private final static String ID="_id";
	public final static String REEKS="reeks";
	public final static String TITLE="title";
	private final static String BEZIT="bezit"; 
	private Context myctx;
	public boolean done = false;
	
/**
 * Java-object voor onze data.
 * Uit performance-overwegingingen zien we af van getter/setter 
 */
	public static class CollectionList {
		public long id;
		public String reeks;
		public String title;
		public String bezit;
	}

    private static final String MAIN_DATABASE_CREATE =
        "create table "+MAIN_TABLE
        		+" (_id integer primary key autoincrement, "
        		+ REEKS+" text not null,"
        		+ TITLE+" text not null,"
        		+ BEZIT+" boolean);";

	
    public WSIDB(Context ctx)     {
    	super(ctx,DATABASE, null, 1); // DB versie 1
    	myctx = ctx;
    }

    /**
     * Maakt de database aan
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MAIN_DATABASE_CREATE);
		// evt. extra tabellen aanmaken
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// wordt aangeroepen als de databaseversie lager is 
// de de in de constructor aangegeven versie
// hier kunnen tabellen uitgebreid (ALTER TABLE) en evt. data gewijzigd worden 
	}
	
	// Belangrijk: voor het definitief verlaten van de activity altijd close() aanroepen
	// methode is hier alleen voor documentatiedoeleinden overschreven
	@Override
	public synchronized void close() {
		super.close();
	}
	
/**
 * Geeft lijst met stripreeksen terug
 * @param id sleutelveld in de database
 * @return stripreeksen lijst of null als er niets gevonden wordt
 */
	public CollectionList getCollectionList(long id){
		Cursor c=null;
		try {
			c=getReadableDatabase().query(MAIN_TABLE, new String[]{ID,TITLE,REEKS}, 
					ID+"=?", new String[]{String.valueOf(id)}, null,null,null);
			if(!c.moveToFirst())
				return null; // geen stripreeks bij ID gevonden 
			CollectionList collectionlist=new CollectionList();
			collectionlist.id=id;
			collectionlist.title=c.getString(c.getColumnIndexOrThrow(TITLE));
			collectionlist.reeks=c.getString(c.getColumnIndexOrThrow(REEKS));
			return collectionlist;
		}
		finally {
			if (c != null)
				c.close(); // Cursor moet steeds in een finally-blok gesloten worden
		}
	}
	
/**
 * Sla collectie op. Als id 0, wordt een nieuwe entry aangemaakt, anders
 * wordt de entry ge�pdatet.	
 * @param collectionlist stripreeks die opgeslagen moet worden
 * @return id van de opgeslagen collection
 */
	public long setWordList(CollectionList collectionlist) {
		ContentValues values=new ContentValues();
		if(collectionlist.id!=0)
			values.put(ID, collectionlist.id);
		values.put(TITLE, collectionlist.title);
		values.put(REEKS, collectionlist.reeks);
		if(collectionlist.id==0) {			
			collectionlist.id=getWritableDatabase().insert(MAIN_TABLE,null,values);
		}
		else {
			getWritableDatabase().update(MAIN_TABLE, values, 
					ID+"=?", new String[]{String.valueOf(collectionlist.id)});
		}
		return collectionlist.id;
	}
/**
 * Get list of owned collection or wishlist	
 * @return cursor over de data, de aanroeper moet de cursor zelf sluiten
 */
	public Cursor getCollectionListsCursor(int inbezit) {
		return getReadableDatabase().query(MAIN_TABLE, new String[]{ID,REEKS,TITLE}, 
				"bezit="+inbezit, null, null,null,
				REEKS+","+TITLE+" COLLATE LOCALIZED"); // sorteer naar titel, sorteervolgorde van de actuele taal
	}
	
	public void clearCollection() {
		getWritableDatabase().delete(MAIN_TABLE, null, null);
	}
	

	/**
	 *  Sync Collection from web to local
	 * @throws IOException
	 */
	public void getCollection() throws IOException {
		int i=1;
		StripinfoPrefs prefs = new StripinfoPrefs();
		
	Document doc = Jsoup.connect("http://www.stripinfo.be/member_collection.php?member="+ prefs.getAccount(myctx)+"&page="+i).get();
		Elements titles = doc.select ("tr.title");
		Element table = doc.select ("table.lijst").first();
	
    	clearCollection();
    	
    	while (table.select("td:contains(De gevraagde collectie heeft geen gegevens)").isEmpty()){
    		/*
    		 * doc = Jsoup.connect("http://www.stripinfo.be/member_collection.php?member="+R.string.account+"&page="+i).get();
    		 
    		Elements titles = doc.select ("tr.title");
    		*/
    	
    	for (Element title : titles) {
    		Elements a = title.select("a[href]");
    			Boolean inbezit = table.select("tr.title").get(titles.indexOf(title)).nextElementSibling().getElementsByAttributeValueStarting("name", "bezit").hasAttr("checked");
    			ContentValues values=new ContentValues();
    			values.put(REEKS, a.get(0).text().toString());
    			values.put(TITLE, a.get(1).text().toString());
    			values.put(BEZIT, inbezit);
    			System.out.println(a.get(0).text() + " " + inbezit);
    			Log.i("In bezit",a.get(0).text() + " " + inbezit);
    			getWritableDatabase().insert(MAIN_TABLE,null, values);
    					
    		}
    	i++;
    	//doc = Jsoup.connect("http://www.stripinfo.be/member_collection.php?member="+R.string.account+"&page="+i).get();
    	doc = Jsoup.connect("http://www.stripinfo.be/member_collection.php?member="+prefs.getAccount(myctx)+"&page="+i).get();
		titles = doc.select ("tr.title");
		table = doc.select("table.lijst").first();
    	}
    	this.done = true;
	}
	
}
