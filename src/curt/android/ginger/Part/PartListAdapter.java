/**
 * 
 */
package curt.android.ginger.Part;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import curt.android.ginger.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author alexninneman
 *
 */
public class PartListAdapter extends ArrayAdapter<Part> {

	Activity activity;
	List<Part> parts;
	
	@SuppressWarnings("unchecked")
	public PartListAdapter(Activity activity, int textViewResourceId, List<Part> parts){
		super(activity, textViewResourceId, R.layout.part_list_row);
		this.activity = activity;
		this.parts = parts;
	}
	
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
				if(img.getWidth().intValue() > 150){
					imgPath = img.getPath();
					break;
				}
			}
			
			try{
				ImageView img = ((ImageView)row.findViewById(R.id.imageview));
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imgPath).getContent());
				img.setImageBitmap(bitmap);
			}catch(MalformedURLException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}

			((TextView)row.findViewById(R.id.textview)).setText(part.getShortDesc().trim());
		}
		
		return row;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return parts.size();
	}
}
