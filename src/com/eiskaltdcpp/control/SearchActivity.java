package com.eiskaltdcpp.control;

import java.util.ArrayList;
import java.util.EnumMap;

import com.eiskaltdcpp.control.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SearchActivity extends ActionBarActivity
{
	private static class SearchConstants
	{
		public enum SizeMode
		{
			DONT_CARE,
			AT_LEAST,
			AT_MOST;
			
		}
		
		public enum SizeUnits
		{
			BYTES,
			KBYTES,
			MBYTES,
			GBYTES
		}
		
		public enum Type
		{
			ANY (0),
			AUDIO (1),
			COMPRESSED (2),
			DOCUMENT (3),
			EXECUTABLE (4),
			PICTURE (5),
			VIDEO (6),
			DIRECTORY (7),
			TTH (8),
			CD_IMAGE (9);
			
			private final int id;
			Type(int id) { this.id = id; }
		    public int getValue() { return id; }
		}
		
		
		private static SearchConstants instance;
		
		public static SearchConstants getInstance()
		{
			if (instance == null)
			{
				instance = new SearchConstants();
			}
			return instance;
		}
				
		
		private EnumMap<SizeMode, String> sizeModesMap;
		
		private SearchConstants()
		{
			
		}
		
		public void initialize(Context context)
		{
			sizeModesMap = new EnumMap<SizeMode, String>(SizeMode.class);
			sizeModesMap.put(SizeMode.DONT_CARE, context.getResources().getString(R.string.search_mode_dont_care));
			sizeModesMap.put(SizeMode.AT_LEAST, context.getResources().getString(R.string.search_mode_at_least));
			sizeModesMap.put(SizeMode.AT_MOST, context.getResources().getString(R.string.search_mode_at_most));
		}
		
		public ArrayList<String> getSizeModes()
		{
			ArrayList<String> result = new ArrayList<String>(sizeModesMap.values());
			return result;
		}
		
		SizeMode getSizeModeByIndex(int index)
		{
			if (index >= sizeModesMap.size())
				return SizeMode.DONT_CARE;
			return (SizeMode) sizeModesMap.keySet().toArray()[index];
		}

	}
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        SearchConstants.getInstance().initialize(this);
        
        Spinner sizeModeSpinner = (Spinner) findViewById(R.id.spinner_search_size_mode);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SearchConstants.getInstance().getSizeModes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeModeSpinner.setAdapter(adapter);
	}
	
	public void onSearchButtonClick(View view)
	{
		EditText edit = (EditText) findViewById(R.id.edit_text_search_string);
    	String searchString = edit.getText().toString();
    	
        EditText editTextSize = (EditText) findViewById(R.id.edit_text_search_size_value);
        String sizeValueStr = editTextSize.getText().toString();
        double sizeValue = 0.0;
        if (sizeValueStr.length() != 0)
        {
        	try
        	{
        		sizeValue = Double.parseDouble(sizeValueStr);
        	}
        	catch (NumberFormatException e)
        	{
        		
        	}
        }
    	
    	Spinner sizeModeSpinner = (Spinner) findViewById(R.id.spinner_search_size_mode);
        int index = sizeModeSpinner.getSelectedItemPosition();
        SearchConstants.SizeMode sizeMode = SearchConstants.getInstance().getSizeModeByIndex(index);
    	
    	Core.getInstance().getSearchController().sendSearch(searchString, SearchConstants.Type.ANY.ordinal(), 
    			sizeMode.ordinal(), SearchConstants.SizeUnits.MBYTES.ordinal(), sizeValue, 
    			"", this);
    }
}
