package com.edible.main;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;



public class CardInfoClient {


	public HttpURLConnection connection = null;
	public ObjectInputStream ois = null;
	public ObjectOutputStream oos = null;
	public static final String ADD_URL = "http://1.edible-bluecheese.appspot.com/bluecheese";
	public static final String host = "1.edible-bluecheese.appspot.com";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int postMethod(ArrayList<Card> cards) {
		int success = 0;
		try {
			URL postUrl = new URL(ADD_URL);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
 			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();
			oos = new ObjectOutputStream(connection.getOutputStream());
			ArrayList hms = new ArrayList ();
			for(Card card: cards) {
				hms.add(card.getHashMap());
			}
			oos.writeObject(hms);
			oos.flush();
			ois = new ObjectInputStream(connection.getInputStream());
			success = (Integer)ois.readObject();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				ois.close();
//				connection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
		
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList<Card> getMethod(String en) {
//		String getURL = ADD_URL + "?en="+en;
		ArrayList<Card> cards = new ArrayList<Card> ();
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		try {
			httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder().setScheme("http").setHost(host).setPath("/bluecheese").setParameter("en", en).build();
			httpget = new HttpGet(uri);
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				ois = new ObjectInputStream(entity.getContent());
				ArrayList hms = (ArrayList) ois.readObject();
				if((Boolean)hms.get(0) == false) {
					System.out.println("Congrats! You are the first one who found this card!");
					return null;
				}
				else for(int i = 1; i < hms.size(); i++) {
					Card card = new Card((HashMap) hms.get(i));
//					System.out.println(card.toString());
					cards.add(card);	
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if ( ois != null)
					ois.close();
				if ( httpget != null )
					httpget.abort();
				if (response != null )
					response.close();
				if ( httpclient != null )
					httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return cards;
	}
	public CardInfoClient() {
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("null")
	public static void main(String[] args) {
		//Save card
		
		Card card = new Card();
		card.en = "Blue Cheese";//en and cn should be checked not null before transition.
		card.cn = "À¶¸ÉÀÒ";

		CardInfoClient c = new CardInfoClient();
		ArrayList<Card> cards = new ArrayList<Card>();
		
		
		cards.add(card);
		System.out.println(c.postMethod(cards));
		
		//Get card
		cards = c.getMethod("blue Cheese");
		if(cards != null || cards.size() != 0) {
			for(Card cd : cards) {
				System.out.println(cd.toString());
			}
		}
		
		
		
	}

}
