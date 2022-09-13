package de.stamm.stammvision.data;

import org.jdom2.Element;
import org.json.simple.JSONObject;

public class Template {

	int templates_id = 0;
	String template_name = "";
	String template_file = "";
	String template_text = "";
	int template_duration = 0;
	String template_background = "#ffffff";
	String template_type = "";
	String template_update = "";



	/**
	 * @return the template_name
	 */
	public String getTemplate_name() {
		return template_name;
	}



	/**
	 * @param template_name the template_name to set
	 */
	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}



	/**
	 * @return the template_file
	 */
	public String getTemplate_file() {
		return template_file;
	}



	/**
	 * @param template_file the template_file to set
	 */
	public void setTemplate_file(String template_file) {
		this.template_file = template_file;
	}



	/**
	 * @return the template_text
	 */
	public String getTemplate_text() {
		return template_text;
	}



	/**
	 * @param template_text the template_text to set
	 */
	public void setTemplate_text(String template_text) {
		this.template_text = template_text;
	}



	/**
	 * @return the template_duration
	 */
	public int getTemplate_duration() {
		return template_duration;
	}



	/**
	 * @param template_duration the template_duration to set
	 */
	public void setTemplate_duration(int template_duration) {
		this.template_duration = template_duration;
	}



	/**
	 * @return the template_background
	 */
	public String getTemplate_background() {
		return template_background;
	}



	/**
	 * @param template_background the template_background to set
	 */
	public void setTemplate_background(String template_background) {
		this.template_background = template_background;
	}



	/**
	 * @return the template_type
	 */
	public String getTemplate_type() {
		return template_type;
	}



	/**
	 * @param template_type the template_type to set
	 */
	public void setTemplate_type(String template_type) {
		this.template_type = template_type;
	}



	/**
	 * @return the template_update
	 */
	public String getTemplate_update() {
		return template_update;
	}



	/**
	 * @param template_update the template_update to set
	 */
	public void setTemplate_update(String template_update) {
		this.template_update = template_update;
	}



	/**
	 * @return the templates_id
	 */
	public int getTemplates_id() {
		return templates_id;
	}



	/**
	 * @param templates_id the templates_id to set
	 */
	public void setTemplates_id(int templates_id) {
		this.templates_id = templates_id;
	}



	public Element getXml() {
		Element template = new Element("template");

		Element xml_lement = new Element("templates_id");
		xml_lement.addContent(String.valueOf(templates_id));
		template.addContent(xml_lement);
		
		xml_lement = new Element("template_name");
		xml_lement.addContent(String.valueOf(template_name));
		template.addContent(xml_lement);
		
		xml_lement = new Element("template_file");
		xml_lement.addContent(String.valueOf(template_file));
		template.addContent(xml_lement);

		xml_lement = new Element("template_text");
		xml_lement.addContent(String.valueOf(template_text));
		template.addContent(xml_lement);
		
		xml_lement = new Element("template_duration");
		xml_lement.addContent(String.valueOf(template_duration));
		template.addContent(xml_lement);
		
		xml_lement = new Element("template_background");
		xml_lement.addContent(String.valueOf(template_background));
		template.addContent(xml_lement);
		
		xml_lement = new Element("template_type");
		xml_lement.addContent(String.valueOf(template_type));
		template.addContent(xml_lement);

		xml_lement = new Element("template_update");
		xml_lement.addContent(String.valueOf(template_update));
		template.addContent(xml_lement);
		
		return template;
	}



	public void readXML(Element element) {
		if (element.getChild("templates_id") != null) templates_id = Integer.valueOf(element.getChild("templates_id").getText());
		if (element.getChild("template_name") != null) template_name = String.valueOf(element.getChild("template_name").getText());
		if (element.getChild("template_file") != null) template_file = String.valueOf(element.getChild("template_file").getText());
		if (element.getChild("template_text") != null) template_text = String.valueOf(element.getChild("template_text").getText());
		if (element.getChild("template_duration") != null) template_duration = Integer.valueOf(element.getChild("template_duration").getText());
		if (element.getChild("template_background") != null) template_background = String.valueOf(element.getChild("template_background").getText());
		if (element.getChild("template_type") != null) template_type = String.valueOf(element.getChild("template_type").getText());
		if (element.getChild("template_update") != null) template_update = String.valueOf(element.getChild("template_update").getText());
		
	}



	public void readResult(JSONObject jsonData) {
		templates_id = Integer.valueOf(String.valueOf(jsonData.get("templates_id")));
		template_name = String.valueOf(jsonData.get("template_name"));
		template_file = String.valueOf(jsonData.get("template_file"));
		template_text = String.valueOf(jsonData.get("template_text"));
		template_duration = Integer.valueOf(String.valueOf(jsonData.get("template_duration")));
		template_background = String.valueOf(jsonData.get("template_background"));
		template_type = String.valueOf(jsonData.get("template_type"));
		template_update = String.valueOf(jsonData.get("template_update"));
	}
	
	
	
}
