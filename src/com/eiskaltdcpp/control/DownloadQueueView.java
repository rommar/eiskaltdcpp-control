package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;

import com.eiskaltdcpp.control.DownloadQueueDataModel.DownloadQueueModelListener;
import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueView extends AbstractView<DownloadQueueDataModel, DownloadQueueModelListener>
{
	private QueueViewAdapter adapter;
	private ArrayList<QueueItem> items;
	
	private Listener listener = new Listener();
	private UserActionsListener actionsListener;
	
	public static interface UserActionsListener 
	{
		
	}
	
	public DownloadQueueView(Context context, UserActionsListener userActionsListener)
	{
		items = new ArrayList<QueueItem>();
		adapter = new QueueViewAdapter(context, items);
		actionsListener = userActionsListener;
	}
	
	public QueueViewAdapter getListViewAdapter()
	{
		return adapter;
	}
	
	@Override
	protected DownloadQueueModelListener getListener()
	{
		return listener;
	}	
		
	
	private static class QueueItem
	{
		public String title = "";
		public String status = "";
	}
	
	private class Listener implements DownloadQueueModelListener
	{
		@Override
		public void onUpdated(QueueRecords records)
		{
			items.clear();
			if (records.values == null)
				return;
			
			//Log.i("onDownloadQueueUpdated", "OK");
			
			for (HashMap.Entry<String, HashMap<String, String> > entry : records.values.entrySet())
			{
				QueueItem newItem = new QueueItem();
				HashMap<String, String> fields = entry.getValue();
				
				
				newItem.title = fields.get("Filename");
				String downloaded = fields.get("Downloaded");
				String size = fields.get("Size");
				String status = fields.get("Status");
				newItem.status = downloaded + "; " + size + "; " + status;
				items.add(newItem);
				
			}
			
			adapter.notifyDataSetChanged();
		}
	}
	
	private static class QueueViewAdapter extends ViewHolderArrayAdapter<QueueItem, ItemViewHolder>
	{
		public QueueViewAdapter(Context context, ArrayList<QueueItem> resultList)
		{
			super(context, R.layout.result_item, resultList);
		}

		@Override
		public ItemViewHolder createViewHolder(View view)
		{
			return new ItemViewHolder(view);
		}

		@Override
		public void fillViewHolder(ItemViewHolder viewHolder, int position)
		{
			final QueueItem item = getArray().get(position);
			viewHolder.title.setText(item.title);
			viewHolder.details.setText(item.status);
			
		}
		
	}
	

}