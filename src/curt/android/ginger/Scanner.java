package curt.android.ginger;

//import com.google.zxing.client.android.ViewfinderView;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import curt.android.scanner.CameraManager;
import curt.android.scanner.ViewfinderView;

import com.google.zxing.client.*;

public class Scanner extends ListActivity {
	
	
	private ViewfinderView viewfinderView;
	private CameraManager cameraManager;
	private Handler handler;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager(){
		return cameraManager;
	}
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.loading);

	    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}
}