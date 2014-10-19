package com.hacktm.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {
	public MyHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyHorizontalScrollView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context) {
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(false);
	}

	public void initViews(View[] children, int scrollToViewIdx,
			SizeCallback sizeCallback) {
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}

		OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent,
				children, scrollToViewIdx, sizeCallback);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
		ViewGroup parent;
		View[] children;
		int scrollToViewIdx;
		int scrollToViewPos = 0;
		SizeCallback sizeCallback;

		public MyOnGlobalLayoutListener(ViewGroup parent, View[] children,
				int scrollToViewIdx, SizeCallback sizeCallback) {
			this.parent = parent;
			this.children = children;
			this.scrollToViewIdx = scrollToViewIdx;
			this.sizeCallback = sizeCallback;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onGlobalLayout() {

			final HorizontalScrollView me = MyHorizontalScrollView.this;

			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

			sizeCallback.onGlobalLayout();

			parent.removeViewsInLayout(0, children.length);

			final int w = me.getMeasuredWidth();
			final int h = me.getMeasuredHeight();

			int[] dims = new int[2];
			scrollToViewPos = 0;
			for (int i = 0; i < children.length; i++) {
				sizeCallback.getViewSize(i, w, h, dims);
				children[i].setVisibility(View.VISIBLE);
				parent.addView(children[i], dims[0], dims[1]);
				if (i < scrollToViewIdx) {
					scrollToViewPos += dims[0];
				}
			}

			new Handler().post(new Runnable() {
				@Override
				public void run() {
					me.scrollBy(scrollToViewPos, 0);
				}
			});
		}
	}

	public interface SizeCallback {
		public void onGlobalLayout();

		public void getViewSize(int idx, int w, int h, int[] dims);
	}

	public static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;
		Context _context;

		public SizeCallbackForMenu(View btnSlide, Context ctx) {
			super();
			this.btnSlide = btnSlide;
			this._context = ctx;
		}

		@Override
		public void onGlobalLayout() {
			Resources r = _context.getResources();
			int px = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());
			btnWidth = btnSlide.getMeasuredWidth() + px;

		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - btnWidth;
			}
		}
	}
}
