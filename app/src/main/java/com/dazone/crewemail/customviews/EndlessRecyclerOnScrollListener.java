package com.dazone.crewemail.customviews;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends
		RecyclerView.OnScrollListener {
	public static String TAG = EndlessRecyclerOnScrollListener.class
			.getSimpleName();

	private int previousTotal = 0;
	private boolean loading = true;
	private int visibleThreshold = 3;
	int firstVisibleItem, visibleItemCount, totalItemCount;

	private int current_page = 1;

	private LinearLayoutManager mLinearLayoutManager;

	public EndlessRecyclerOnScrollListener(
			LinearLayoutManager linearLayoutManager) {
		this.mLinearLayoutManager = linearLayoutManager;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);
		visibleItemCount = recyclerView.getChildCount();
		totalItemCount = mLinearLayoutManager.getItemCount();
		firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			// End has been reached

			// Do something
			current_page++;

			onLoadMore(current_page);

			loading = true;
		}
//		if(!loading){
//			 if(firstVisibleItem == 0 ){
//				 View v = recyclerView.getChildAt(0);
//	             int offset = (v == null) ? 0 : v.getTop();
//	             if (offset == 0 ) {
//	            	 onLoadOld(current_page);
//	            	 loading = true;
//	             }
//				 onLoadOld(current_page);
//			 }
//		}
	}

	public void reset(int previousTotal, boolean loading) {
		this.previousTotal = previousTotal;
		this.loading = loading;
	}


	public abstract void onLoadMore(int current_page);
}
