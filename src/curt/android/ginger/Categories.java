/**
 * 
 */
package curt.android.ginger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.TextView;

import curt.android.ginger.CategoryObject.Category;
import curt.android.ginger.CategoryObject.CategoryAdapter;

/**
 * @author alexninneman
 *
 */
public class Categories extends ListActivity {
	
	// Async objects
	private static Handler handler;
	private Thread downloadThread;
	
	private List<Category> cats;
	CategoryAdapter adapter;
	String result = "";
	Number catID = 0;
	EditText filterText;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("catID", catID.intValue());
		super.onSaveInstanceState(outState);
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		handler = new Handler();
		
		// Attempt to retrieve a category from Bundle
		if(catID == null){
			catID = 0;
		}
		
		if(savedInstanceState != null){
			catID = (int)savedInstanceState.getDouble("catID");
		}else{
			Bundle bundle = this.getIntent().getExtras();
			if(bundle != null){
				catID = (int)bundle.getDouble("catID");
			}
		}
		if(catID == null){
			catID = 0;
		}
		
		downloadThread = (Thread) getLastNonConfigurationInstance();
		if(downloadThread != null && downloadThread.isAlive()){
			// TO-DO
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
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}
	
	public class MyThread extends Thread{
		@Override
		public void run(){
			try{
				handler.post(new CategoryRunnable());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void paintResults(){
		if(cats == null){
			cats = new ArrayList<Category>();
			if(catID == null || catID.equals(0)){
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

		setContentView(R.layout.cat_list);
		ListView list = getListView();
		
		if(list.getAdapter() == null){
			filterText = new EditText(getApplicationContext());
			filterText.setHint("Type to Filter");
			filterText.addTextChangedListener(filterTextWatcher);
			list.addHeaderView(filterText, null, true);
		}
		TextView tv = (TextView)findViewById(R.id.textview);
		if(tv != null){
			Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ITCAvantGardeStd_Bold.otf");
			tv.setTypeface(tf);
		}
		
		adapter = new CategoryAdapter(Categories.this, cats);
		setListAdapter(adapter);
	}
	
	public class CategoryRunnable implements Runnable{
		public void run(){
			paintResults();
			
			ListView list = getListView();
			list.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					Category cat = cats.get(position - 1);
					catID = cat.getCatID();
					setContentView(R.layout.loading);
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
			});
		}
	}
	
	@Override
	public void onBackPressed(){
		CategoryGroup.group.back();
		return;
	}
}