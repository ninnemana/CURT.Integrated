/**
 * 
 */
package curt.android.ginger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author alexninneman
 *
 */

public class JSONHandler {
	
	//private static JSONParser parser = new JSONParser();
	
 	public static JSONObject getJSONObjectFromURL(String url){
		return getJSONObjectFromURL(url, false);
	}

	public static JSONObject getJSONObjectFromURL(String url, boolean doPost){
		
		// initialize
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		
		try{

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			if(doPost){ // make post request
				HttpPost post = new HttpPost(url);
				response = client.execute(post);
			}else{ // make get request
				HttpGet get = new HttpGet(url);
				response = client.execute(get);
			}
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		
		// convert response to string
		try {
			InputStreamReader inReader = new InputStreamReader(is, "iso-8859-1");
			BufferedReader reader = new BufferedReader(inReader, 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		
		// try parse the string to a json object
		jArray = getJSONObjectFromString(result);
		return jArray;
	}
	
	public static String getJSONListFromURL(String url){
		return getJSONListFromURL(url, false);
	}

	public static String getJSONListFromURL(String url, boolean doPost){
		
		// initialize
		InputStream is = null;
		String result = "";
		try{

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			if(doPost){ // make post request
				HttpPost post = new HttpPost("http://api.curtmfg.com/v2/" + url);
				response = client.execute(post);
			}else{ // make get request
				HttpGet get = new HttpGet("http://api.curtmfg.com/v2/" + url);
				response = client.execute(get);
			}
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		
		// convert response to string
		try {
			InputStreamReader inReader = new InputStreamReader(is, "iso-8859-1");
			BufferedReader reader = new BufferedReader(inReader, 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		
		return result;
		
		// try parse the string to a json object
		//jArray = getJSONListFromString(result);
		//return jArray;
	}
	
	
	public static JSONObject getJSONObjectFromString(String jString){
		JSONObject jObject = new JSONObject();
		try{
			jObject = new JSONObject(jString);
		}catch(Exception e){
			Log.e("log_tag", "Error converting string to JSONObject " + e.toString());
		}
		return jObject;
	}
	
	public static JSONArray getJSONListFromString(String jString){
		JSONArray jObject = new JSONArray();
		try{
			//jObject = Parser.class.
		}catch(Exception e){
			Log.e("log_tag", "Error converting string to JSONObject " + e.toString());
		}
		return jObject;
	}
	
}
