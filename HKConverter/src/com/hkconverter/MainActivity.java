package com.hkconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static String[][] Weight = { { "斤(香港)", "1.653" }, { "公斤kg", "1" },
			{ "克g", "1000" }, { "磅lbs", "2.20462262" }, { "兩", "26.45" },
			{ "安士oz", "35.2739619" }, { "斤(中國)", "2" }, { "噸", "0.001" } };
	static String[][] Area = { { "平方米sq m", "1" },
			{ "平方呎sq ft", "10.76391042" }, { "公頃", "0.0001" },
			{ "維園", "5.26315789474e-06" }, { "標準足球場", "0.0001" },
			{ "標準籃球場", "0.00238" }, { "畝", "0.0015" }, { "坪(日本)", "0.3025" },
			{ "甲(台灣)", "1.03103412723e-04" }, { "斗(田)", "0.00148367952522" } };
	static String[][] Length = { { "呎ft", "3.2808399" }, { "米m", "1" },
			{ "厘米cm", "100" }, { "毫米mm", "1000" }, { "公里km", "0.001" },
			{ "吋inch", "39.37007874" }, { "碼yard", "1.09361" } };
	static String[][] Speed = { { "m/s", "1" }, { "km/h", "3.6" },
			{ "mi/h", "2.23694" }, { "海浬knot", "1.94384449" } };
	static String[][] Volume = { { "fl oz(us)", "33.814" }, { "ml", "1000" },
			{ "L", "1" }, { "m cube", "1" }, { "fl oz(uk)", "35.1951" },
			{ "gallon(uk)", "0.2199688" }, { "gallon(us)", "0.2641722" },
			{ "pint(uk)", "1.75975299" }, { "pint(us)", "2.11337642" } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//		int id = item.getItemId();
		//		if (id == R.id.action_settings) {
		//			return true;
		//		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);

			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static final String ARG_SECTION_CATEGORY = "section_category";
		private String fromUnit;
		private String toUnit;
		private String fromQty = "";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			ExtendedLayout.setActivity(getActivity());
			final TextView result = (TextView) rootView
					.findViewById(R.id.tv_to_qty);
			result.setText("???");
			final EditText input = (EditText) rootView
					.findViewById(R.id.et_from_qty);
			input.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					fromQty = input.getText().toString();
					result.setText(calculate());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
			final Spinner fromSpinner = (Spinner) rootView
					.findViewById(R.id.spinnerFrom);
			final Spinner toSpinner = (Spinner) rootView
					.findViewById(R.id.spinnerTo);
			List<String> spinnerArray = new ArrayList<String>();
			switch (this.getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 1:
				for (int i = 0; i < Weight.length; i++) {
					spinnerArray.add(Weight[i][0]);
				}
				break;
			case 2:
				for (int i = 0; i < Area.length; i++) {
					spinnerArray.add(Area[i][0]);
				}
				break;
			case 3:
				for (int i = 0; i < Length.length; i++) {
					spinnerArray.add(Length[i][0]);
				}
				break;
			case 4:
				for (int i = 0; i < Speed.length; i++) {
					spinnerArray.add(Speed[i][0]);
				}
				break;
			case 5:
				for (int i = 0; i < Volume.length; i++) {
					spinnerArray.add(Volume[i][0]);
				}
				break;
			default:
				for (int i = 0; i < Weight.length; i++) {
					spinnerArray.add(Weight[i][0]);
				}
				break;
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_spinner_item,
					spinnerArray);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			fromSpinner.setAdapter(adapter);
			toSpinner.setAdapter(adapter);
			toSpinner.setSelection(1);
			fromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					fromUnit = fromSpinner.getItemAtPosition(arg2).toString();
					result.setText(calculate());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			toSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					toUnit = toSpinner.getItemAtPosition(arg2).toString();
					result.setText(calculate());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			return rootView;
		}

		public String calculate() {
			double mIn = 0, mOut = 0;
			if (fromQty.equals("")
					|| (fromQty.indexOf(".") != fromQty.lastIndexOf("."))
					|| fromQty.indexOf(".") == 0)
				return "???";
			else {
				switch (this.getArguments().getInt(ARG_SECTION_NUMBER)) {
				case 1:
					for (int i = 0; i < Weight.length; i++) {
						if (Weight[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Weight[i][1]);
						if (Weight[i][0].equals(toUnit))
							mOut = Double.parseDouble(Weight[i][1]);
					}
				case 2:
					for (int i = 0; i < Area.length; i++) {
						if (Area[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Area[i][1]);
						if (Area[i][0].equals(toUnit))
							mOut = Double.parseDouble(Area[i][1]);
					}
				case 3:
					for (int i = 0; i < Length.length; i++) {
						if (Length[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Length[i][1]);
						if (Length[i][0].equals(toUnit))
							mOut = Double.parseDouble(Length[i][1]);
					}
				case 4:
					for (int i = 0; i < Speed.length; i++) {
						if (Speed[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Speed[i][1]);
						if (Speed[i][0].equals(toUnit))
							mOut = Double.parseDouble(Speed[i][1]);
					}
				case 5:
					for (int i = 0; i < Volume.length; i++) {
						if (Volume[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Volume[i][1]);
						if (Volume[i][0].equals(toUnit))
							mOut = Double.parseDouble(Volume[i][1]);
					}
				default:
					for (int i = 0; i < Weight.length; i++) {
						if (Weight[i][0].equals(fromUnit))
							mIn = Double.parseDouble(Weight[i][1]);
						if (Weight[i][0].equals(toUnit))
							mOut = Double.parseDouble(Weight[i][1]);
					}
				}
				return String.format(Locale.CHINESE, "%.2f",
						(Double.parseDouble(fromQty) * mOut / mIn));
			}
		}
	}

}
