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
import com.openfooddata.app.misc.MyVolley;
import com.openfooddata.app.misc.ReportsArrayAdapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;


public class SponsorViewFragment extends Fragment   {

	protected static final String TAG = "nevin";

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reports_view,
				container, false);
		
		return rootView;
	}
	

	
}
