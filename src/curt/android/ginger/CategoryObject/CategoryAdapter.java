package curt.android.ginger.CategoryObject;

import java.util.List;

import curt.android.ginger.R;
import curt.android.ginger.Part.*;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter {
	private final Activity activity;
	private final List<Category> cats;
	private ImageDownloader imgDownloader = new ImageDownloader();
	
	/*
	 * Override the constructor for ArrayAdapter
	 * @param activity	the Activity that contains the ListView
	 * @param cats		List of Category objects to paint into the adapter
	 */
	@SuppressWarnings("unchecked")
	public CategoryAdapter(Activity activity, List<Category> cats){
		super(activity, R.layout.cat_list, cats);
		this.activity = activity;
		this.cats = cats;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		
		// we call an if statement on our view that is passed in,
		// to see if it has been recycled or not. if it has been recycled
		// this it already exists and we do not need to call the inflater function
		// this saves us a HUGE amount of resources and processing
		if(row == null){
			LayoutInflater inflater = activity.getLayoutInflater();
			row = inflater.inflate(R.layout.cat_list_row, null);
		}
		
		Category cat = cats.get(position);
		if(cat != null){
			CategoryView catView = new CategoryView();
			try{
				String img = cat.getImage();
				catView.imgView = (ImageView)row.findViewById(R.id.cat_image);
				imgDownloader.download(img.replaceAll("\\\\", "/"), catView.imgView);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			catView.catTitle = (TextView)row.findViewById(R.id.cat_title);
			catView.catTitle.setText(cat.getCatTitle());
			
			catView.catDesc = (TextView)row.findViewById(R.id.cat_desc);
			catView.catDesc.setText(cat.getShortDesc());
			row.setTag(catView);
		}
		return row;
	}
	
	protected static class CategoryView{
		ImageView imgView;
		TextView catTitle;
		TextView catDesc;
	}
	
}
