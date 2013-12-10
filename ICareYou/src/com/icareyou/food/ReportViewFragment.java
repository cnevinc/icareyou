/**
public String getmImgurl() {
		return mImgurl;
	}

	public void setmImgurl(String mImgurl) {
		this.mImgurl = mImgurl;
	}

	public boolean ismCname() {
		return mCname;
	}

	public void setmCname(boolean mCname) {
		this.mCname = mCname;
	}

	public long getmDesc() {
		return mDesc;
	}

	public void setmDesc(long mDesc) {
		this.mDesc = mDesc;
	}

	public boolean ismPrice() {
		return mPrice;
	}

	public void setmPrice(boolean mPrice) {
		this.mPrice = mPrice;
	} * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icareyou.food;

import java.util.ArrayList;

import com.icareyou.food.DaoMaster.DevOpenHelper;
import com.openfooddata.app.misc.ReportsArrayAdapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

public class ReportViewFragment extends Fragment implements
		SearchView.OnQueryTextListener {

	protected static final String TAG = "nevin";

	// Private member for UI
	private ListView mlvProduct;
	private ArrayList<Reports> mEntries = new ArrayList<Reports>();
	private ReportsArrayAdapter mAdapter;
	private SearchView mSearchView;

	// Private member for DB operation
	private SQLiteDatabase db;
	private DaoMaster mDaoMaster;
	private DaoSession mDaoSession;
	private ReportsDao mReportsDAO;

	public static String REPORT_ID = "report_id";
	private int mViewing_report = 0;

	OnWatchClickedListener mCallback;

	// Container Activity must implement this interface
	public interface OnWatchClickedListener {
		public void onWatchClicked();
	}

	public void applyFilter() {
		// Clear the filter on old fragment to avoid duplicate popup msg for
		// listview
		// When swipe to new fragment, will set filter text again in
		// onPrepareOptionsMenu
		if (mlvProduct != null) {
			Log.v(TAG, "Fragment[" + mViewing_report + "] applyFilter");
			mlvProduct.clearTextFilter();
		}
	}

	// Clear the filer and text in searchview when configuration changed
	public void clearFilter() {
		if (mlvProduct != null && mSearchView != null) {
			Log.v(TAG, "Fragment[" + mViewing_report + "] clearFilter");
			mlvProduct.clearTextFilter();
			mSearchView.setQuery("", true);
		}
	}

	public void updateUI() {
		if (this.mAdapter != null) {
			mEntries = (ArrayList<Reports>) mReportsDAO.queryBuilder()
					.where(ReportsDao.Properties.Reportid.eq(mViewing_report))
					.orderDesc(ReportsDao.Properties.Watched)
					.orderDesc(ReportsDao.Properties.Companyname).list();
			mDaoSession.clear();
			this.mAdapter.addAll(mEntries);
			this.mAdapter.notifyDataSetChanged();
		}

	}

	public void closeDB() {
		if (this.db != null && db.isOpen())
			db.close();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnWatchClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		setupSearchView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reports_view,
				container, false);
		// Setup DB operation
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this.getActivity(),
				"foodabc-db", null);
		db = helper.getWritableDatabase();
		mDaoMaster = new DaoMaster(db);
		mDaoSession = mDaoMaster.newSession();
		mReportsDAO = mDaoSession.getReportsDao();
		// if (this.getArguments()==null){return null;}
		mViewing_report = this.getArguments().getInt(REPORT_ID);
		mDaoSession.clear();

		mEntries = (ArrayList<Reports>) mReportsDAO.queryBuilder()
				.where(ReportsDao.Properties.Reportid.eq(mViewing_report))
				.orderDesc(ReportsDao.Properties.Watched)
				.orderDesc(ReportsDao.Properties.Companyname).list();

		Log.v(TAG, "Fragment onCreateView mViewing_report:" + mViewing_report);
		mlvProduct = (ListView) rootView.findViewById(R.id.lv_picasa);
		mAdapter = new ReportsArrayAdapter(this.getActivity(), 0, mEntries,
				mCallback);
		mlvProduct.setAdapter(mAdapter);
		mlvProduct.setTextFilterEnabled(true);

		return rootView;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			mlvProduct.clearTextFilter();
		} else {
			mlvProduct.setFilterText(newText.toString());
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	private void setupSearchView() {
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(false);
		// mSearchView.setQueryHint(getString(R.string.product_hunt_hint));
		mSearchView.setQueryHint(getString(R.string.search_hint));
		if (mSearchView.getQuery() != null)
			this.mlvProduct.setFilterText(mSearchView.getQuery().toString());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.v(TAG, "Fragment[" + mViewing_report + "] onConfigurationChanged");
		this.clearFilter();
	}
}
