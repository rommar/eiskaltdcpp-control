package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.HashMap;

import com.eiskaltdcpp.control.DownloadQueueSingleton.DownloadQueueListenerInterface;
import com.eiskaltdcpp.control.SearchUi.SearchResultsDataModel;
import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;

public class DownloadQueueUi
{

	public static class QueueItem
	{
		public String title = "";
	}
	
	public static class QueueViewAdapter extends ViewHolderArrayAdapter<QueueItem>
	{

		public QueueViewAdapter(Context context, ArrayList<QueueItem> resultList)
		{
			super(context, R.layout.result_item, resultList);
		}

		@Override
		public Object createViewHolder(View view)
		{
			return new ItemViewHolder(view);
		}

		@Override
		public void fillViewHolder(Object viewHolderObject, int position)
		{

			ItemViewHolder holder = (ItemViewHolder)viewHolderObject;
			
			final QueueItem item = getArray().get(position);
			
			holder.title.setText(item.title);
			
		}
		
	}
	
	
	
	public static class QueueDataModel
	{
		private ArrayList<QueueItem> items = new ArrayList<QueueItem>();
		private QueueViewAdapter adapter;
		
		private static QueueDataModel instance;
		
		private QueueDataModel()
		{
			
		}
		
		public static QueueDataModel getInstance()
		{
			if (instance == null)
				instance = new QueueDataModel();
			return instance;
		}
		
		public void initialize(Context context)
		{
			adapter = new QueueViewAdapter(context, items);
			DownloadQueueListenerInterface listener = new DownloadQueueListenerInterface()
			{
				@Override
				public void onDownloadQueueUpdated(QueueRecords records)
				{
					items.clear();
					if (records.values == null)
						return;
					
					//Log.i("onDownloadQueueUpdated", "OK");
					
					for (HashMap.Entry<String, HashMap<String, String> > entry : records.values.entrySet())
					{
						QueueItem newItem = new QueueItem();
						newItem.title = entry.getKey();
						items.add(newItem);
					}
					
					adapter.notifyDataSetChanged();
					
					
				}
			};
			
			DownloadQueueSingleton.getInstance().setListener(listener);
		}
		
		public ListAdapter getListViewAdapter()
		{
			return adapter;
		}
		
	}
	
	
	
	
}
