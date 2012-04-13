package curt.android.ginger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import configr.Configurator;
import configr.Configurator.configStates;
import curt.android.gif.GIFView;
import curt.android.gif.GifWebView;

import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Lookup extends ListActivity {

	// Async objects
	private static ProgressDialog dialog;
	private static Handler handler;
	private Thread thread;
	
	private Configurator config = new Configurator();
	private Bitmap mBitmap;
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
		
		GifWebView webView = new GifWebView(this, "file:///android_asset/loader.gif");
		LinearLayout layout = (LinearLayout)findViewById(R.id.loading_layout);
		layout.addView(webView);
		
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
		
		thread = (Thread) getLastNonConfigurationInstance();
		if(thread != null && thread.isAlive()){
			// TO DO
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
			
			/*LinearLayout top_bar = new LinearLayout(getApplicationContext());
			top_bar.setBackgroundColor(Color.WHITE);
			
			TextView tv = new TextView(getApplicationContext());
			tv.setWidth(LayoutParams.FILL_PARENT);
			tv.set
			
			String tv_text = "";
			if(config.getMount() != null && config.getMount().length() > 0){
				if(config.getMount().toUpperCase().trim() == "REAR"){
					tv_text += " Rear Mount";
				}else{
					tv_text += " Front Mount";
				}
				if(config.getYear() != null && config.getYear().length() > 0){
					tv_text += " " + config.getYear();
					if(config.getMake() != null && config.getMake().length() > 0){
						tv_text += " " + config.getMake();
						if(config.getModel() != null && config.getModel().length() > 0){
							tv_text += " " + config.getModel();
							if(config.getStyle() != null && config.getStyle().length() > 0){
								tv_text += " " + config.getStyle();
							}
						}
					}
				}
			}
			if(tv_text.length() > 0){
				tv.setText(tv_text);
				top_bar.addView(tv);
			}
			
			filterText = new EditText(getApplicationContext());
			filterText.setHint("Type to filter");
			filterText.addTextChangedListener(filterTextWatcher);
			
			top_bar.addView(filterText);*/
			listView = getListView();
			//listView.addHeaderView(top_bar, null, true);
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
		}
	}
	
	@Override
	public void onBackPressed(){
		if(LookupGroup.history == null || LookupGroup.history.size() == 0){
			config.setMount(null);
			config.setYear(null);
			config.setMake(null);
			config.setModel(null);
			config.setStyle(null);
		}else if(LookupGroup.history.size() == 1){
			config.setMount(null);
			config.setYear(null);
			config.setMake(null);
			config.setModel(null);
			config.setStyle(null);
		}else if(LookupGroup.history.size() == 2){
			config.setYear(null);
			config.setMake(null);
			config.setModel(null);
			config.setStyle(null);
		}else if(LookupGroup.history.size() == 3){
			config.setMake(null);
			config.setModel(null);
			config.setStyle(null);
		}else if(LookupGroup.history.size() == 4){
			config.setModel(null);
			config.setStyle(null);
		}else if(LookupGroup.history.size() == 5){
			config.setStyle(null);
		}
		
		LookupGroup.group.back();
		return;
	}
}