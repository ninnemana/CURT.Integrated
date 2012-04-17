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
public class ConfigrModel extends ListActivity {

	// Aync objects
	private static Handler handler;
	private Thread thread;
	
	private Configurator config = new Configurator();
	private List<String> options;
	private ArrayAdapter<String> adapter;
	public String mount, year, make, model;
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
			year = savedInstanceState.getString("year");
			make = savedInstanceState.getString("make");
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
		if(year == null || year.length() == 0){
			Bundle bundle = this.getIntent().getExtras();
			if (bundle != null) {
				try {
					year = bundle.getString("year");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(make == null || make.length() == 0){
			Bundle bundle = this.getIntent().getExtras();
			if (bundle != null) {
				try {
					make = bundle.getString("make");
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
		config.setYear(year);
		config.setMake(make);
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
			config.setYear(year);
			config.setMake(make);
			options = config.getOptions();
			
			setContentView(R.layout.lookup_list);
			TextView config_history = (TextView)findViewById(R.id.lookup_history);
			String history = config.getDisplayableMount() + " " + config.getYear() + " " + config.getMake();
			if (history != null && history.length() > 0) {
				LayoutParams params = config_history.getLayoutParams();
				params.height = LayoutParams.WRAP_CONTENT;
				config_history.setText(history);
			}
			
			filter = (EditText)findViewById(R.id.lookup_filter);
			filter.addTextChangedListener(filterWatcher);
			
			listView = getListView();
			listView.setTextFilterEnabled(true);
			
			adapter = new ArrayAdapter<String>(ConfigrModel.this, R.layout.lookup_list_row, R.id.lookup_option,options);
			setListAdapter(adapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
	
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					model = (String) parent.getItemAtPosition(pos);
					
					Intent intent = null;
					Bundle iBundle = new Bundle();
					iBundle.putString("mount", mount);
					iBundle.putString("year", year);
					iBundle.putString("make", make);
					iBundle.putString("model", model);
					
					intent = new Intent(getApplicationContext(), ConfigrStyle.class);
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

