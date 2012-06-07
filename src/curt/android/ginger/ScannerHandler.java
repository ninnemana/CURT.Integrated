package curt.android.ginger;

import java.util.Collection;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import curt.android.camera.CameraManager;
import curt.android.scanner.DecodeThread;
import curt.android.scanner.ViewfinderResultPointCallback;
import curt.android.ginger.R.*;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */

public final class ScannerHandler extends Handler {
	private static final String TAG = ScannerHandler.class.getSimpleName();

	  private final Scanner activity;
	  private final DecodeThread decodeThread;
	  private State state;
	  private final CameraManager cameraManager;

	  private enum State {
	    PREVIEW,
	    SUCCESS,
	    DONE
	  }

	  ScannerHandler(Scanner activity,
	                         Collection<BarcodeFormat> decodeFormats,
	                         String characterSet,
	                         CameraManager cameraManager) {
	    this.activity = activity;
	    decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
	        new ViewfinderResultPointCallback(activity.getViewfinderView()));
	    decodeThread.start();
	    state = State.SUCCESS;

	    // Start ourselves capturing previews and decoding.
	    this.cameraManager = cameraManager;
	    cameraManager.startPreview();
	    restartPreviewAndDecode();
	  }

	  @Override
	  public void handleMessage(Message message) {

	    if (message.what == curt.android.ginger.R.id.auto_focus) {
			//Log.d(TAG, "Got auto-focus message");
	        // When one auto focus pass finishes, start another. This is the closest thing to
	        // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
	        if (state == State.PREVIEW) {
	          cameraManager.requestAutoFocus(this, curt.android.ginger.R.id.auto_focus);
	        }
		} else if (message.what == curt.android.ginger.R.id.restart_preview) {
			Log.d(TAG, "Got restart preview message");
			restartPreviewAndDecode();
		} else if (message.what == curt.android.ginger.R.id.decode_succeeded) {
			
			Log.d(TAG, "Got decode succeeded message" + message.arg1 + message.arg2 + message.toString());
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = bundle == null ? null :
	            (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
			activity.handleDecode((Result) message.obj, barcode);
		} else if (message.what == curt.android.ginger.R.id.decode_failed) {
			// We're decoding as fast as possible, so when one decode fails, start another.
	        state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), curt.android.ginger.R.id.decode);
		} else if (message.what == curt.android.ginger.R.id.return_scan_result) {
			Log.d(TAG, "Got return scan result message");
			activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.finish();
		} else if (message.what == curt.android.ginger.R.id.launch_product_query) {
			Log.d(TAG, "Got product query message");
			String url = (String) message.obj;
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			activity.startActivity(intent);
		}
	  }

	  public void quitSynchronously() {
	    state = State.DONE;
	    cameraManager.stopPreview();
	    Message quit = Message.obtain(decodeThread.getHandler(), curt.android.ginger.R.id.quit);
	    quit.sendToTarget();
	    try {
	      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
	      decodeThread.join(500L);
	    } catch (InterruptedException e) {
	      // continue
	    }

	    // Be absolutely sure we don't send any queued up messages
	    removeMessages(curt.android.ginger.R.id.decode_succeeded);
	    removeMessages(curt.android.ginger.R.id.decode_failed);
	  }

	  private void restartPreviewAndDecode() {
	    if (state == State.SUCCESS) {
	      state = State.PREVIEW;
	      cameraManager.requestPreviewFrame(decodeThread.getHandler(), curt.android.ginger.R.id.decode);
	      cameraManager.requestAutoFocus(this, curt.android.ginger.R.id.auto_focus);
	      activity.drawViewfinder();
	    }
	  }
}
