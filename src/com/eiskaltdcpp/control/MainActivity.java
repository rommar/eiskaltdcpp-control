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
	
	
	/*
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }


	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	        	
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            //ft.add(android.R.id.content, mFragment, mTag);
	            ft.add(R.id.view_group, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	*/
	
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
        
        /*
		Tab tab = actionBar.newTab();
		tab.setText("Hubs List");
		tab.setTabListener(new TabListener<HubsListFragment>(this, "hubs", HubsListFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab();
		tab.setText("Search Results");
		tab.setTabListener(new TabListener<SearchResultsFragment>(this, "search_results", SearchResultsFragment.class));
		actionBar.addTab(tab);
		*/
		
        Tab tab = actionBar.newTab();
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
		fragmentTransaction.add(R.id.view_group, hubsFragment);
		fragmentTransaction.add(R.id.view_group, searchResultFragment);
		fragmentTransaction.detach(searchResultFragment);
		
		
		fragmentTransaction.commit();
		
		
        
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
   
}