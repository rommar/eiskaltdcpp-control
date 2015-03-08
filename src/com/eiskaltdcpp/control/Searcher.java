package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import android.app.AlertDialog;
import android.content.Context;
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
		void onUpdateResult(SearchResult result);
		void onBatchUpdateCompleted();
	}

	
	public static class SearchResultItem
	{		
		public String title = "";
		public int slots = 0;
	}
	
	public static class SearchResult
	{
		public String title = "";
		public HashMap<String, SearchResultItem> children = new LinkedHashMap<String, SearchResultItem>();
		public long getCount()
		{
			return children.size();
		}
		
		
	}
		
	
	
	private static Searcher instance = null;
	private HashMap<String, SearchResult> searchResults = new LinkedHashMap<String, SearchResult>();
	
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
	
	
	
	private void updateResults(SearchResults results)
	{
		if (results != null && results.values != null && results.values.length > 0)
		{
			for (HashMap<String, String> resultMap: results.values)
			{
				String cid = resultMap.get("CID");
				String tth = resultMap.get("TTH");
				if (cid != null && tth != null)
				{
					//String id = cid + tth;
					//String id = tth;
					
					// If no result group exists for TTH
					if (!searchResults.containsKey(tth))
					{
						SearchResult resultGroup = searchResultMapToObject(resultMap);
						updateSearchResultGroup(resultGroup, resultMap);
						searchResults.put(tth, resultGroup);
						if (listener != null)
						{
							listener.onNewResult(resultGroup);
						}
					}
					else // results group exists
					{
						SearchResult resultGroup = searchResults.get(tth);
						
						if (!resultGroup.children.containsKey(cid))
						{
							updateSearchResultGroup(resultGroup, resultMap);
							
							if (listener != null)
							{
								listener.onUpdateResult(resultGroup);
							}
						}
						
						
						
					}
				}
			}
			if (listener != null)
				listener.onBatchUpdateCompleted();
		}
			
	}
	
	public void clearResults()
	{
		searchResults.clear();
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
		
		return result;
		
	}
	
	private static void updateSearchResultGroup(SearchResult resultGroup, HashMap<String, String> resultMap)
	{
		if (!resultMap.containsKey("Filename"))
			return;
		
		String cid = resultMap.get("CID");
		if (cid == null)
			return;
		
		SearchResultItem resultItem = new SearchResultItem();
		resultItem.title = resultMap.get("Filename");
		
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
		
		resultGroup.children.put(cid, resultItem);
		
		
		
	}
	
	public static class PeriodicTask
	{
		//private long timerCount = 0;
		//private final long maxTimerCount;
		private long time = 0;
		private final long maxTime;
		private final long period;
		private Handler timerHandler = new Handler();
		private Runnable targetRunnable;
		
		private Runnable timerRunnable = new Runnable()
		    {
		    	public void run()
		    	{
		    		if (time >= maxTime)
		    			return;
		    		
		    		
		    		targetRunnable.run();
		    		
					timerHandler.postDelayed(this, period);
					time += period;
		    	}
		    };
		
		public PeriodicTask(long durationMs, long periodMs, Runnable r)
		{
			maxTime = durationMs;
			period = periodMs;
			targetRunnable = r;
			
		}
		
		public void start(long delayMs)
		{
			time = 0;
			timerHandler.postDelayed(timerRunnable, delayMs);
		}
		
		public void stop()
		{
			time = maxTime;
		}
	}
	
	
    
    PeriodicTask searchResultsTask = new PeriodicTask(60000, 5000, new Runnable()
	{
		
		@Override
		public void run()
		{
			//TODO: Avoid sending new request before receiving reply.
			Log.i("SearchActivityTimer", "Sending request");
    		ServiceProxy.getInstance().getSearchResults("", 
				new AsyncTaskResultListener<SearchResults>()
				{

					@Override
					public void onCompleted(SearchResults results)
					{
						Log.i("SearchActivityTimer", "Completed");
						Log.i("SearchActivityTimer", "Results count: " + Integer.toString(Searcher.getInstance().getCount()));
						
						if (results.values == null)
						{
							Log.e("SearchActivityTimer", "ERROR: NULL Search result values");
							//searchResultsTask.stop();
						}
						
						Searcher.getInstance().updateResults(results);
					}

					@Override
					public void onError(Throwable error)
					{
						Log.e("SearchActivityTimer", "Error" + ": " + error.toString());
						
						searchResultsTask.stop();
					}
				
				});

		}
	});
    
	public void sendSearch(String searchString, Context context)
	{
		searchResultsTask.stop();
		Searcher.getInstance().clearResults();
		
		final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

		dlgAlert.setTitle("App Title");
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(true);
		
		ServiceProxy.getInstance().sendSearch(searchString, 0, 0, 0, 0, "", 
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
						
						searchResultsTask.start(2000);
						
					}
				});
	}
	public void stopSearch()
	{
		searchResultsTask.stop();
	}


}
