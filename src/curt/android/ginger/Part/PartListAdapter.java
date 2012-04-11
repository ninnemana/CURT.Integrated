/**
 * 
 */
package curt.android.ginger.Part;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import curt.android.ginger.R;

/**
 * @author alexninneman
 *
 */
public class PartListAdapter extends ArrayAdapter<Part> {

	private ImageDownloader imgDownloader = new ImageDownloader();
	private Activity activity;
	private List<Part> parts;
	
	/*
	 * Override the constructor for ArrayAdapter
	 * @param activity the Activity that contains the ListView
	 * @param textViewResourceId We're not going to use this, we need to pass it to the super
	 * @param parts The List of Part objects to paint to our ListView
	 */
	@SuppressWarnings("unchecked")
	public PartListAdapter(Activity activity, int textViewResourceId, List<Part> parts){
		super(activity, textViewResourceId, R.layout.part_list_row);
		this.activity = activity;
		this.parts = parts;
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
		// then it already exists and we do not need to call the inflater function
		// this saves us a HUGE amount of resources and processing
		if(row == null){
			LayoutInflater inflater = activity.getLayoutInflater();
			row = inflater.inflate(R.layout.part_list_row, null);
		}
		
		Part part = parts.get(position);
		if(part != null){
			List<Images> part_images = part.getImages();
			String imgPath = null;
			for(Iterator<Images> i = part_images.iterator(); i.hasNext();){
				Images img = i.next();
				if(img.getWidth().intValue() > 250){
					imgPath = img.getPath();
					break;
				}
			}
			
			try{
				ImageView img = ((ImageView)row.findViewById(R.id.imageview));
				imgDownloader.download(imgPath.replaceAll("\\\\", "/"), img);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			Typeface tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/ITCAvantGardeStd_Bold.otf");
			((TextView)row.findViewById(R.id.textview)).setText(part.getShortDesc().trim());
			TextView tv = (TextView) row.findViewById(R.id.textview);
			tv.setText(part.getShortDesc().trim());
			tv.setTypeface(tf);
		}
		
		return row;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return parts.size();
	}
}
