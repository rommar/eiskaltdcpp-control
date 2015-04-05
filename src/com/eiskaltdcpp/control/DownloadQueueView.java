package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.eiskaltdcpp.control.DownloadQueueDataModel.DownloadQueueModelListener;
import com.eiskaltdcpp.control.SearchDataModel.SearchResult;
import com.eiskaltdcpp.control.ServiceProxy.QueueRecords;

public class DownloadQueueView extends AbstractView<DownloadQueueDataModel, DownloadQueueModelListener>
{
	private QueueViewAdapter adapter;
	private ArrayList<QueueItem> items;
	
	private Listener listener = new Listener();
	private UserActionsListener actionsListener;
	
	public static interface UserActionsListener 
	{
		void removeQueueItem(QueueItem item);
	}
	
	public DownloadQueueView(Context context, UserActionsListener userActionsListener)
	{
		items = new ArrayList<QueueItem>();
		actionsListener = userActionsListener;
		adapter = new QueueViewAdapter(context, items, actionsListener);
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
		
	
	public static class QueueItem
	{
		public String target = "";
		public String title = "";
		public String status = "";
		
	}
	
	private class Listener implements DownloadQueueModelListener
	{
		@Override
		public void onUpdated(QueueRecords records)
		{
			items.clear();
			if (records.values != null)
			{
				for (HashMap.Entry<String, HashMap<String, String> > entry : records.values.entrySet())
				{
					QueueItem newItem = new QueueItem();
					HashMap<String, String> fields = entry.getValue();
					
					newItem.target = entry.getKey();
					
					newItem.title = fields.get("Filename");
					String downloaded = fields.get("Downloaded");
					String size = fields.get("Size");
					String status = fields.get("Status");
					newItem.status = downloaded + "; " + size + "; " + status;
					items.add(newItem);
					
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	private static class QueueViewAdapter extends StandardListViewAdapter<QueueItem>
	{
		private UserActionsListener actionsListener;
		
		public QueueViewAdapter(Context context, ArrayList<QueueItem> resultList, UserActionsListener actionsListener)
		{
			super(context, R.layout.result_item, resultList, R.menu.download_queue_popup_menu);
			this.actionsListener = actionsListener;
		}

		@Override
		protected String getItemTitle(QueueItem item)
		{
			return item.title;
		}

		@Override
		protected String getItemDetails(QueueItem item)
		{
			return item.status;
		}

		@Override
		protected boolean onMenuItemClicked(int resource, QueueItem item)
		{
			switch (resource)
			{
			case R.id.download_queue_action_remove:
				removeItemActivated(item);
			}
			return false;
		}

		@Override
		protected void configureDefaultAction(ImageView imageView)
		{
			String uri = "@drawable/ic_action_remove";
			int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
			Drawable res = getContext().getResources().getDrawable(imageResource);
			imageView.setImageDrawable(res);
		};
		
		@Override
		protected void onDefaultActionClicked(QueueItem item)
		{
			removeItemActivated(item);
		}; 
		
		private void removeItemActivated(QueueItem item)
		{
			if (actionsListener != null)
				actionsListener.removeQueueItem(item);
		}
		
	}
	

}