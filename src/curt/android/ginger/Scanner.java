package curt.android.ginger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Scanner extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText("This is the scanner tab");
		setContentView(tv);
	}
}
