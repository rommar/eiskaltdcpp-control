package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.eiskaltdcpp.control.Searcher.SearchListenerInterface;
import com.eiskaltdcpp.control.Searcher.SearchResult;

public class SearchUi
{

	public static class SearchListener implements SearchListenerInterface
	{
		private ArrayList<SearchResult> results;
		ArrayAdapter<SearchResult> adapter;
		public SearchListener(ArrayList<SearchResult> results, ArrayAdapter<SearchResult> adapter)
		{
			this.results = results; 
			this.adapter = adapter;
		}
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
	
	public static class SearchResultItemAdapter extends ViewHolderArrayAdapter<SearchResult>
	{
		
		public SearchResultItemAdapter(Context context, ArrayList<SearchResult> resultList)
		{
			super(context, R.layout.result_item, resultList);
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
		
		private void downloadItemClicked(SearchResult resultsGroup)
		{
			Log.i("downloadItemClicked", "downloadItemClicked");
			
			DownloadQueueSingleton.getInstance().addItem(
					resultsGroup.getFirstItem().fileName, 
					resultsGroup.realSize, 
					resultsGroup.tth, 
					"");
		}
		
		@Override
		public Object createViewHolder(View view)
		{
			return new ItemViewHolder(view);
		}
		
		@Override
		public void fillViewHolder(Object viewHolderObject, int position)
		{
			ItemViewHolder holder = (ItemViewHolder)viewHolderObject;
			
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
	
	public static class SearchResultsDataModel
	{

		private ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		private SearchResultItemAdapter adapter = null;
		private SearchListener listener = null;
		
		
		private static SearchResultsDataModel instance = null;
		private SearchResultsDataModel()
		{
			
		}
		
		public static SearchResultsDataModel getInstance()
		{
			if (instance == null)
				instance = new SearchResultsDataModel();
			return instance;
		}
		
		public void initialize(Context context)
		{
			adapter = new SearchResultItemAdapter(context, results);
		    listener = new SearchListener(results, adapter);
		    Searcher.getInstance().setListener(listener);
		    
		    
		}
		
		public ListAdapter getListViewAdapter()
		{
			return adapter;
		}
	}

		
}
