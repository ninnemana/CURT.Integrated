/**
 * 
 */
package curt.android.ginger;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import configr.Configurator;

/**
 * @author alexninneman
 *
 */
public class ConfigrMount extends ListActivity {

	// Aync objects
	private static Handler handler;
	private Thread thread;
	
	private Configurator config = new Configurator();
	private List<String> options;
	private ArrayAdapter<String> adapter;
	public String mount;
	public EditText filter;
	public ListView listView;
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle state){
		
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		handler = new Handler();
		
		// Reset our Configr settings
		config.clear();
		
		// Reset our options to any empty String array
		options = new ArrayList<String>();
		
		thread = (Thread) getLastNonConfigurationInstance();
		if(thread != null && thread.isAlive()){
			// TO DO
		}
		setConfiguration();
	}
	
	private void setConfiguration(){
		config.setState();
		thread = new YearThread();
		thread.start();
	}
	
	@Override
	public Object onRetainNonConfigurationInstance(){
		return thread;
	}
	
	private TextWatcher filterWatcher = new TextWatcher(){
		public void afterTextChanged(Editable s) { }
		public void beforeTextChanged(CharSequence s, int start, int count, int after){ }
		public void onTextChanged(CharSequence s, int start, int before, int count){
			adapter.getFilter().filter(s);
		}
	};
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(filter != null){
			filter.removeTextChangedListener(filterWatcher);
		}
	}
	
	public class YearRunner implements Runnable{
		public void run(){
			options = new ArrayList<String>();
			options = config.getOptions();
			
			setContentView(R.layout.lookup_list);
			
			filter = (EditText)findViewById(R.id.lookup_filter);
			filter.addTextChangedListener(filterWatcher);
			
			listView = getListView();
			listView.setTextFilterEnabled(true);
			
			adapter = new ArrayAdapter<String>(ConfigrMount.this, R.layout.lookup_list_row, R.id.lookup_option,options);
			setListAdapter(adapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
	
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					mount = (String) parent.getItemAtPosition(pos);
					
					Intent intent = null;
					Bundle iBundle = new Bundle();
					iBundle.putString("mount", mount);
					
					intent = new Intent(getApplicationContext(), ConfigrYear.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtras(iBundle);
					
					LocalActivityManager processManager = LookupGroup.group.getLocalActivityManager();
					Window w = processManager.startActivity("Year", intent);
					View newView = w.getDecorView();
					
					LookupGroup.group.replaceView(newView);
				}
				
			});
		}
	}
	
	public class YearThread extends Thread{
		public void run(){
			try{
				handler.post(new YearRunner());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
		
	// On future classes for the Configr implementation you will find a onBackPressed override here
	// However this our base level, so we will let the user exit.
}
