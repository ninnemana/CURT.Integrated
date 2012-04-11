package curt.android.ginger;

import java.util.HashMap;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class CURTIntialActivity extends TabActivity {
	
	TabHost mTabHost;
	public static TabActivity context;
	
	private void setupTabHost(){
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        
        setupTabHost();
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        
        //mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
        /*Intent intent = new Intent().setClass(this,CategoryGroup.class);
        TabHost.TabSpec spec;
        spec = mTabHost.newTabSpec("CategoryGroup").setIndicator("CategoryGroup").setContent(intent);
        mTabHost.addTab(spec);*/
        
        setupTab(new TextView(this), "categories", new Intent().setClass(this, CategoryGroup.class));
        setupTab(new TextView(this), "lookup", new Intent().setClass(this, LookupGroup.class));
        setupTab(new TextView(this), "scanner");
        
        if(savedInstanceState != null){
        	mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }else{
        	mTabHost.setCurrentTab(0);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
    private void setupTab(final View view, final String tag){
    	setupTab(view,tag,null);
    }
    
    private void setupTab(final View view, final String tag, Intent intent){
    	View tabView = createTabView(mTabHost.getContext(),tag);
    	
    	TabSpec setContent;
    	if(intent == null){
	    	setContent = mTabHost.newTabSpec(tag).setIndicator(tabView).setContent(new TabContentFactory(){
	    		public View createTabContent(String tag) { return view; }
	    	});
    	}else{
    		setContent = mTabHost.newTabSpec(tag).setIndicator(tabView).setContent(intent);
    	}
    	mTabHost.addTab(setContent);
    }
    
    private static View createTabView(final Context context, final String text){
    	View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg,null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    }
    
}

