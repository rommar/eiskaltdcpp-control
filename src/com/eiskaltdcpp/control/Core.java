package com.eiskaltdcpp.control;

import android.content.Context;

public class Core
{

	public static Core getInstance()
	{
		if (instance == null)
		{
			instance = new Core();
		}
		return instance;
	}
	
	public DownloadQueueController getDownloadQueueController()
	{
		return downloadQueueController;
	}

	public SearchController getSearchController()
	{
		return searchController;
	}
	
	public SearchResultsView createSearchResultsView(Context context)
	{
		SearchResultsView view = new SearchResultsView(context, searchController.getUserActionsListener());
		view.setModel(searchModel);
		return view;
	}
	
	public DownloadQueueView createDownloadQueueView(Context context)
	{
		DownloadQueueView view = new DownloadQueueView(context, null);
		view.setModel(downloadQueueModel);
		return view;
	}
	
	
	
	private Core() 
	{
		searchModel = new SearchDataModel();
		downloadQueueModel = new DownloadQueueDataModel();
		
		searchController = new SearchController(searchModel);
		downloadQueueController = new DownloadQueueController(downloadQueueModel);
	}
	
	
	
	
	private static Core instance = null;
	
	private SearchDataModel searchModel;
	private SearchController searchController;
	
	private DownloadQueueDataModel downloadQueueModel;
	private DownloadQueueController downloadQueueController;
	
}
