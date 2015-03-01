package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.eiskaltdcpp.control.ServiceProxy.AsyncTaskResultListener;
import com.eiskaltdcpp.control.ServiceProxy.SearchResults;


public class Searcher
{
	


	public static interface SearchListenerInterface
	{
		void onClearResults();
		void onNewResult(SearchResult result);
		void onBatchUpdateCompleted();
	}

	
	public static class SearchResult
	{
		public String title = "";
		public String cid = "";
		public String tth = "";
		public int slots = 0;
		public int count = 0;
		
	}
	
	public static class SearchResultItemComparator implements Comparator<String>
	{

		private HashMap<String, SearchResult> resultsMap; 
		
		public SearchResultItemComparator(HashMap<String, SearchResult> searchResults)
		{
			resultsMap = searchResults;
		}

		@Override
		public int compare(String lhs, String rhs)
		{
			SearchResult left = resultsMap.get(lhs);
			SearchResult right = resultsMap.get(rhs);
			
			if (left.slots < right.slots)
				return -1;
			else if (left.slots > right.slots)
				return +1;
			return 0;
		}
		
	}
	

	
	private static Searcher instance = null;
	private HashMap<String, SearchResult> searchResults = new LinkedHashMap<String, SearchResult>();
	private SearchResultItemComparator comparator = new SearchResultItemComparator(searchResults);
	
	private SearchListenerInterface listener = null;
	
	private Searcher ()
	{
	}
	
	public static Searcher getInstance()
	{
		if (instance == null)
			instance = new Searcher();
		return instance;
	}
	
	public void setListener(SearchListenerInterface listener)
	{
		this.listener = listener;
	}
	
	
	
	public void updateResults(SearchResults results)
	{
		if (results != null && results.values != null && results.values.length > 0)
		{
			for (HashMap<String, String> resultMap: results.values)
			{
				String cid = resultMap.get("CID");
				String tth = resultMap.get("TTH");
				if (cid != null && tth != null)
				{
					String id = cid + tth;
					if (!searchResults.containsKey(id))
					{
						SearchResult result = searchResultMapToObject(resultMap);
						searchResults.put(id, result);
						//sortedResults.add(id);
						if (listener != null)
						{
							listener.onNewResult(result);
						}
						
					}
					else
					{
						searchResults.get(id).count++;
					}
				}
			}
			if (listener != null)
				listener.onBatchUpdateCompleted();
		}
			
	}
	
	public void clearResults()
	{
		if (listener != null)
			listener.onClearResults();
	}
	
	
	public int getCount()
	{
		return searchResults.size();
	}
	
	public Collection<SearchResult> getResults()
	{
		return searchResults.values();
	}
	
	private static SearchResult searchResultMapToObject(HashMap<String, String> resultMap)
	{
		if (!resultMap.containsKey("Filename"))
			return null;
		SearchResult result = new SearchResult();
		result.title = resultMap.get("Filename");
		
		if (resultMap.containsKey("Slots"))
		{
			String slotsStr = resultMap.get("Slots");
			try
			{
				//result.slots = Integer.parseInt(slotsStr);
			}
			catch(NumberFormatException err)
			{
				
			}
		}
		
		return result;
		
	}
	
	
	
	//TODO: Move to separate class
	private long timerCount = 0;
	private long maxTimerCount = 12;
	private Handler timerHandler = new Handler();
	

    private Runnable timerRunnable = new Runnable()
    {
    	public void run()
    	{
    		if (timerCount >= maxTimerCount)
    			return;
    		
    		Log.i("SearchActivityTimer", "Sending request");
    		ServiceProxy.getInstance().getSearchResults("hub.dc.zet", 
    				new AsyncTaskResultListener<SearchResults>()
    				{

						@Override
						public void onCompleted(SearchResults results)
						{
							Log.i("SearchActivityTimer", "Completed");
							Searcher.getInstance().updateResults(results);
						}

						@Override
						public void onError(Throwable error)
						{
							Log.e("SearchActivityTimer", "Error" + ": " + error.toString());
							
							timerCount = maxTimerCount;
						}
    				
    				});

			timerHandler.postDelayed(this, 5000);
			timerCount++;
    	}
    };
	
    
	public void sendSearch(String searchString, Context context)
	{
		timerCount = maxTimerCount;
		
		Searcher.getInstance().clearResults();
		
		
		
		final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

		dlgAlert.setTitle("App Title");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		
		ServiceProxy.getInstance().sendSearch(searchString, 0, 0, 0, 0, "hub.dc.zet", 
				new AsyncTaskResultListener<Integer>()
				{
					
					public void onError(Throwable error)
					{
						dlgAlert.setMessage("Error");
						dlgAlert.create().show();
					}
					
					public void onCompleted(Integer result)
					{
						dlgAlert.setMessage("OK");
						dlgAlert.create().show();
						
						timerCount = 0;
						timerHandler.postDelayed(timerRunnable, 1000);
						
					}
				});
	}
	


}
