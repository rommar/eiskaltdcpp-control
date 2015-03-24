package com.eiskaltdcpp.control;

import android.util.Log;

import com.eiskaltdcpp.control.DownloadQueueView.QueueItem;
import com.eiskaltdcpp.control.DownloadQueueView.UserActionsListener;
import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueController
{
	private DownloadQueueDataModel model;
	private ViewUserActionsListener userActionsListener;
	
	public DownloadQueueController(DownloadQueueDataModel model)
	{
		this.model = model;
		userActionsListener = new ViewUserActionsListener();
		queueListTask.start(2000);
	}
	
	protected UserActionsListener getUserActionsListener()
	{
		return userActionsListener;
	}
	
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
	
	
	public void addItem(String fileName, long size, String tth, String directory)	
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
	
	private class ViewUserActionsListener implements UserActionsListener
	{

		@Override
		public void removeQueueItem(QueueItem item)
		{
			Log.i("ViewUserActionsListener", "removeQueueItem");
			
			ServiceProxy.getInstance().removeQueueItem(item.target, new AsyncTaskResultListener<Boolean>()
			{
				
				@Override
				public void onError(Throwable error)
				{
					Log.e("removeQueueItem", "Error", error);
					
				}
				
				@Override
				public void onCompleted(Boolean result)
				{
					Log.i("removeQueueItem", "OK");
					
				}
			});
			
		}
	
	}
	
}
