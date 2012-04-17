/**
 * 
 */
package configr;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import curt.android.ginger.JSONHandler;
import curt.android.ginger.Part.Part;

/**
 * @author alexninneman
 *
 */
public class Configurator {

	private String mount, year, make, model, style;
	public configStates state;
	public enum configStates { MOUNT, YEAR, MAKE, MODEL, STYLE, CONFIGURED };
	
	
	public Configurator(){
		this.setConfigurator("", "", "", "", "");
	}
	
	/**
	 * @param mount
	 */
	public Configurator(String mount){
		this.setConfigurator(mount,"","","","");
	}

	/**
	 * @param mount
	 * @param year
	 */
	public Configurator(String mount, String year){
		this.setConfigurator(mount, year, "", "", "");
	}
	
	/**
	 * @param mount
	 * @param year
	 * @param make
	 */
	public Configurator(String mount, String year, String make){
		this.setConfigurator(mount, year, make, "", "");
	}

	/**
	 * @param mount
	 * @param year
	 * @param make
	 * @param model
	 */
	public Configurator(String mount, String year, String make, String model){
		this.setConfigurator(mount, year, make, model, "");
	}
	
	/**
	 * @param mount
	 * @param year
	 * @param make
	 * @param model
	 * @param style
	 */
	public Configurator(String mount, String year, String make, String model, String style){
		this.setConfigurator(mount, year, make, model, style);
	}
	
	/**
	 * @param mount
	 * @param year
	 * @param make
	 * @param model
	 * @param style
	 */
	private void setConfigurator(String mount, String year, String make,
			String model, String style) {
		this.mount = mount;
		this.year = year;
		this.make = make;
		this.model = model;
		this.style = style;
		this.setState();
	}

	/**
	 * @return the mount
	 */
	public String getMount() {
		return mount;
	}
	
	public String getDisplayableMount() {
		String mount = "";
		if(this.mount != null && this.mount.toLowerCase().contains("rear")){
			mount = "Rear Mount";
		}else{
			mount = "Front Mount";
		}
		return mount;
	}

	/**
	 * @param mount the mount to set
	 */
	public void setMount(String mount) {
		if(mount == null){
			this.mount = null;
		}else{
			if(mount.toUpperCase().contains("FRONT")){
				this.mount = "front";
			}else{
				this.mount = "rear";
			}
		}
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the make
	 */
	public String getMake() {
		return make;
	}

	/**
	 * @param make the make to set
	 */
	public void setMake(String make) {
		this.make = make;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	public List<String> getOptions() {
		List<String> options = new ArrayList<String>();
		if(this.mount == null || this.mount.length() == 0){
			this.state = configStates.MOUNT;
			options.add("Front Mount");
			options.add("Rear Mount");
		}else if(this.year == null || this.year.length() == 0){
			options.addAll(this.getYears());
		}else if(this.make == null || this.make.length() == 0){
			options.addAll(this.getMakes());
		}else if(this.model == null || this.model.length() == 0){
			options.addAll(this.getModels());
		}else if(this.style == null || this.style.length() == 0){
			options.addAll(this.getStyles());
		}
		return options;
	}
	
	public List<String> getYears(){
		Gson gson = new Gson();
		String url = "GetYear?dataType=JSON&mount=" + this.mount;
		this.state = configStates.YEAR;
		
		
		String jString = JSONHandler.getJSONListFromURL(url);
		List<String> tmpYears = gson.fromJson(jString, new TypeToken<List<String>>(){}.getType());
		if(tmpYears == null){
			tmpYears = new ArrayList<String>();
		}
		
		List<String> years = new ArrayList<String>();
		for(Iterator<String> i = tmpYears.iterator(); i.hasNext();){
			String tmp = i.next();
			years.add(tmp.replace(".0", ""));
		}
		return years;
	}
	
	public List<String> getMakes(){
		Gson gson = new Gson();
		this.state = configStates.MAKE;
		String url = "GetMake?dataType=JSON&mount=" + this.mount + "&year=" + this.year;
		
		String jString = JSONHandler.getJSONListFromURL(url);
		List<String> makes = gson.fromJson(jString, new TypeToken<List<String>>(){}.getType());
		if(makes == null){
			makes = new ArrayList<String>();
		}
		return makes;
	}
	
	public List<String> getModels(){
		Gson gson = new Gson();
		this.state = configStates.MODEL;
		String url = "GetModel?dataType=JSON&mount=" + URLEncoder.encode(this.mount) + "&year=" + URLEncoder.encode(this.year) + "&make=" + URLEncoder.encode(this.make);
		
		String jString = JSONHandler.getJSONListFromURL(url);
		List<String> models = gson.fromJson(jString, new TypeToken<List<String>>(){}.getType());
		if(models == null){
			models = new ArrayList<String>();
		}
		return models;
	}
	
	public List<String> getStyles(){
		Gson gson = new Gson();
		this.state = configStates.STYLE;
		String url = "GetStyle?dataType=JSON&mount=" + URLEncoder.encode(this.mount) + "&year=" + URLEncoder.encode(this.year) + "&make=" + URLEncoder.encode(this.make) + "&model=" + URLEncoder.encode(this.model);
		
		String jString = JSONHandler.getJSONListFromURL(url);
		List<String> styles = gson.fromJson(jString, new TypeToken<List<String>>(){}.getType());
		if(styles == null){
			styles = new ArrayList<String>();
		}
		return styles;
	}
	
	public List<Part> getParts(){
		Gson gson = new Gson();
		String mount = "";
		if(mount.toLowerCase().contains("rear")){
			mount = "rear";
		}else{
			mount = "front";
		}
		String url = "GetParts?dataType=JSON&mount=" + mount + "&year=" + URLEncoder.encode(this.year) + "&make=" + URLEncoder.encode(this.make) + "&model=" + URLEncoder.encode(this.model) + "&style=" + URLEncoder.encode(this.style);
		
		String jString = JSONHandler.getJSONListFromURL(url);
		List<Part> parts = gson.fromJson(jString, new TypeToken<List<Part>>(){}.getType());
		if(parts == null){
			parts = new ArrayList<Part>();
		}
		return parts;
	}
	
	public String getState(){
		if(this.mount == null || this.mount.length() == 0){
			return "MOUNT";
		}
		if(this.year == null || this.year.length() == 0){
			return "YEAR";
		}
		if(this.make == null || this.make.length() == 0){
			return "MAKE";
		}
		if(this.model == null || this.model.length() == 0){
			return "MODEL";
		}
		if(this.style == null || this.style.length() == 0){
			return "STYLE";
		}
		return "CONFIGURED";
	}
	
	public void setState(){
		if(this.mount == null || this.mount.length() == 0){
			this.state = configStates.MOUNT;
			this.year = this.make = this.model = this.style = null;
		}else if(this.year == null || this.year.length() == 0){
			this.state = configStates.YEAR;
			this.make = this.model = this.style = null;
		}else if(this.make == null || this.make.length() == 0){
			this.state = configStates.MAKE;
			this.model = this.style = null;
		}else if(this.model == null || this.model.length() == 0){
			this.state = configStates.MODEL;
			this.style = null;
		}else if(this.style == null || this.style.length() == 0){
			this.state = configStates.STYLE;
		}else{
			this.state = configStates.CONFIGURED;
		}
		
	}
	
	public void clear(){
		this.mount = this.year = this.make = this.model = this.style = null;
	}
}
