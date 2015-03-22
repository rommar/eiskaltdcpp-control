package com.eiskaltdcpp.control;

import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueDataModel extends AbstractPublisher<DownloadQueueDataModel.DownloadQueueModelListener>
{
	
	public static interface DownloadQueueModelListener
	{
		void onUpdated(QueueRecords records);
	}
	
	private QueueRecords records = new QueueRecords();
	
	public void setQueueRecords(final QueueRecords newRecords)
	{
		records = newRecords;
		
		notifyEach(new Notificator<DownloadQueueDataModel.DownloadQueueModelListener>()
		{
			@Override
			public void notify(DownloadQueueDataModel.DownloadQueueModelListener listener)
			{
				listener.onUpdated(records);
			}
		});
		
		
	}
			
}