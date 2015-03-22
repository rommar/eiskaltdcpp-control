package com.eiskaltdcpp.control;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public abstract class ViewHolderArrayAdapter<ItemType, HolderType> extends ArrayAdapter<ItemType> 
{
	private Context context = null;
	int resource;
	ArrayList<ItemType> array;
	
	public ViewHolderArrayAdapter(Context context, int resource, ArrayList<ItemType> resultList)
	{
		super(context, resource, resultList);
		this.context = context;
		this.resource = resource;
		this.array = resultList;
	}

	public abstract HolderType createViewHolder(View view);
	public abstract void fillViewHolder(HolderType viewHolder, int position);
	
	@SuppressWarnings("unchecked")
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
		
		fillViewHolder((HolderType)convertView.getTag(), position);
		return convertView;
	}
	
	public Context getContext()
	{
		return context;
	}

	public ArrayList<ItemType> getArray()
	{
		return array;
	}
	
	
}