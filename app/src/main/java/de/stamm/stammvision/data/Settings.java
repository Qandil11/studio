package de.stamm.stammvision.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

public class Settings extends Application {

    MainModel mainModel;
	    
    String device_id = "";
    String online_folder = "";
    String customers_name = "";
    
	public Settings(MainModel mainModel) {
		this.mainModel = mainModel;
	}

	public void saveXML(boolean docTypeAvailable) {
		Element root = new  Element("settings");
		Document doc = new Document(root);
		if (docTypeAvailable) {
			DocType doctype =  new DocType("settings", "settings.dtd");
			doc.setDocType(doctype);
		}
		
		Element item = new Element("app_settings");
		
		Element element = new Element("device_id");
		element.addContent(String.valueOf(device_id));
		item.addContent(element);
		
		element = new Element("online_folder");
		element.addContent(String.valueOf(online_folder));
		item.addContent(element);
		
		element = new Element("customers_name");
		element.addContent(String.valueOf(customers_name));
		item.addContent(element);
					
		root.addContent(item);
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileOutputStream output;
		try {
			output = new FileOutputStream(MainModel.PATH_APP+MainModel.SETTINGS_FILENAME);
			outputter.output(doc, output);
			output.close();
		} catch (FileNotFoundException e) {
			Log.e("saveXML", "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.e("saveXML", "IOException: " + e.getMessage());
		}
	}
		
	public void loadXML() {		
		Document doc;
		try {
			FileInputStream input = new FileInputStream(MainModel.PATH_APP+MainModel.SETTINGS_FILENAME);
			SAXBuilder sxbuild = new SAXBuilder();
			doc = sxbuild.build(input);
			Element root = doc.getRootElement();
			
			if (root.getChildren("app_settings") != null) {
				List<Element> list = root.getChildren("app_settings");
				for (int i = 0; i < list.size(); i++) {
					Element item = (Element) list.get(i);
					if (item.getChild("device_id") != null) device_id = String.valueOf(item.getChild("device_id").getText());
					if (item.getChild("online_folder") != null) online_folder = String.valueOf(item.getChild("online_folder").getText());
					if (item.getChild("customers_name") != null) customers_name = String.valueOf(item.getChild("customers_name").getText());
				}
			}
		} catch (JDOMException e) {
			Log.e("loadItemsDelivery", "JDOMException: " + e.getMessage());
		} catch (IOException e) {
			Log.e("loadItemsDelivery", "Fehler beim Einlesen der Datei: " + e.getMessage());
		}
	}

	/**
	 * @return the device_id
	 */
	public String getDevice_id() {
		return device_id;
	}

	/**
	 * @param device_id the device_id to set
	 */
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
		saveXML(false);
	}

	/**
	 * @return the online_folder
	 */
	public String getOnline_folder() {
		return online_folder;
	}

	/**
	 * @param online_folder the online_folder to set
	 */
	public void setOnline_folder(String online_folder) {
		this.online_folder = online_folder;
	}

	/**
	 * @return the customers_name
	 */
	public String getCustomers_name() {
		return customers_name;
	}

	/**
	 * @param customers_name the customers_name to set
	 */
	public void setCustomers_name(String customers_name) {
		this.customers_name = customers_name;
	}

	public boolean fileExists() {
		File settingsFile = new File(MainModel.PATH_APP+MainModel.SETTINGS_FILENAME);
		return settingsFile.exists();
	}

	public void clearData() {
		this.online_folder = "";
		this.customers_name = "";		
	}
	
	
	
}
