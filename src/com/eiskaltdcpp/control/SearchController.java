package com.eiskaltdcpp.control;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.eiskaltdcpp.control.SearchResultsView.UserActionsListener;
import com.eiskaltdcpp.control.ServiceProxy.SearchResults;

public class SearchController
{
	private SearchDataModel model;
	private ViewUserActionsListener listener;

	public SearchController(SearchDataModel model)
	{
		this.model = model;
		this.listener = new ViewUserActionsListener();
	}
	
	public void sendSearch(String searchString, Context context)
	{
		searchResultsTask.stop();
		model.clearResults();
		
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


	protected UserActionsListener getUserActionsListener()
	{
		return listener;
	}
    
	
	private class ViewUserActionsListener implements UserActionsListener
	{
		@Override
		public void downloadItemClicked(SearchDataModel.SearchResult resultsGroup)
		{
			Log.i("downloadItemClicked", "downloadItemClicked");			
			
			
			Core.getInstance().getDownloadQueueController().addItem(
					resultsGroup.getFirstItem().fileName, 
					resultsGroup.realSize, 
					resultsGroup.tth, 
					"");
			
		}
	}
	
	
	private PeriodicTask searchResultsTask = new PeriodicTask(60000, 5000, new Runnable()
	{
		boolean bypass = false;
		@Override
		public void run()
		{
			// Avoid sending new request before receiving reply.
			if (bypass)
				return;
			
			bypass = true;
			
			Log.i("SearchActivityTimer", "Sending request");
    		ServiceProxy.getInstance().getSearchResults("", 
				new AsyncTaskResultListener<SearchResults>()
				{

					@Override
					public void onCompleted(SearchResults results)
					{
						Log.i("SearchActivityTimer", "Completed");
						//Log.i("SearchActivityTimer", "Results count: " + Integer.toString(Searcher.getInstance().getCount()));
						
						if (results.values == null)
						{
							Log.e("SearchActivityTimer", "ERROR: NULL Search result values");
							//searchResultsTask.stop();
						}
						
						model.updateResults(results);
						bypass = false;
					}

					@Override
					public void onError(Throwable error)
					{
						Log.e("SearchActivityTimer", "Error" + ": " + error.toString());
						
						searchResultsTask.stop();
						bypass = false;
					}
				
				});

		}
	});

	
}