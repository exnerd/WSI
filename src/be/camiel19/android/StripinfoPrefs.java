package be.camiel19.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StripinfoPrefs {
	private static String account;

	public String getAccount(Context ctx) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		System.out.println(prefs.getString("username","not found"));
		return prefs.getString("username", "");	// default is true, d.w.z. geen geluid
	}
	
	public void setAccount(Context ctx,String s){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("username", s);
		editor.commit();
		prefs.edit().putString("username", account);
	}
}
