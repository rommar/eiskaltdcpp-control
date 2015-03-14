package com.eiskaltdcpp.control;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.lang.Boolean;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import com.eiskaltdcpp.control.R;
import com.eiskaltdcpp.control.SearchUi;

public class MainActivity extends ActionBarActivity 
{
	public static class TabListener implements ActionBar.TabListener
	{
	    private final Fragment mFragment;

	    public TabListener(Fragment fragment) 
	    {
	        mFragment = fragment;
	    }

	    public void onTabSelected(Tab tab, FragmentTransaction ft)
	    {
	    	
            ft.attach(mFragment);
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft)
	    {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft)
	    {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	
	
	public static class DownloadQueueFragment extends Fragment
	{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			//TODO: Rename layouts which are common for download queue and search results
			View view = inflater.inflate(R.layout.search_results, container, false);
			final ListView listview = (ListView)view.findViewById(R.id.search_results_list_view);
		    listview.setAdapter(DownloadQueueUi.QueueDataModel.getInstance().getListViewAdapter());
		    return view;
		    
			
			
			
		}
		
	}
	
	public static class HubsListFragment extends Fragment
	{
		
	}
	
	public static class SearchResultsFragment extends Fragment
	{
	
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View view = inflater.inflate(R.layout.search_results, container, false);
			final ListView listview = (ListView)view.findViewById(R.id.search_results_list_view);
		    listview.setAdapter(SearchUi.SearchResultsDataModel.getInstance().getListViewAdapter());
		    return view;

		}
		

	}
	
	
	
	public MainActivity()
	{
		super();
		
		
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.main);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        
        SearchUi.SearchResultsDataModel.getInstance().initialize(this);
        DownloadQueueUi.QueueDataModel.getInstance().initialize(this);
	
        Tab tab = actionBar.newTab();
		tab.setText("Download Queue");
		DownloadQueueFragment queueFragment = new DownloadQueueFragment();
		tab.setTabListener(new TabListener(queueFragment));
		actionBar.addTab(tab);
        
        tab = actionBar.newTab();
		tab.setText("Hubs List");
		HubsListFragment hubsFragment = new HubsListFragment();
		tab.setTabListener(new TabListener(hubsFragment));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab();
		tab.setText("Search Results");
		final SearchResultsFragment searchResultFragment = new SearchResultsFragment();
		tab.setTabListener(new TabListener(searchResultFragment));
		actionBar.addTab(tab);
        
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.view_group, queueFragment);
		fragmentTransaction.add(R.id.view_group, hubsFragment);
		fragmentTransaction.add(R.id.view_group, searchResultFragment);
		fragmentTransaction.detach(searchResultFragment);
		
		
		fragmentTransaction.commit();
		
		
		//EXP:
		DownloadQueueSingleton.getInstance();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_search:
            	Intent intent = new Intent(this, SearchActivity.class);
            	startActivity(intent);
                return true;
            case R.id.action_stop_search:
            	Searcher.getInstance().stopSearch();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
   
}