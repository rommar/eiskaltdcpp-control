package com.eiskaltdcpp.control;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public abstract class StandardListViewAdapter<ItemType> extends ViewHolderArrayAdapter<ItemType, ItemViewHolder>
{

	private int popupMenuResource;
	
	public StandardListViewAdapter(Context context, int resource, ArrayList<ItemType> resultList, int popupMenuResource)
	{
		super(context, resource, resultList);
		this.popupMenuResource = popupMenuResource; 
	}

	@Override
	protected ItemViewHolder createViewHolder(View view)
	{
		return new ItemViewHolder(view);
	}
	
	protected abstract String getItemTitle(ItemType item);
	protected abstract String getItemDetails(ItemType item);
	protected abstract boolean onMenuItemClicked(int resource, ItemType item);
	
	protected void configureDefaultAction(ImageView imageView) {};
	protected void onDefaultActionClicked(ItemType item) {};
	
	@Override
	protected void fillViewHolder(ItemViewHolder holder, int position)
	{
		final ItemType item = getArray().get(position);
		
		holder.title.setText(getItemTitle(item));
		holder.details.setText(getItemDetails(item));
		
		holder.default_action_icon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.animator.image_view_click));
				onDefaultActionClicked(item);
			}
		});
		
		//TODO: Can be optimized: don't create listener each time
		holder.popup_icon.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				showPopupMenu(v, item);
				
			}
		});
		
		
		configureDefaultAction(holder.default_action_icon);
		
	}
	
	private void showPopupMenu(View view, final ItemType item)
	{
		PopupMenu popup = new PopupMenu(getContext(), view);
		
		MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(popupMenuResource, popup.getMenu());
	    
	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem menuItem)
			{
				int id = menuItem.getItemId();
				return onMenuItemClicked(id, item);
			}
		});

	    
	    popup.show();

	}

	
}