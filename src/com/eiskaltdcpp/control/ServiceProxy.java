package com.eiskaltdcpp.control;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import org.json.rpc.client.JsonRpcParam;

import android.util.Log;



public class ServiceProxy
{
	private static interface Hub 
	{
		String list();
	}
	
	
	
	public static class QueueRecords
	{
		public HashMap<String, HashMap<String, String> > values;
	}
	
	private static interface Queue
	{
		HashMap<String, com.google.gson.internal.StringMap<String> > list();
		int add(
				@JsonRpcParam(name="filename") String fileName, 
				@JsonRpcParam(name="size") long size,
				@JsonRpcParam(name="tth") String tth,
				@JsonRpcParam(name="directory") String directory);
		
		int remove (@JsonRpcParam(name="target") String target);
	}

	
	public static class SearchResults
	{
		public HashMap<String, String>[] values;
	}
	
	
	private static interface Search
	{
		int send( @JsonRpcParam(name="searchstring") String searchString, 
				@JsonRpcParam(name="searchtype") int searchType, 
				@JsonRpcParam(name="sizemode") int sizeMode, 
				@JsonRpcParam(name="sizetype") int sizeType, 
				@JsonRpcParam(name="size") double size, 
				@JsonRpcParam(name="huburls") String hubUrls);
		HashMap<String, String>[] getresults(@JsonRpcParam(name="huburl") String hubUrl);
		void clear();
	}
	
	
	
	private Search search;
	private Hub hub;
	private Queue queue;
	
	
	private static ServiceProxy instance;
	public static ServiceProxy getInstance()
	{
		if (instance == null)
			instance = new ServiceProxy();
		return instance;
	}
	
	private ServiceProxy()
	{
		URL url = null;
		try
		{
			url = new URL("http://192.168.0.2:3121");
		} catch (MalformedURLException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(url);

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		
		search = invoker.get(transport, "search", Search.class); 
		hub = invoker.get(transport, "hub", Hub.class);
		queue = invoker.get(transport, "queue", Queue.class);
		
		
	}
	
	
	
	public void sendSearch(final String searchString, final int searchType, final int sizeMode, final int sizeType, final double size, final String hubUrls, final AsyncTaskResultListener<Integer> listener)
	{
		AsyncAction<Integer> searchSendAction = new AsyncAction<Integer>(listener)
		{
			@Override
			protected Integer execAction()
			{
				search.clear();
				return search.send(searchString, searchType, sizeMode, sizeType, size, hubUrls);
			}
		};
		
		searchSendAction.execute();
		
	}
	
		
	public void getSearchResults(final String hubUrl, final AsyncTaskResultListener<SearchResults> listener)
	{
		AsyncAction<SearchResults> getSearchResultsAction = new AsyncAction<SearchResults> (listener)
			{

				@Override
				protected SearchResults execAction()
				{
					SearchResults results = new SearchResults();
					long startTime = System.currentTimeMillis();
					results.values = search.getresults(hubUrl);
					Log.i("getSearchResults", "Time consumed for results retrieval: " + Long.toString(System.currentTimeMillis() - startTime));
					return results;
				}
		
			};
		getSearchResultsAction.execute();
	}
	
	public void listQueue(final AsyncTaskResultListener<QueueRecords> listener)
	{
		AsyncAction<QueueRecords> getQueueRecords = new AsyncAction<QueueRecords> (listener)
		{

			@Override
			protected QueueRecords execAction()
			{
				// Workaround. Gson doesn't support (de)serialization of maps containing maps.
				// See also: stackoverflow.com/questions/4547739/how-to-serialize-a-map-of-a-map-with-gson
				HashMap<String, com.google.gson.internal.StringMap<String> > values = queue.list();
				
				QueueRecords records = new QueueRecords();
				if (values == null)
				{
					records.values = null;
				}
				else
				{
					records.values = new HashMap<String, HashMap<String,String>>();
					for (HashMap.Entry<String, com.google.gson.internal.StringMap<String> > entry : values.entrySet())
					{
						HashMap<String, String> convertedValue = new HashMap<String, String>();
						convertedValue.putAll(entry.getValue());
						records.values.put(entry.getKey(), convertedValue);
					}
				}
				return records;
			}
		};

		getQueueRecords.execute();

	
	}
	
	public void addQueueItem(final String fileName, final long size, final String tth, final String directory, final AsyncTaskResultListener<Boolean> listener)
	{
		AsyncAction<Boolean> addQueueItemAction = new AsyncAction<Boolean> (listener)
		{

			@Override
			protected Boolean execAction()
			{
				int result = queue.add(fileName, size, tth, directory);
				return result == 0;
			}
	
	
		};
		
		addQueueItemAction.execute();
	}
	
	public void removeQueueItem(final String target, final AsyncTaskResultListener<Boolean> listener)
	{
		AsyncAction<Boolean> removeQueueItem = new AsyncAction<Boolean> (listener)
		{
			@Override
			protected Boolean execAction()
			{
				int result = queue.remove(target);
				return result == 0;
			}
		};
				
		removeQueueItem.execute();
	}
	
}
