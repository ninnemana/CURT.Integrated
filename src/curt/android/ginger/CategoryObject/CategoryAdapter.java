package curt.android.ginger.CategoryObject;

import java.util.List;

import curt.android.ginger.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter {
	private final Activity activity;
	private final List<Category> cats;
	
	// Initialize adapter
	@SuppressWarnings("unchecked")
	public CategoryAdapter(Activity activity, List<Category> cats){
		super(activity, R.layout.cat_list, cats);
		this.activity = activity;
		this.cats = cats;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View rowView = convertView;
		CategoryView catView = null;
		
		if(rowView == null){
			// Get a new instance of the row layout view
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.cat_list, null);
			
			// Hold the view objects in an object,
			// so they don't need to be refetched
			catView = new CategoryView();
			catView.txtCatTitle = (TextView) rowView.findViewById(R.id.txtCatTitle);
			
			// Cache the view objects in the tag,
			// so they can be re-accessed later
			rowView.setTag(catView);			
		}else{
			catView = (CategoryView) rowView.getTag();
		}
		// Transfer the Category data from the data object
		// to the view objects
		Category cat = cats.get(position);
		catView.txtCatTitle.setText(cat.getCatTitle());
		
		return rowView;
	}
	
	protected static class CategoryView{
		protected TextView txtCatTitle;
	}
	
}
