package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
	
	public static class SearchResultItemAdapter extends ArrayAdapter<SearchResult> 
	{

		private Searcher searcher = null;
		private Context context = null;
		private ArrayList<SearchResult> resultList = null;
		
		public SearchResultItemAdapter(Context context, ArrayList<SearchResult> resultList)
		{
			super(context, R.layout.result_item, resultList);
			
			if (searcher == null)
				searcher = Searcher.getInstance();
			
			this.context = context;
			this.resultList = resultList;
		}

		@Override
		public void notifyDataSetChanged()
		{
			super.notifyDataSetChanged();
		}
		
		private static class ViewHolder
		{
			public TextView title;
			public TextView details;
			public ImageView icon;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.result_item, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.title =  (TextView)convertView.findViewById(R.id.resource_title);
				holder.details = (TextView)convertView.findViewById(R.id.resource_details);
				convertView.setTag(holder);
			}
			
			ViewHolder holder = (ViewHolder)convertView.getTag();
			
			SearchResult resultsGroup = resultList.get(position);
			holder.title.setText(resultsGroup.title);
			holder.details.setText("Count: " + Long.toString(resultsGroup.getCount()));
			
			return convertView;
		}
		
		
	}
	
	public static class SearchResultsDataModel
	{

		private ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		private SearchResultItemAdapter adapter = null;
		//EXP: make private
		public SearchListener listener = null;
		
		
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
