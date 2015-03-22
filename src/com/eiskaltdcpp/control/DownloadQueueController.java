package com.eiskaltdcpp.control;

import android.util.Log;

import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueController
{

	private DownloadQueueDataModel model;
	
	public DownloadQueueController(DownloadQueueDataModel model)
	{
		this.model = model;
		queueListTask.start(2000);
	}
	
	//DownloadQueueListenerInterface listener;
	
	private PeriodicTask queueListTask = new PeriodicTask(Long.MAX_VALUE, 5000, new Runnable()
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
					if (result.values != null)
					{
						Log.i("queueListTask", "Size: " + Integer.toString(result.values.size()));
					}
					
					model.setQueueRecords(result);
					
					
				}
			});
			
		}
	});
	
	/*
	void setListener(DownloadQueueListenerInterface listener)
	{
		this.listener = listener;
	}
	*/
	
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