package curt.android.ginger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import configr.Configurator;
import configr.Configurator.configStates;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import curt.android.ginger.CategoryObject.Category;
import curt.android.ginger.Part.*;

public class PartList extends ListActivity {

	// Async objects
	private static ProgressDialog progressDialog;
	private static Handler handler;
	private Thread downloadThread;
	
	// Application objects
	private List<Part> parts = null;
	private Number catID = null;
	private String mount, year, make, model, style = null;
	private PartListAdapter adapter;
	private Configurator config;
	private Activity partListContext;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		partListContext = this.getParent();
		this.catID = 0;
		
		// Create handler to update the UI
		handler = new Handler();
		
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			this.catID = bundle.getDouble("catID", 0);
			this.mount = bundle.getString("mount");
			this.year = bundle.getString("year");
			this.make = bundle.getString("make");
			this.model = bundle.getString("model");
			this.style = bundle.getString("style");
		}
		parts = new ArrayList<Part>();
		
		//adapter = new PartListAdapter(this, R.layout.part_list_row, parts);
		//setListAdapter(adapter);
		
		downloadThread = (Thread) getLastNonConfigurationInstance();
		if(downloadThread != null && downloadThread.isAlive()){
			//progressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving parts...");
		}
		loadParts();
	}
	
	// Save the thread
	@Override
	public Object onRetainNonConfigurationInstance(){
		return downloadThread;
	}
	
	public void loadParts(){
		//progressDialog = ProgressDialog.show(CURTIntialActivity.context, "Please Wait...", "Retrieving parts...");
		downloadThread = new MyThread();
		downloadThread.start();
	}
	
	// Dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy(){
		/*if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog = null;
		}*/
		super.onDestroy();
	}
	
	private List<Part> getCategoryParts(){
		parts = new ArrayList<Part>();
		Category cat = new Category();
		
		cat.setCatID(this.catID);
		parts = cat.GetCategoryParts();
		return parts;
	}
	
	/**
	 * @return the catID
	 */
	public Number getCatID() {
		return catID;
	}

	/**
	 * @param catID the catID to set
	 */
	public void setCatID(Number catID) {
		this.catID = catID;
	}

	public class MyThread extends Thread{
		@Override
		public void run(){
			handler.post(new MyRunnable());
		}
	}
	
	public class MyRunnable implements Runnable{
		public void run(){
			try{
				
				if(catID != null && catID.intValue() > 0){
					parts = getCategoryParts();
				}else{
					config = new Configurator(mount,year,make,model,style);
					if(config.state.equals(configStates.CONFIGURED)){
						parts = config.getParts();
					}else{
						parts = new ArrayList<Part>();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			setContentView(R.layout.part_list);
			if(parts != null && parts.size() > 0){
				adapter = new PartListAdapter(partListContext,R.layout.part_list_row, parts);
				setListAdapter(adapter);
			}
			
			//progressDialog.dismiss();
		}
	}
	
}
