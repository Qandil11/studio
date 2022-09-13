package de.stamm.stammvision.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import android.util.Log;

public class WebserviceConnector {

	String serverUrl = "";
	String port = "443";
	MainModel mainModel;
	
	private static final int TIME_OUT_MILL = 10000;
	
	public WebserviceConnector(MainModel mainModel, String serverUrl, String port) {
		this.mainModel = mainModel;
		this.serverUrl = serverUrl;
		this.port = port;
	}
		
	public JSONArray getResult(Object[][] data) {
		Log.d("clip", "time" + Calendar.getInstance().getTime());
		String postData = "";		
		try {
		    // Construct data
			postData += "device_id=" + URLEncoder.encode(mainModel.getSettings().getDevice_id(), "UTF-8");
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					postData += "&"+URLEncoder.encode(String.valueOf(data[i][0]), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(data[i][1]), "UTF-8");
				}
			}
			
		    // Send data
		    URL url = new URL(serverUrl);
		    
		    Log.i("clip", serverUrl + " - " + postData);
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("connection", "close");
	        conn.setConnectTimeout(TIME_OUT_MILL);  
	        conn.setReadTimeout(TIME_OUT_MILL);  
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(postData);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
		    String line = "";
		    String webResult = "";
		    while ((line = rd.readLine()) != null) {
		    	webResult += line;
		    }
		    wr.close();
		    rd.close();
		    
		    if (webResult.equals("")) return null;
		    
		    JSONParser parser=new JSONParser();
		    Object obj=parser.parse(webResult);
		    conn.disconnect();
		    JSONArray array = (JSONArray) obj;
		    Log.d("clip", "response" + array.toJSONString());
		    return array;
		} catch (Exception e) {
			Log.e("getResult", "Unerwarteter Fehler");
			//mainModel.log(MainModel.LOGLEVEL_ERROR, "Unerwarteter Fehler in Webservice getResult: " + postData + " - ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public int insert(Object[][] data) {
		String postData = "";
		try {
		    // Construct data
			postData += "device_id=" + URLEncoder.encode(mainModel.getSettings().getDevice_id(), "UTF-8");
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					postData += "&"+URLEncoder.encode(String.valueOf(data[i][0]), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(data[i][1]), "UTF-8");
				}
			}

		    // Send data
		    URL url = new URL(serverUrl);

		    //Log.i("insert", serverUrl + " - " + postData);
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("connection", "close");
	        conn.setConnectTimeout(TIME_OUT_MILL);  
	        conn.setReadTimeout(TIME_OUT_MILL);  
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(postData);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
		    String line = "";
		    String webResult = "";
		    while ((line = rd.readLine()) != null) {
		    	webResult += line;
		    }
		    wr.close();
		    rd.close();
		    conn.disconnect();
		    return Integer.valueOf(webResult);
		} catch (Exception e) {
			Log.e("insert", "Unerwarteter Fehler");
			//mainModel.log(MainModel.LOGLEVEL_ERROR, "Unerwarteter Fehler in Webservice insert: " + postData + " - ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}
	
	public void update(Object[][] data) {
		String postData = "";
		try {
		    // Construct data
			postData += "device_id=" + URLEncoder.encode(mainModel.getSettings().getDevice_id(), "UTF-8");
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					postData += "&"+URLEncoder.encode(String.valueOf(data[i][0]), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(data[i][1]), "UTF-8");
				}
			}

		    // Send data
		    URL url = new URL(serverUrl);
		    
		    //Log.i("update", serverUrl + " - " + postData);
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("connection", "close");
	        conn.setConnectTimeout(TIME_OUT_MILL);  
	        conn.setReadTimeout(TIME_OUT_MILL);  
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(postData);
		    wr.flush();
		    
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
		    wr.close();
		    rd.close();
		    conn.disconnect();
		} catch (Exception e) {
			Log.e("update", "Unerwarteter Fehler");
			//mainModel.log(MainModel.LOGLEVEL_ERROR, "Unerwarteter Fehler in Webservice update: " + postData + " - ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public JSONArray serverRequest(Object[][] data) {
		String postData = "";
		try {
		    // Construct data
			postData += "device_id=" + URLEncoder.encode(mainModel.getSettings().getDevice_id(), "UTF-8");
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					postData += "&"+URLEncoder.encode(String.valueOf(data[i][0]), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(data[i][1]), "UTF-8");
				}
			}

		    // Send data
		    URL url = new URL(serverUrl);
		    
		    //Log.i("serverRequest", serverUrl + " - " + postData);
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("connection", "close");
	        conn.setConnectTimeout(TIME_OUT_MILL);  
	        conn.setReadTimeout(TIME_OUT_MILL);  
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(postData);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
		    String line = "";
		    String webResult = "";
		    while ((line = rd.readLine()) != null) {
		    	webResult += line;
		    }
		    wr.close();
		    rd.close();
		    
		    if (webResult.equals("")) return null;
		    
		    JSONParser parser=new JSONParser();
		    Object obj=parser.parse(webResult);
		    conn.disconnect();
		    JSONArray array = (JSONArray) obj;
		    return array;
		} catch (Exception e) {
			Log.e("serverRequest", "Unerwarteter Fehler");
			//mainModel.log(MainModel.LOGLEVEL_ERROR, "Unerwarteter Fehler in Webservice insert: " + postData + " - ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}