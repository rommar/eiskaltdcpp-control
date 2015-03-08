package com.eiskaltdcpp.control;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import org.json.rpc.client.JsonRpcParam;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;



public class ServiceProxy
{
	private static interface Hub 
	{
		String list();
	}
	
	

	
	
	public static class SearchResults
	{
		public HashMap<String, String>[] values;
	}
	
	

	
	
	public static class AsyncTaskResult<T>
	{
		private Throwable exception;
		private T value;
		
		public AsyncTaskResult(T value)
		{
			this.value = value;
		}
		
		public AsyncTaskResult(Throwable exception)
		{
			this.exception = exception; 
		}
		
		public Throwable getError()
		{
			return exception;
		}
		
		public T getValue()
		{
			return value;
		}		
	}
	
	/*
	public static interface AsyncTaskListener<T>
	{
		void onCompleted(T result);
		void onError(Throwable error);
		
	}
	*/

	public static interface AsyncTaskResultListener<T>
	{
		void onCompleted(T result);
		void onError(Throwable error);
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
	
	
	
	public Search search;
	public Hub hub;
	
	
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

		
		
	}
	
	
	
	private static abstract class AsyncAction<ResultT> extends AsyncTask<Void, Void, AsyncTaskResult<ResultT> >
	{

		protected abstract ResultT execAction();
		protected void onError(Throwable error)
		{
			if (listener != null)
				listener.onError(error);
		}
		protected void onCancelled() {}
		protected void onCompleted(ResultT result)
		{
			if (listener != null)
				listener.onCompleted(result);
		}
		
		private AsyncTaskResultListener<ResultT> listener;
		
		public AsyncAction(AsyncTaskResultListener<ResultT> resultListener)
		{
			super();
			listener = resultListener;
		}
		
		@Override
		protected final AsyncTaskResult<ResultT> doInBackground(Void... params)
		{
			try
			{
				ResultT result = execAction();
				return new AsyncTaskResult<ResultT>(result);
			}
			catch(Throwable error)
			{
				return new AsyncTaskResult<ResultT>(error);
			}
		}
		
		@Override
		protected final void onPostExecute(AsyncTaskResult<ResultT> result)
		{
			Throwable error = result.getError();
			if (error != null)
			{
				onError(error);
			}
			else if (isCancelled())
			{
				onCancelled();
			}
			else
			{	
				onCompleted(result.getValue());
			}
		}

		
	}

	
	
	
	public void sendSearch(final String searchString, final int searchType, final int sizeMode, final int sizeType, final double size, final String hubUrls, final AsyncTaskResultListener<Integer> listener)
	{
		AsyncAction<Integer> searchSendAction = new AsyncAction<Integer>(listener)
		{
			@Override
			protected Integer execAction()
			{
				//EXP:
				String value = hub.list();
				if (value.length() == 0)
				{
					Log.e("sendSearch", "hub list empty");
					throw new Error("hub list empty");
				}
				//EXP:
				search.clear();
				/*
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				
				return search.send(searchString, searchType, sizeMode, sizeType, size, hubUrls);
			}
		};
		
		searchSendAction.execute();
		
	}
	
	/*
	public void getSearchResults(final String hubUrl, final AsyncTaskResultListener<HashMap<String, String>[]> listener)
	{
		AsyncAction<HashMap<String, String>[]> getSearchResultsAction = new AsyncAction<HashMap<String, String>[]> (listener)
			{

				@Override
				protected HashMap<String, String>[] execAction()
				{
					return search.getresults(hubUrl);
				}
		
			};
		getSearchResultsAction.execute();
	}
	*/

	
	public void getSearchResults(final String hubUrl, final AsyncTaskResultListener<SearchResults> listener)
	{
		AsyncAction<SearchResults> getSearchResultsAction = new AsyncAction<SearchResults> (listener)
			{

				@Override
				protected SearchResults execAction()
				{
					SearchResults results = new SearchResults();
					results.values = search.getresults(hubUrl);
					return results;
				}
		
			};
		getSearchResultsAction.execute();
	}
	
	
	
}
