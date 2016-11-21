import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.*;
	
/** 
 * @author D.Goller, goller@hm.edu
 * @version 16.11.2016
 */
public class HueControl {
	
	private static final String[] BRIDGE_LIST = {
			"10.28.9.120", "197ea42c25303cef1a68c4042ed5688",
			"10.28.9.121", "3dc1d8f23e55321f3c049c03ac88dff",
			"10.28.9.122", "2217334838210e7f244460f83b42026f",
			"10.28.9.123", "2b2d3ff23d63751f10c1d8c0332d50ff",
			"localhost", "newdeveloper",
	};
	
	private final String bridge;
	private final String id;
	
	public static void main(String... args) throws IOException, InterruptedException{
		HueControl hue = new HueControl(4);
		for(int i=1;i<4;i++) {
		hue.setSaturation(i,254);
		hue.setTransitionTime(i,0);
		hue.setBrightness(i,254);
		hue.setColor(i,0.73,0.26);
		}	
		for(int i=1;i<4;i++) {
		hue.setOn(i,true);
		}
		Thread.sleep(3000);
		for(int i=1;i<4;i++) {
		hue.setAlert(i);
		}
		Thread.sleep(20000);
		for(int i=1;i<4;i++) {
		hue.setOn(i,false);
		}
	}
	
	public HueControl(int bridge) {
		if (bridge > BRIDGE_LIST.length / 2 || bridge < 1) {
			throw new IllegalArgumentException();
		}
		this.bridge = BRIDGE_LIST[(bridge - 1) * 2];
		this.id = BRIDGE_LIST[bridge * 2 - 1];
	}
	
	public boolean setOn(int lamp, boolean on) throws IOException {
		JSONObject json = new JSONObject();
		json.put("on", on);
		return setLampState(lamp, json);
	}
	
	// in range 0 - 254. 0 is not off.
	public boolean setBrightness(int lamp, int brightness) throws IOException {
		JSONObject json = new JSONObject();
		json.put("bri", brightness);
		return setLampState(lamp, json);
	}
	
	// in range 0 - 254.
	public boolean setSaturation(int lamp, int saturation) throws IOException {
		JSONObject json = new JSONObject();
		json.put("sat", saturation);
		return setLampState(lamp, json);
	}
	
	// time for transition in centiseconds.
	public boolean setTransitionTime(int lamp, int cs) throws IOException {
		JSONObject json = new JSONObject();
		json.put("transitiontime", cs);
		return setLampState(lamp, json);
	}
	
	// flashes repeatedly for 10 seconds.
	public boolean setAlert(int lamp) throws IOException {
		JSONObject json = new JSONObject();
		json.put("alert", "lselect");
		return setLampState(lamp, json);
	}
	
	// color as array of xy-coordinates.
	public boolean setColor(int lamp, double  x, double y) throws IOException {
		JSONObject json = new JSONObject();
		json.append("xy", x);
		json.append("xy", y);
		return setLampState(lamp, json);
	}
	
	private boolean set(String put, JSONObject json) throws IOException {
		URL con = new URL("http", bridge, "/api/" + id + put);
		HttpURLConnection httpCon = (HttpURLConnection) con.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
		out.write(json.toString());
		out.close();
		return httpCon.getResponseCode() == HttpURLConnection.HTTP_OK;
	}
	
	private boolean setLampState(int lamp, JSONObject json) throws IOException {
		return set("/lights/" + lamp + "/state", json);
	}
}
