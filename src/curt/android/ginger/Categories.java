/**
 * 
 */
package curt.android.ginger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import curt.android.ginger.CategoryObject.Category;
import curt.android.ginger.CategoryObject.CategoryAdapter;
import curt.android.ginger.Part.Part;

/**
 * @author alexninneman
 *
 */
public class Categories extends ListActivity {
	
	final static String[] ITEMS = {"blah", "floop", "gnarlp", "stuff"};
	List<Category> cats;
	ArrayAdapter<String> adapter;
	String result = "";
	final String tag = "Your Logcat tag: ";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cat_list);
		
		// Instantiate our List<String> to hold the category titles
		// which will be used to paint out our ListView
		List<String> cat_titles = new ArrayList<String>();
		
		// Attempt to retrieve a category from Bundle
		Bundle bundle = this.getIntent().getExtras();
		Number catID = 0;
		if(bundle != null){
			catID = (int)bundle.getDouble("catID", 0);
		}
		
		cats = new ArrayList<Category>();
		if(catID.equals(0)){
			// Retrieve all the parent categories
			cats = new Category().GetParents();
		}else{
			Category cat = new Category();
			cat.setCatID(catID);
			cats = cat.GetSubcategories();
		}
		
		// Iterate through our List of Category objects to retrieve the title of the object
		for(Iterator<Category> i = cats.iterator(); i.hasNext();){
			Category cat = i.next();
			cat_titles.add(cat.getCatTitle().trim());
		}
		
		adapter = new ArrayAdapter<String>(this,R.layout.cat_list_row,R.id.textview, cat_titles);
		setListAdapter(adapter);
		
		ListView list = getListView();
		list.setTextFilterEnabled(true);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				String selected = (String) parent.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(), "Fetching " + selected, Toast.LENGTH_SHORT).show();
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
	}
}
