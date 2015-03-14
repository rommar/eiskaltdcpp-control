package com.eiskaltdcpp.control;

import android.util.Log;

import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueSingleton
{

	public static interface DownloadQueueListenerInterface
	{
		void onDownloadQueueUpdated();
		
	}
	
	
	private static DownloadQueueSingleton instance;
	
	public static DownloadQueueSingleton getInstance()
	{
		if (instance == null)
		{
			instance = new DownloadQueueSingleton();
		}
		return instance;
	}
	
	
	private DownloadQueueSingleton()
	{
		queueListTask.start(2000);
	}
	
	PeriodicTask queueListTask = new PeriodicTask(Long.MAX_VALUE, 5000, new Runnable()
	{
		@Override
		public void run()
		{
			ServiceProxy.getInstance().listQueue(new AsyncTaskResultListener<ServiceProxy.QueueRecords>()
			{
				
				@Override
				public void onError(Throwable error)
				{
					
					Log.e("queueListTask", "Error", error);
				}
				
				@Override
				public void onCompleted(QueueRecords result)
				{
					Log.i("queueListTask", "OK");
					//Log.i("queueListTask", "Size: " + Integer.toString(result.values.length));
				}
			});
			
		}
	});
	
	void addItem(String fileName, long size, String tth, String directory)	
	{
		ServiceProxy.getInstance().addQueueItem(fileName, size, tth, directory, new AsyncTaskResultListener<Boolean>()
		{
			
			@Override
			public void onError(Throwable error)
			{
				Log.e("queueAddItem", "Error", error);
				
			}
			
			@Override
			public void onCompleted(Boolean result)
			{
				Log.i("queueAddItem", "OK");
				
				
			}
		});
	}
	
}
