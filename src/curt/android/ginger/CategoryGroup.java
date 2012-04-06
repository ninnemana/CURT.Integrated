package curt.android.ginger;

import java.util.ArrayList;

import curt.android.ginger.CategoryObject.Category;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class CategoryGroup extends ActivityGroup {
	// Keep this in a static variable to make it accessible for all the nested activities,
	// lets them manipulate the view
	public static CategoryGroup group;
	
	// Need to keep track of the history if you want the back-button to work properly
	// don't use this if your activities require a lot of memory
	private ArrayList<View> history;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;
		
		// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity("Category", new Intent(this,Categories.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		
		// Replace the view of this ActivityGroup
		replaceView(view);
	}
	
	public void replaceView(View v){
		// Adds the old on to history
		history.add(v);
		// Changes this groups View to the new View
		setContentView(v);
	}
	
	public void back(){
		if(history.size() > 0){
			setContentView(history.get(history.size() - 1));
			history.remove(history.size() - 1);
		}else{
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && event.getRepeatCount() == 0){
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()){
			CategoryGroup.group.back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed(){
		CategoryGroup.group.back();
		return;
	}
}
