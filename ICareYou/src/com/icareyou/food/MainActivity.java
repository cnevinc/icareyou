package com.icareyou.food;

import java.util.ArrayList;
import java.util.Locale;
 
import com.google.android.gms.ads.*;
import com.google.android.gms.plus.*;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
 
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, ReportViewFragment.OnWatchClickedListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	private PlusOneButton mPlusOneStandardButton;
	private AdView mAdView;
	ViewPager mViewPager;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main);

		mPlusOneStandardButton = (PlusOneButton) findViewById(R.id.sign_in_button);
		mPlusOneStandardButton.initialize(URL, PLUS_ONE_REQUEST_CODE); 
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						for (int i = 0; i < mSectionsPagerAdapter.fragments.size(); i++)
							MainActivity.this.mSectionsPagerAdapter.fragments.get(i).applyFilter();
					}
				});
  
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
  
		mAdView = (AdView) this.findViewById(R.id.ad);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		mAdView.loadAd(adRequest);
		mAdView.setAdListener(new AdListener() {
			public void onAdLoaded() {
			}

			public void onAdFailedToLoad(int errorcode) {
			}
			// Only implement methods you need.
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_rateme:
			Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				this.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the market",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		private static final String TAG = "nevin";
		public ArrayList<ReportViewFragment> fragments = new ArrayList<ReportViewFragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			fragments.add(new ReportViewFragment());
			fragments.add(new ReportViewFragment());
		}

		@Override
		public Fragment getItem(int position) {
			// 0 : Watch List
			Bundle bundle = new Bundle();
			bundle.putInt(ReportViewFragment.REPORT_ID, position);
			fragments.get(position).setArguments(bundle);
			Log.v(TAG, "Fragment getItem"+ position);
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onWatchClicked() {
		for (int i = 0; i < mSectionsPagerAdapter.fragments.size(); i++)
			// when an item in adapter is clicked, upate UI for each fragment
			this.mSectionsPagerAdapter.fragments.get(i).updateUI();
	}

	@Override
	public void onPause() {
		this.mAdView.pause();
		super.onPause();
	}

	private static final String URL = "https://developers.google.com/+";

    // The request code must be 0 or higher.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
	private static final String TAG = "nevin";
    
	@Override
	public void onResume() {
		super.onResume();
		
		Log.v(TAG,"onResume~~"+mPlusOneStandardButton.getHeight());
		
		this.mAdView.resume();
	}

	@Override
	public void onDestroy() {
		this.mAdView.destroy();
		super.onDestroy();
		for (int i = 0; i < mSectionsPagerAdapter.fragments.size(); i++)
			this.mSectionsPagerAdapter.fragments.get(i).closeDB();
	}
	
}
