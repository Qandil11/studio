package de.stamm.stammvision.data;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;
import org.json.simple.JSONObject;

public class Playlist {

	LinkedList<Template> templates = new LinkedList<Template>();
	int playlists_id = 0;
	String playlist_name = "";
	String start_date = "";
	String end_date = "";
	String daily_start_time = "";
	String daily_end_time = "";
	int mo = 0;
	int tu = 0;
	int we = 0;
	int th = 0;
	int fr = 0;
	int sa = 0;
	int so = 0;
	String playlist_update = "";
	
	
	/**
	 * @return the templates
	 */
	public LinkedList<Template> getTemplates() {
		return templates;
	}
	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(LinkedList<Template> templates) {
		this.templates = templates;
	}

	/**
	 * @return the playlist_name
	 */
	public String getPlaylist_name() {
		return playlist_name;
	}
	/**
	 * @param playlist_name the playlist_name to set
	 */
	public void setPlaylist_name(String playlist_name) {
		this.playlist_name = playlist_name;
	}
	/**
	 * @return the start_date
	 */
	public String getStart_date() {
		return start_date;
	}
	/**
	 * @param start_date the start_date to set
	 */
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	/**
	 * @return the end_date
	 */
	public String getEnd_date() {
		return end_date;
	}
	/**
	 * @param end_date the end_date to set
	 */
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	/**
	 * @return the daily_start_time
	 */
	public String getDaily_start_time() {
		return daily_start_time;
	}
	/**
	 * @param daily_start_time the daily_start_time to set
	 */
	public void setDaily_start_time(String daily_start_time) {
		this.daily_start_time = daily_start_time;
	}
	/**
	 * @return the daily_end_time
	 */
	public String getDaily_end_time() {
		return daily_end_time;
	}
	/**
	 * @param daily_end_time the daily_end_time to set
	 */
	public void setDaily_end_time(String daily_end_time) {
		this.daily_end_time = daily_end_time;
	}
	/**
	 * @return the mo
	 */
	public int getMo() {
		return mo;
	}
	/**
	 * @param mo the mo to set
	 */
	public void setMo(int mo) {
		this.mo = mo;
	}
	/**
	 * @return the tu
	 */
	public int getTu() {
		return tu;
	}
	/**
	 * @param tu the tu to set
	 */
	public void setTu(int tu) {
		this.tu = tu;
	}
	/**
	 * @return the we
	 */
	public int getWe() {
		return we;
	}
	/**
	 * @param we the we to set
	 */
	public void setWe(int we) {
		this.we = we;
	}
	/**
	 * @return the th
	 */
	public int getTh() {
		return th;
	}
	/**
	 * @param th the th to set
	 */
	public void setTh(int th) {
		this.th = th;
	}
	/**
	 * @return the fr
	 */
	public int getFr() {
		return fr;
	}
	/**
	 * @param fr the fr to set
	 */
	public void setFr(int fr) {
		this.fr = fr;
	}
	/**
	 * @return the sa
	 */
	public int getSa() {
		return sa;
	}
	/**
	 * @param sa the sa to set
	 */
	public void setSa(int sa) {
		this.sa = sa;
	}
	/**
	 * @return the so
	 */
	public int getSo() {
		return so;
	}
	
	/**
	 * @param so the so to set
	 */
	public void setSo(int so) {
		this.so = so;
	}
			
	/**
	 * @return the playlist_update
	 */
	public String getPlaylist_update() {
		return playlist_update;
	}
	/**
	 * @param playlist_update the playlist_update to set
	 */
	public void setPlaylist_update(String playlist_update) {
		this.playlist_update = playlist_update;
	}
	/**
	 * @return the playlists_id
	 */
	public int getPlaylists_id() {
		return playlists_id;
	}
	/**
	 * @param playlists_id the playlists_id to set
	 */
	public void setPlaylists_id(int playlists_id) {
		this.playlists_id = playlists_id;
	}
	
	
	public Element getXML() {
		Element playlist = new Element("playlist");

		Element xml_lement = new Element("playlists_id");
		xml_lement.addContent(String.valueOf(playlists_id));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("playlist_name");
		xml_lement.addContent(String.valueOf(playlist_name));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("start_date");
		xml_lement.addContent(String.valueOf(start_date));
		playlist.addContent(xml_lement);

		xml_lement = new Element("end_date");
		xml_lement.addContent(String.valueOf(end_date));
		playlist.addContent(xml_lement);

		xml_lement = new Element("daily_start_time");
		xml_lement.addContent(String.valueOf(daily_start_time));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("daily_end_time");
		xml_lement.addContent(String.valueOf(daily_end_time));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("mo");
		xml_lement.addContent(String.valueOf(mo));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("tu");
		xml_lement.addContent(String.valueOf(tu));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("we");
		xml_lement.addContent(String.valueOf(we));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("th");
		xml_lement.addContent(String.valueOf(th));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("fr");
		xml_lement.addContent(String.valueOf(fr));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("sa");
		xml_lement.addContent(String.valueOf(sa));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("so");
		xml_lement.addContent(String.valueOf(so));
		playlist.addContent(xml_lement);
		
		xml_lement = new Element("playlist_update");
		xml_lement.addContent(String.valueOf(playlist_update));
		playlist.addContent(xml_lement);
		

		xml_lement = new Element("templates");
		for (int i = 0; i < templates.size(); i++) {
			xml_lement.addContent(templates.get(i).getXml());			
		}
		playlist.addContent(xml_lement);
		
		return playlist;
	}
	public void readXML(Element element) {
		if (element.getChild("playlists_id") != null) playlists_id = Integer.valueOf(element.getChild("playlists_id").getText());
		if (element.getChild("playlist_name") != null) playlist_name = String.valueOf(element.getChild("playlist_name").getText());
		if (element.getChild("start_date") != null) start_date = String.valueOf(element.getChild("start_date").getText());
		if (element.getChild("end_date") != null) end_date = String.valueOf(element.getChild("end_date").getText());
		if (element.getChild("daily_start_time") != null) daily_start_time = String.valueOf(element.getChild("daily_start_time").getText());
		if (element.getChild("daily_end_time") != null) daily_end_time = String.valueOf(element.getChild("daily_end_time").getText());
		if (element.getChild("mo") != null) mo = Integer.valueOf(element.getChild("mo").getText());
		if (element.getChild("tu") != null) tu = Integer.valueOf(element.getChild("tu").getText());
		if (element.getChild("we") != null) we = Integer.valueOf(element.getChild("we").getText());
		if (element.getChild("th") != null) th = Integer.valueOf(element.getChild("th").getText());
		if (element.getChild("fr") != null) fr = Integer.valueOf(element.getChild("fr").getText());
		if (element.getChild("sa") != null) sa = Integer.valueOf(element.getChild("sa").getText());
		if (element.getChild("so") != null) so = Integer.valueOf(element.getChild("so").getText());
		if (element.getChild("playlist_update") != null) playlist_update = String.valueOf(element.getChild("playlist_update").getText());
		if (element.getChild("templates") != null) {
			templates.clear();
			List<Element> list = element.getChild("templates").getChildren("template");
			for (int i = 0; i < list.size(); i++) {
				Template template = new Template();
				template.readXML((Element) list.get(i));
				templates.add(template);
			}
		}
	}
	
	public void readResult(JSONObject jsonData) {
		playlists_id = Integer.valueOf(String.valueOf(jsonData.get("playlists_id")));
		playlist_name = String.valueOf(jsonData.get("playlist_name"));
		start_date = String.valueOf(jsonData.get("start_date"));
		end_date = String.valueOf(jsonData.get("end_date"));
		daily_start_time = String.valueOf(jsonData.get("daily_start_time"));
		daily_end_time = String.valueOf(jsonData.get("daily_end_time"));
		mo = Integer.valueOf(String.valueOf(jsonData.get("mo")));
		tu = Integer.valueOf(String.valueOf(jsonData.get("tu")));
		we = Integer.valueOf(String.valueOf(jsonData.get("we")));
		th = Integer.valueOf(String.valueOf(jsonData.get("th")));
		fr = Integer.valueOf(String.valueOf(jsonData.get("fr")));
		sa = Integer.valueOf(String.valueOf(jsonData.get("sa")));
		so = Integer.valueOf(String.valueOf(jsonData.get("so")));
		playlist_update = String.valueOf(jsonData.get("playlist_update"));
	}	
	
}
