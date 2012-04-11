package curt.android.ginger;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import configr.Configurator;
import configr.Configurator.configStates;

import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
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

public class Lookup extends ListActivity {

	// Async objects
	private static ProgressDialog dialog;
	private static Handler handler;
	private Thread thread;
	
	private Configurator config = new Configurator();
	List<String> options;
	ArrayAdapter<String> adapter;
	String mount, year, make, model, style;
	EditText filterText;
	ListView listView;
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		Gson gson = new Gson();
		String json = gson.toJson(options);
		outState.putString("options", json);
		outState.putString("mount",mount);
		outState.putString("year", year);
		outState.putString("make",make);
		outState.putString("model", model);
		outState.putString("style",style);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle state){
		Gson gson = new Gson();
		String json = state.getString("options");
		mount = state.getString("mount");
		year = state.getString("year");
		make = state.getString("make");
		model = state.getString("model");
		style = state.getString("style");
		options = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
		super.onRestoreInstanceState(state);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		handler = new Handler();
		
		
		
		// Attempt to retrieve year, make, model, and style from the Bundle
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			try{
				mount = bundle.getString("mount");
				year = bundle.getString("year");
				make = bundle.getString("make");
				model = bundle.getString("model");
				style = bundle.getString("style");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		// Reset our options to an empty String array
		options = new ArrayList<String>();
		
		// Bind our adapter to the ListView
		//adapter = new ArrayAdapter<String>(this, R.layout.lookup_list_row,R.id.lookup_option, options);
		//setListAdapter(adapter);
		
		thread = (Thread) getLastNonConfigurationInstance();
		if(thread != null && thread.isAlive()){
			//dialog = ProgressDialog.show(this, "Please wait...", "Retrieving options...");
		}
		setConfiguration();
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher(){

		public void afterTextChanged(Editable s) { }
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) { }

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
			
		}
	};
	
	private void setConfiguration(){
		config.setMount(mount);
		config.setYear(year);
		config.setMake(make);
		config.setModel(model);
		config.setStyle(style);
		//dialog = ProgressDialog.show(CURTIntialActivity.context, "Please wait...", "Loading options...");
		thread = new LookupThread();
		thread.start();
	}
	
	// Save the thread
	@Override
	public Object onRetainNonConfigurationInstance(){
		return thread;
	}
	
	// Dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
			dialog = null;
		}
		super.onDestroy();
		if(filterText != null){
			filterText.removeTextChangedListener(filterTextWatcher);
		}
	}
	
	public class LookupThread extends Thread{
		@Override
		public void run(){
			try{
				handler.post(new LookupRunner());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public class LookupRunner implements Runnable{
		public void run(){
			options = new ArrayList<String>();
			
			options = config.getOptions();
			
			setContentView(R.layout.lookup_list);
			
			filterText = new EditText(getApplicationContext());
			filterText.setHint("Type to filter");
			filterText.addTextChangedListener(filterTextWatcher);
			
			listView = getListView();
			listView.addHeaderView(filterText, null, true);
			listView.setTextFilterEnabled(true);
			
			adapter = new ArrayAdapter<String>(Lookup.this, R.layout.lookup_list_row, R.id.lookup_option, options);
			setListAdapter(adapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String selected = (String) parent.getItemAtPosition(position);
					config.setState();
					if(config.state.equals(configStates.MOUNT)){
						config.setMount(selected);
					}else if(config.state.equals(configStates.YEAR)){
						config.setYear(selected);
					}else if(config.state.equals(configStates.MAKE)){
						config.setMake(selected);
					}else if(config.state.equals(configStates.MODEL)){
						config.setModel(selected);
					}else if(config.state.equals(configStates.STYLE)){
						config.setStyle(selected);
					}
					config.setState();
					
					Intent intent;
					Bundle intentBundle = new Bundle();
					intentBundle.putString("mount", config.getMount());
					intentBundle.putString("year", config.getYear());
					intentBundle.putString("make", config.getMake());
					intentBundle.putString("model", config.getModel());
					intentBundle.putString("style", config.getStyle());
					
					if(config.state.equals(configStates.CONFIGURED)){
						intent = new Intent(getApplicationContext(), PartList.class);
					}else{
						intent = new Intent(getApplicationContext(), Lookup.class);
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtras(intentBundle);
					
					LocalActivityManager processManager = LookupGroup.group.getLocalActivityManager();
					Window w = processManager.startActivity("Hitch Lookup", intent);
					View newView = w.getDecorView();
					
					LookupGroup.group.replaceView(newView);
				}
			});
			//dialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		LookupGroup.group.back();
		return;
	}
}