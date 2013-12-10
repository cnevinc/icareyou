package com.icareyou.food;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.icareyou.food.DaoMaster.DevOpenHelper;
import com.openfooddata.app.misc.MyVolley;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

// THis activity is the entrance point to the APP
// It quickly load Core and see its setup,
//   start the correspond Activity as a decision maker 
//		then pass the core to the next Activity
public class SplashActivity extends Activity {
 
	String TAG = "nevin";

	// Private member for DB operation
	private SQLiteDatabase db;
	private DaoMaster mDaoMaster;
	private DaoSession mDaoSession;
	private ReportsDao mReportsDao;

	// Private member
	final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"); // example: 2013-11-08T17:05:08.654Z

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		// Setup DB operation
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(SplashActivity.this,
				"foodabc-db", null);
		db = helper.getWritableDatabase();
		mDaoMaster = new DaoMaster(db);
		mDaoSession = mDaoMaster.newSession();
		mReportsDao = mDaoSession.getReportsDao();
		
		// empty self first
		Log.d(TAG,"updatTable!!!aa" );
//		mReportsDao.deleteAll();
		// Download Data from server
		
		new Task().execute(1);
	}
	class Task extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			if (mReportsDao.count()==0)
				updatTable();
			return null;
		}
		
		protected void onPostExecute(Void s) {
			closeSelf();
		}
	}
	private void updatTable() {
		Log.d(TAG,"updatTable!!!" );
		String json = null;
        try {
            InputStream is = getAssets().open("report.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
             
            json = new String(buffer, "UTF-8");
            JSONArray entries = new JSONArray(json);
            for (int i = 0; i < entries.length(); i++) {
             
				JSONObject entry = entries.getJSONObject(i);
				Reports obj = new Reports();
//				obj.setBatchno(entry.getString("batchno"));
//				obj.setCompanycountry(entry.getString("companycountry"));
				obj.setCompanyname(entry.getString("companyname"));
				obj.setCreated_at(sdf.parse(entry.getString("createdat")));
//				obj.setGuarantee(entry.getString("guarantee"));
//				obj.setImportexport(entry.getString("importexport"));
				obj.setProductname(entry.getString("productname"));
				obj.setReportcat(entry.getString("reportcat"));
				obj.setReportid(entry.getString("reportid"));
				obj.setResult(entry.getString("result"));
				obj.setUdpated_at(sdf.parse(entry.getString("udpatedat")));
				obj.setWatched(false);
				mReportsDao.insertOrReplace(obj);
//				Log.d(TAG, "Inserted new Reports : " + obj.getReportid() + "/: " +obj.getResult() );
            }
        } catch (JSONException e) {
			Log.v(TAG,e.toString());
		} catch (NumberFormatException e) {
			Log.v(TAG,e.toString());
		} catch (ParseException e) {
			Log.v(TAG,e.toString());
		} catch (IOException e) {
			Log.v(TAG,e.toString());
        } 
	}

	private void showErrorDialog(Exception e) {
		AlertDialog.Builder b = new AlertDialog.Builder(SplashActivity.this);
		b.setMessage("Error! " + e.toString());
		b.show();
	}
	
	private void closeSelf() {
		// Start Product View
		Intent intent = new Intent(SplashActivity.this,	MainActivity.class);
		startActivity(intent);
		SplashActivity.this.finish();
		
	}


}