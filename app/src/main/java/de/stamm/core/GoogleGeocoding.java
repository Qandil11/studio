package de.stamm.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleGeocoding {
	
	String googleMaps = "http://maps.google.com/maps/geo?output=xml&q=";

	public String[] getLonLat(String street, String postcode, String city, String country) {
		if (country.trim().equals("")) {
			country = "Deutschland";
		}
		String[] lonlat = {"0", "0", "0"};
		String location = street+"+"+postcode+"+"+city+"+"+country;
		location = location.toLowerCase().replace("ß", "ss").replace("ä", "%E4").replace("ö", "%F6").replace("ü", "%FC");
		URL requestUrl;
		InputStream is;
		try {
			location = URLEncoder.encode(location, "UTF-8");
			requestUrl = new URL(googleMaps+location);
			is = requestUrl.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String s;
			while((s = br.readLine()) != null) {
				if (s.contains("<coordinates>")) {
					lonlat = s.replace("<Point><coordinates>", "").replace("</coordinates></Point>", "").trim().split(",");
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lonlat;
	}
	
	public double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515; //Miles
		  if (unit == 'K') {
		    dist = dist * 1.609344; //Kilometers
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684; // Nautical Miles
		  }
		  return (dist);
	}
	
	private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}

}
