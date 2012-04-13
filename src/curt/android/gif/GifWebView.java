/**
 * 
 */
package curt.android.gif;

import android.content.Context;
import android.webkit.WebView;

/**
 * @author alexninneman
 *
 */
public class GifWebView extends WebView {
	
	public GifWebView(Context context, String path){
		super(context);
		loadUrl(path);
	}
}
