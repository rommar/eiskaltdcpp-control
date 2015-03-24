package com.eiskaltdcpp.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.eiskaltdcpp.control.ServiceProxy.SearchResults;

public class SearchDataModel extends AbstractPublisher<SearchDataModel.SearchDataModelListener>
{
	public static class SearchResultItem
	{		
		public String title = "";
		public int slots = 0;
		public String fileName = "";		
	}
	
	public static class SearchResult
	{
		public String title = "";
		public String tth = "";
		public long realSize = 0;
		public HashMap<String, SearchResultItem> children = new LinkedHashMap<String, SearchResultItem>();
		public long getCount()
		{
			return children.size();
		}
		
		public SearchResultItem getFirstItem()
		{
			//TODO: Is there any less ugly way to get first item from map?
			return children.entrySet().iterator().next().getValue();
		}
		
	}
		
	public static interface SearchDataModelListener
	{
		void onClearResults();
		void onNewResult(SearchResult result);
		void onUpdateResult(SearchResult result);
		void onBatchUpdateCompleted();
	}
	
	private HashMap<String, SearchResult> searchResults = new LinkedHashMap<String, SearchResult>();
	
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
					//String id = cid + tth;
					//String id = tth;
					
					// If no result group exists for TTH
					if (!searchResults.containsKey(tth))
					{
						SearchResult resultGroup = searchResultMapToObject(resultMap);
						updateSearchResultGroup(resultGroup, resultMap);
						searchResults.put(tth, resultGroup);
						notifyNewResult(resultGroup);
					}
					else // results group exists
					{
						SearchResult resultGroup = searchResults.get(tth);
						
						if (!resultGroup.children.containsKey(cid))
						{
							updateSearchResultGroup(resultGroup, resultMap);
							notifyResultUpdated(resultGroup);
						}
						
						
						
					}
				}
			}
			notifyBatchUpdateCompleted();
		}
			
	}

	public void clearResults()
	{
		searchResults.clear();
		notifyCleared();
	}
	
	public int getCount()
	{
		return searchResults.size();
	}
	
	public Collection<SearchResult> getResults()
	{
		return searchResults.values();
	}
	
	
	private void notifyNewResult(final SearchResult resultGroup)
	{
		notifyEach(new Notificator<SearchDataModel.SearchDataModelListener>()
		{
			@Override
			public void notify(SearchDataModel.SearchDataModelListener listener)
			{
				listener.onNewResult(resultGroup);
			}
		});
	}
	
	private void notifyResultUpdated(final SearchResult resultGroup)
	{
		notifyEach(new Notificator<SearchDataModel.SearchDataModelListener>()
		{
			@Override
			public void notify(SearchDataModel.SearchDataModelListener listener)
			{
				listener.onUpdateResult(resultGroup);
			}
		});
	}
	
	private void notifyBatchUpdateCompleted()
	{
		notifyEach(new Notificator<SearchDataModel.SearchDataModelListener>()
		{
			@Override
			public void notify(SearchDataModel.SearchDataModelListener listener)
			{
				listener.onBatchUpdateCompleted();
			}
		});
	}
	
	private void notifyCleared()
	{
		notifyEach(new Notificator<SearchDataModel.SearchDataModelListener>()
		{
			@Override
			public void notify(SearchDataModel.SearchDataModelListener listener)
			{
				listener.onClearResults();
			}
		});
	}
	
	
	private static SearchResult searchResultMapToObject(HashMap<String, String> resultMap)
	{
		if (!resultMap.containsKey("Filename"))
			return null;
		SearchResult result = new SearchResult();
		result.title = resultMap.get("Filename");
		result.tth = resultMap.get("TTH");
		//TODO: add possible exception catch
		result.realSize = Long.parseLong(resultMap.get("Real Size")); 
		
		
		
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
		resultItem.fileName = resultMap.get("Filename");
		
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
	
	
}