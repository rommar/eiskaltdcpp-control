package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eiskaltdcpp.control.AbstractPublisher.Notificator;
import com.eiskaltdcpp.control.SearchDataModel.SearchDataModelListener;
import com.eiskaltdcpp.control.SearchDataModel.SearchResult;

public class SearchResultsView extends AbstractView<SearchDataModel, SearchDataModel.SearchDataModelListener>
{

	public static interface UserActionsListener 
	{
		void downloadItemClicked(SearchResult resultsGroup);
	}
	
	private ArrayList<SearchResult> results;
	private SearchResultItemAdapter adapter;
	private SearchListener listener;
	private UserActionsListener actionsListener;
	
	public SearchResultsView(Context context, UserActionsListener userActionsListener)
	{
		results = new ArrayList<SearchResult>();
		//actionsNotifier = new UserActionsNotifier();
		actionsListener = userActionsListener;
		adapter = new SearchResultItemAdapter(context, results, actionsListener);
		listener = new SearchListener();
	}
	
	public SearchResultItemAdapter getListViewAdapter()
	{
		return adapter;
	}
	
	@Override
	protected SearchDataModelListener getListener()
	{
		return listener;
	}
	
	private class SearchListener implements SearchDataModel.SearchDataModelListener
	{
		@Override
		public void onClearResults()
		{
			results.clear();
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onNewResult(SearchResult result)
		{
			results.add(result);
			
		}
		
		@Override
		public void onUpdateResult(SearchResult result)
		{
			sort();
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onBatchUpdateCompleted()
		{
			sort();
			adapter.notifyDataSetChanged();
		}
		
		private void sort()
		{
			adapter.sort(new Comparator<SearchResult>()
			{
				@Override
				public int compare(SearchResult lhs, SearchResult rhs)
				{
					return (int) (rhs.getCount() - lhs.getCount());
				}
			});
		}
		
	}
	
	private static class SearchResultItemAdapter extends StandardListViewAdapter<SearchResult>
	{
		private UserActionsListener actionsListener;
		public SearchResultItemAdapter(Context context, ArrayList<SearchResult> resultList, UserActionsListener userActionsListener)
		{
			super(context, R.layout.result_item, resultList, R.menu.search_results_popup_menu);
			this.actionsListener = userActionsListener;
		}
		
		private void downloadItemClicked(final SearchResult resultsGroup)
		{
			if (actionsListener != null)
				actionsListener.downloadItemClicked(resultsGroup);
		}

		@Override
		protected String getItemTitle(SearchResult item)
		{
			return item.title;
		}

		@Override
		protected String getItemDetails(SearchResult item)
		{
			String count = "Count: " + Long.toString(item.getCount());
			String size = "Size: " + Long.toString(item.realSize / (1024 * 1024)) + " MiB";
			return count + "; " + size;
		}

		@Override
		protected boolean onMenuItemClicked(int resource, SearchResult resultsGroup)
		{
			switch (resource) 
			{
	        case R.id.search_result_action_download:
	            downloadItemClicked(resultsGroup);
	            return true;
			}
			return false;
		}
		
	}
	
}