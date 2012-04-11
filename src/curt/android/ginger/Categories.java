/**
 * 
 */
package curt.android.ginger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import curt.android.ginger.CategoryObject.Category;
import curt.android.ginger.CategoryObject.CategoryAdapter;
import curt.android.ginger.Part.Part;
import curt.android.ginger.Part.PartListAdapter;
import curt.android.ginger.PartList.MyRunnable;
import curt.android.ginger.PartList.MyThread;

/**
 * @author alexninneman
 *
 */
public class Categories extends ListActivity {
	
	// Async objects
	private static ProgressDialog progressDialog;
	private static Handler handler;
	private Thread downloadThread;
	
	List<Category> cats;
	List<String> cat_list;
	ArrayAdapter<String> adapter;
	String result = "";
	Number catID = 0;
	EditText filterText;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String jString = gson.toJson(cats);
		outState.putString("categories_json", jString);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String jString = state.getString("categories_json");
		cats = gson.fromJson(jString, new TypeToken<List<Part>>(){}.getType());
		super.onRestoreInstanceState(state);
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cat_list);
		handler = new Handler();
		
		filterText = new EditText(this);
		filterText.setHint("Type to Filter");
		ListView list = getListView();
		list.addHeaderView(filterText, null, true);
		filterText.addTextChangedListener(filterTextWatcher);
		
		// Attempt to retrieve a category from Bundle
		Bundle bundle = this.getIntent().getExtras();
		catID = 0;
		if(bundle != null){
			catID = (int)bundle.getDouble("catID", 0);
		}
		
		cat_list = new ArrayList<String>();
		
		adapter = new ArrayAdapter<String>(this, R.layout.cat_list_row, R.id.textview, cat_list);
		setListAdapter(adapter);
		
		downloadThread = (Thread) getLastNonConfigurationInstance();
		if(downloadThread != null && downloadThread.isAlive()){
			progressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving categories...");
		}
		loadCats();
		
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher(){
		public void afterTextChanged(Editable s){ }
		public void beforeTextChanged(CharSequence s, int start, int count, int after){ }
		public void onTextChanged(CharSequence s, int start, int before, int count){
			adapter.getFilter().filter(s);
		}
	};
	
	private void loadCats(){
		progressDialog = ProgressDialog.show(CURTIntialActivity.context, "Please Wait...", "Retrieving categories...");
		downloadThread = new MyThread();
		downloadThread.start();
	}
	
	// Save the thread
	@Override
	public Object onRetainNonConfigurationInstance(){
		return downloadThread;
	}
	
	// Dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}
	
	public class MyThread extends Thread{
		@Override
		public void run(){
			try{
				// Simulate a slow network
				try{
					new Thread().sleep(5000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				handler.post(new CategoryRunnable());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public class CategoryRunnable implements Runnable{
		public void run(){
			if(cats == null){
				cats = new ArrayList<Category>();
				if(catID.equals(0)){
					// Retrieve all the parent categories
					cats.clear();
					cats = new Category().GetParents();
				}else{
					Category cat = new Category();
					cat.setCatID(catID);
					cats.clear();
					cats = cat.GetSubcategories();
				}
			}
			cat_list = new ArrayList<String>();
			
			// Iterate through our List of Category objects to retrieve the title of the object
			for(Iterator<Category> i = cats.iterator(); i.hasNext();){
				Category cat = i.next();
				cat_list.add(cat.getCatTitle().trim());
			}
			
			adapter = new ArrayAdapter<String>(Categories.this,R.layout.cat_list_row,R.id.textview, cat_list);
			setListAdapter(adapter);
			
			ListView list = getListView();
			list.setTextFilterEnabled(true);
			
			list.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					String selected = (String) parent.getItemAtPosition(position);
					for(Iterator<Category> i = cats.iterator(); i.hasNext();){
						Category cat = i.next();
						if(selected.equalsIgnoreCase(cat.getCatTitle().trim())){
							List<Category> subs = cat.GetSubcategories();
							
							// Create our Bundle and our Intent
							Intent intent;
							Bundle intentBundle = new Bundle();
							
							// Throw out catID into the Bundle
							intentBundle.putDouble("catID", cat.getCatID().doubleValue());
							
							if(subs != null && subs.size() > 0){

								intent = new Intent(getApplicationContext(), Categories.class);
							}else{ // Load category products
								
								intent = new Intent(getApplicationContext(), PartList.class);
								
							}
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtras(intentBundle);
							
							// Create the view using 
							LocalActivityManager processManager = CategoryGroup.group.getLocalActivityManager();
							Window w = processManager.startActivity(cat.getCatTitle().trim(), intent);
							View newView = w.getDecorView();
							
							CategoryGroup.group.replaceView(newView);
							
						}
					}
				}
			});
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		CategoryGroup.group.back();
		return;
	}
}
