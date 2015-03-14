package com.eiskaltdcpp.control;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public abstract class ViewHolderArrayAdapter<T> extends ArrayAdapter<T> 
{
	private Context context = null;
	int resource;
	
	public ViewHolderArrayAdapter(Context context, int resource, ArrayList<T> resultList)
	{
		super(context, resource, resultList);
		
		this.context = context;
		this.resource = resource;
	}

	public abstract Object createViewHolder(View view);
	public abstract void fillViewHolder(Object viewHolder, int position);
	
	@Override
	public final View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource, parent, false);
			
			Object holder = createViewHolder(convertView); 
			convertView.setTag(holder);
		}
		
		fillViewHolder(convertView.getTag(), position);
		return convertView;
	}
	
	public Context getContext()
	{
		return context;
	}

	
	
}