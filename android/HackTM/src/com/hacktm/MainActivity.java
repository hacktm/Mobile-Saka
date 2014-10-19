package com.hacktm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.hacktm.utils.MyHorizontalScrollView;

public class MainActivity extends Activity {

	private TimePicker mTimePicker;
	private EditText mDescription;
	private Spinner mDay;
	private View mInfoView, mMainView;
	private MyHorizontalScrollView mSrollView;

	private boolean menuOut = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSrollView = (MyHorizontalScrollView) mInflater.inflate(
				R.layout.horizontal_scroll_view, null);
		setContentView(mSrollView);
		mInfoView = mInflater.inflate(R.layout.activity_info, null);
		mMainView = mInflater.inflate(R.layout.activity_main, null);
		final View[] children = new View[] { mInfoView, mMainView };
		View btnHome = mMainView.findViewById(R.id.iv_menu);
		btnHome.setOnClickListener(mMenuListener);
		int scrollToViewIdx = 1;
		mSrollView.initViews(children, scrollToViewIdx,
				new MyHorizontalScrollView.SizeCallbackForMenu(btnHome, this));

		mainView();
		infoView();

		super.onCreate(savedInstanceState);
	}

	private void mainView() {
		// mTimePicker = (TimePicker) mMainView.findViewById(R.id.timepicker);
		mDescription = (EditText) mMainView.findViewById(R.id.et_description);
		mDay = (Spinner) mMainView.findViewById(R.id.sp_days);
		// mTimePicker.setIs24HourView(true);
	}

	private void infoView() {

	}

	private OnClickListener mMenuListener = new OnClickListener() {

		public void onClick(View v) {
			int menuWidth = mInfoView.getMeasuredWidth();
			mInfoView.setVisibility(View.VISIBLE);
			if (!menuOut) {
				int left = 0;
				mSrollView.smoothScrollTo(left, 0);
			} else {
				int left = menuWidth;
				mSrollView.smoothScrollTo(left, 0);
			}
			menuOut = !menuOut;
		}

	};

}
