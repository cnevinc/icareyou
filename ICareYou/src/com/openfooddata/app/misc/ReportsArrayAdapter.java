/**
 * Copyright 2013 Ognyan Bankov
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

package com.openfooddata.app.misc;

import java.util.ArrayList;
import java.util.List;

import com.icareyou.food.DaoMaster;
import com.icareyou.food.DaoMaster.DevOpenHelper;
import com.icareyou.food.DaoSession;
import com.icareyou.food.R;
import com.icareyou.food.ReportViewFragment.OnWatchClickedListener;
import com.icareyou.food.Reports;
import com.icareyou.food.ReportsDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ReportsArrayAdapter extends ArrayAdapter<Reports> {

	private static final String TAG = "nevin";

	// A copy of the original mObjects array, initialized from and then used
	// instead as soon as
	// the mFilter ArrayFilter is used. mObjects will then only contain the
	// filtered values.
	private ArrayList<Reports> mOriginalValues;
	private ArrayFilter mFilter;
	private List<Reports> mObjects;
	/**
	 * Lock used to modify the content of {@link #mObjects}. Any write operation
	 * performed on the array should be synchronized on this lock. This lock is
	 * also used by the filter (see {@link #getFilter()} to make a synchronized
	 * copy of the original array of data.
	 */
	private final Object mLock = new Object();

	// Private member for DB operation
	private SQLiteDatabase db;
	private DaoMaster mDaoMaster;
	private DaoSession mDaoSession;
	private ReportsDao mReportsDao;
	
	// Register the call back to activity when an watch star is clicked
	OnWatchClickedListener mCallback;
	
	public ReportsArrayAdapter(Context context, int textViewResourceId,
			List<Reports> objects, OnWatchClickedListener callback) {
		super(context, textViewResourceId, objects);
		this.mObjects = objects;
		this.mCallback = callback;
	}

	private class ViewHolder {
		TextView result;
		TextView product_name;
		TextView company_name;
		ImageView watchListButton;

		public ViewHolder(View v) {
			result = (TextView) v.findViewById(R.id.tv_result);
			product_name = (TextView) v.findViewById(R.id.tv_product_name);
			company_name = (TextView) v.findViewById(R.id.tv_company);
			watchListButton = (ImageView) v.findViewById(R.id.wathclist_ib);
			v.setTag(this);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.lv_reports_row, null);
		}

		ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

		if (holder == null) {
			holder = new ViewHolder(v);
			v.setTag(R.id.id_holder, holder);
		}
		final Reports entry = getItem(position);
		holder.result.setText(entry.getResult());
		holder.product_name.setText(entry.getProductname());
		// TODO : Get actual company name
		holder.company_name.setText(entry.getCompanyname());
		holder.watchListButton
				.setImageResource(android.R.drawable.star_big_off);
		if (entry.getWatched() != null && entry.getWatched())
			holder.watchListButton
					.setImageResource(android.R.drawable.star_big_on);
		holder.watchListButton
				.setOnClickListener(new WatchButtonnOnClickListener(position));
		
		return v;
	}

	class WatchButtonnOnClickListener implements OnClickListener {

		int mCurrentPosition;

		public WatchButtonnOnClickListener(int currentPosition) {
			this.mCurrentPosition = currentPosition;
		}

		public void onClick(View v) {
			Reports entry = ReportsArrayAdapter.this.getItem(mCurrentPosition);
			if (entry.getWatched() != null && entry.getWatched())
				((ImageView) v)
						.setImageResource(android.R.drawable.star_big_on);
			else
				((ImageView) v)
						.setImageResource(android.R.drawable.star_big_off);
			entry.setWatched(!entry.getWatched());
			// Setup DB operation
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(ReportsArrayAdapter.this.getContext(),
					"foodabc-db", null);
			db = helper.getWritableDatabase();
			mDaoMaster = new DaoMaster(db);
			mDaoSession = mDaoMaster.newSession();
			mReportsDao = mDaoSession.getReportsDao();
			mReportsDao.update(entry);
			db.close();
			mCallback.onWatchClicked();
			ReportsArrayAdapter.this.notifyDataSetChanged();
		}
	}

	// Setup filter for searchView , overwrite parent's(ArrayAdapter<T>'s)
	// Filter
	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	/**
	 * <p>
	 * An array filter constrains the content of the array adapter with a
	 * prefix. Each item that does not start with the supplied prefix is removed
	 * from the list.
	 * </p>
	 */
	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<Reports>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<Reports> list;
				synchronized (mLock) {
					list = new ArrayList<Reports>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				ArrayList<Reports> values;
				synchronized (mLock) {
					values = new ArrayList<Reports>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<Reports> newValues = new ArrayList<Reports>();

				for (int i = 0; i < count; i++) {
					final Reports value = values.get(i);
					final String valueText = value.getProductname()
							.toLowerCase(); // 20131108 Nevin changed the match
											// criteria

					// First match against the whole, non-splitted value
					if (value.getProductname().contains(prefixString)
							|| value.getCompanyname().contains(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with
						// space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// no inspection unchecked
			mObjects = (List<Reports>) results.values;
			ReportsArrayAdapter.this.clear(); // nevin modify this line
			ReportsArrayAdapter.this.addAll(mObjects); // nevin modify this line
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
