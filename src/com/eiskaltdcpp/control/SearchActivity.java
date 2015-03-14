package com.eiskaltdcpp.control;

import com.eiskaltdcpp.control.R;
import com.eiskaltdcpp.control.ServiceProxy.SearchResults;


import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import android.view.View;
import android.widget.EditText;

public class SearchActivity extends ActionBarActivity
{
	
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        EditText edit = (EditText) findViewById(R.id.editTextSearchString);
        edit.setText("time");
        
	}
	
	public void onSearchButtonClick(View view)
	{
	
		EditText edit = (EditText) findViewById(R.id.editTextSearchString);
		
    	String searchString = edit.getText().toString();
    	
    	Searcher.getInstance().sendSearch(searchString, this);
    	
    	
    	//finish();
    }
}
