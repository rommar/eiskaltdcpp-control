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
	
	private static class SearchResultItemAdapter extends ViewHolderArrayAdapter<SearchResult, ItemViewHolder>
	{
		private UserActionsListener actionsListener;
		public SearchResultItemAdapter(Context context, ArrayList<SearchResult> resultList, UserActionsListener userActionsListener)
		{
			super(context, R.layout.result_item, resultList);
			this.actionsListener = userActionsListener;
		}
		
		public void showPopupMenu(View view, final SearchResult resultsGroup)
		{
			PopupMenu popup = new PopupMenu(getContext(), view);
			
			MenuInflater inflater = popup.getMenuInflater();
		    inflater.inflate(R.menu.search_results_popup_menu, popup.getMenu());
		    
		    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				
				@Override
				public boolean onMenuItemClick(MenuItem item)
				{
					switch (item.getItemId()) 
					{
			        case R.id.search_result_action_download:
			            downloadItemClicked(resultsGroup);
			            return true;

			        default:
			        	return false;
					}
				}
			});

		    
		    popup.show();

		}
		
		private void downloadItemClicked(final SearchResult resultsGroup)
		{
			actionsListener.downloadItemClicked(resultsGroup);
			/*
			Log.i("downloadItemClicked", "downloadItemClicked");
			
			DownloadQueueController.getInstance().addItem(
					resultsGroup.getFirstItem().fileName, 
					resultsGroup.realSize, 
					resultsGroup.tth, 
					"");
			*/
		}
		
		@Override
		public ItemViewHolder createViewHolder(View view)
		{
			return new ItemViewHolder(view);
		}
		
		@Override
		public void fillViewHolder(ItemViewHolder holder, int position)
		{
			final SearchResult resultsGroup = getArray().get(position);
			
			holder.title.setText(resultsGroup.title);
			holder.details.setText("Count: " + Long.toString(resultsGroup.getCount()));
			//TODO: Can be optimized: don't create listener each time
			holder.popup_icon.setOnClickListener(new View.OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					showPopupMenu(v, resultsGroup);
					
				}
			});			
		}

		
		
	}
	
}