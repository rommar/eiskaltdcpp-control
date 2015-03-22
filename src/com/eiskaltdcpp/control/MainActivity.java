package com.eiskaltdcpp.control;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import com.eiskaltdcpp.control.R;

public class MainActivity extends ActionBarActivity 
{
	
	private static class Views
	{
		public static Views getInstance()
		{
			if (instance == null)
				instance = new Views();
			return instance;
		}
		
		private SearchResultsView searchResultsView;
		private DownloadQueueView downloadQueueView;
		
		
		public void initSearchResultsView(Context context)
		{
			searchResultsView = Core.getInstance().createSearchResultsView(context);
		}
		
		public SearchResultsView getSearchResultsView()
		{
			return searchResultsView;
		}
		
		public void initDownloadQueueView(Context context)
		{
			downloadQueueView = Core.getInstance().createDownloadQueueView(context);
		}
		
		public DownloadQueueView getDownloadQueueView()
		{
			return downloadQueueView;
		}
		
		private Views() { }
		private static Views instance = null;
		
	}
	
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
			
		    listview.setAdapter(Views.getInstance().getDownloadQueueView().getListViewAdapter());
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
			listview.setAdapter(Views.getInstance().getSearchResultsView().getListViewAdapter());
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
        
        Views.getInstance().initDownloadQueueView(this);
        Views.getInstance().initSearchResultsView(this);
		
		Views.getInstance().downloadQueueView = Core.getInstance().createDownloadQueueView(this);
		Views.getInstance().searchResultsView = Core.getInstance().createSearchResultsView(this);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        
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
            	Core.getInstance().getSearchController().stopSearch();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
   
}