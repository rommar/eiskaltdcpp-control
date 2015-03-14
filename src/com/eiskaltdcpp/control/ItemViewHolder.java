package com.eiskaltdcpp.control;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class ItemViewHolder
{
	public TextView title;
	public TextView details;
	public ImageView icon;
	public ImageView popup_icon;
	
	public ItemViewHolder(View view)
	{
		title =  (TextView)view.findViewById(R.id.resource_title);
		details = (TextView)view.findViewById(R.id.resource_details);
		icon = (ImageView)view.findViewById(R.id.resource_type_icon);
		popup_icon = (ImageView)view.findViewById(R.id.result_item_popup_icon);
	}
	
}