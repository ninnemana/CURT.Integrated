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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import configr.Configurator;

/**
 * @author alexninneman
 *
 */
public class ConfigrYear extends ListActivity {

	// Aync objects
	private static Handler handler;
	private Thread thread;
	
	private Configurator config = new Configurator();
	private List<String> options;
	private ArrayAdapter<String> adapter;
	public String mount;
	public String year;
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
		
		// Attempt to retrieve mount from the Bundle
		try{
			mount = savedInstanceState.getString("mount");
		}catch(Exception e){
			e.printStackTrace();
		}
		if(mount == null || mount.length() == 0){
			Bundle bundle = this.getIntent().getExtras();
			if (bundle != null) {
				try {
					mount = bundle.getString("mount");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		thread = (Thread) getLastNonConfigurationInstance();
		if(thread != null && thread.isAlive()){
			// TO DO
		}
		setConfiguration();
	}
	
	private void setConfiguration(){
		config.setMount(mount);
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
			config.setMount(mount);
			options = config.getOptions();
			
			setContentView(R.layout.lookup_list);
			
			TextView config_history = (TextView)findViewById(R.id.lookup_history);
			String history = config.getDisplayableMount();
			if (history != null && history.length() > 0) {
				LayoutParams params = config_history.getLayoutParams();
				params.height = LayoutParams.WRAP_CONTENT;
				config_history.setText(history);
			}
			
			
			filter = (EditText)findViewById(R.id.lookup_filter);
			filter.addTextChangedListener(filterWatcher);
			
			listView = getListView();
			listView.setTextFilterEnabled(true);
			
			adapter = new ArrayAdapter<String>(ConfigrYear.this, R.layout.lookup_list_row, R.id.lookup_option,options);
			setListAdapter(adapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
	
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					year = (String) parent.getItemAtPosition(pos);
					
					Intent intent = null;
					Bundle iBundle = new Bundle();
					iBundle.putString("mount", mount);
					iBundle.putString("year", year);
					
					intent = new Intent(getApplicationContext(), ConfigrMake.class);
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
		
	@Override
	public void onBackPressed() {
		LookupGroup.group.back();
		return;
	}
}

