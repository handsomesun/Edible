package com.edible.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Card (id, en, cn, bg, cat, info, memo, date, lat, log, editor)
 * @author Ming
 *
 */

public class Card {

	public Long id = null;
	public String en = null, cn = null, bg = null, cat = null, info = null, memo = null, editor = null;
	public Date date = null;
	public Double lat = null, log = null;
	public Card() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("rawtypes")
	public Card(HashMap hm) {
		 id = (Long) hm.get("id");
		 en = (String) hm.get("en");
		 cn = (String) hm.get("cn");
		 bg = (String) hm.get("bg");
		 cat = (String) hm.get("cat");
		 info = (String) hm.get("info");
		 memo = (String) hm.get("memo");
		 editor = (String) hm.get("editor");
		 lat = (Double) hm.get("lat");
		 log = (Double) hm.get("log");
		 date = (Date) hm.get("date"); 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap getHashMap() {
		HashMap hm = new HashMap();
		hm.put("id", id);
		hm.put("en", en);
		hm.put("cn", cn);
		hm.put("bg", bg);
		hm.put("cat", cat);
		hm.put("info", info);
		hm.put("memo", memo);
		hm.put("editor", editor);
		hm.put("lat", lat);
		hm.put("log", log);
		hm.put("date", date);
		return hm;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "";
		 s += "id: " + id + "\n";
		 s += "en: " + en + "\n";
		 s += "cn: " + cn + "\n";
		 s += "bg: " + bg + "\n";
		 s += "cat: " + cat + "\n";
		 s += "info: " + info + "\n";
		 s += "memo: " + memo + "\n";
		 s += "editor: " + editor + "\n";
		 s += "lat: " + lat + "\n";
		 s += "log: " + log + "\n";
		 s += "date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "\n";
		 return s;
	}
	
	

}
