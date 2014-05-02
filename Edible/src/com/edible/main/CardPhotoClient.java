package com.edible.main;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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


public class CardPhotoClient {

	public HttpURLConnection connection = null;
	public ObjectInputStream ois = null;
	public ObjectOutputStream oos = null;
//	public static final String ADD_URL = "http://localhost:8888/photo";
//	public static final String host = "localhost:8888";
	public static final String ADD_URL = "http://1.edible-bluecheese.appspot.com/photo";
	public static final String host = "1.edible-bluecheese.appspot.com";
//	public static final String ADD_URL = "http://1.blue-cheese.appspot.com/photo";
//	public static final String host = "1.blue-cheese.appspot.com";
	public static final int MAX_SIZE = 1024 * 1024;//max size of image 1MB
	
	public CardPhotoClient() {
		// TODO Auto-generated constructor stub
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int PostMethod(String imgPath, String picName, String picEditor) {
		int resp = 0;
		BufferedInputStream in = null;
		byte[] imageData;
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
			

			in = new BufferedInputStream(new FileInputStream(imgPath));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int size = 0;
			byte[] temp = new byte[MAX_SIZE];
			while((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			imageData = out.toByteArray();
			
			HashMap hm = new HashMap();
			hm.put("pName", picName);
			hm.put("pData", imageData);
			hm.put("pEditor", picEditor == null ? "Edible" : picEditor);
			oos.writeObject(hm);
			oos.flush();
			ois = new ObjectInputStream(connection.getInputStream());
			resp = (Integer) ois.readObject();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				oos.close();
				ois.close();
				connection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resp;
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public HashMap getImgData(Long id) {
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		HashMap picData = null;
		try {
			httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder().setScheme("http").setHost(host).setPath("/photodata").setParameter("pID", id+"").build();
			httpget = new HttpGet(uri);
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				ois = new ObjectInputStream(entity.getContent());
				picData = (HashMap) ois.readObject();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				 httpget.abort();
				response.close();
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return picData;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Long> getImgID(String picName) {
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		ArrayList<Long> picIDs = null;//new ArrayList<Long> ();
		try {
			httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder().setScheme("http").setHost(host).setPath("/photo").setParameter("pName", picName).build();
			httpget = new HttpGet(uri);
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				ois = new ObjectInputStream(entity.getContent());
				picIDs = (ArrayList<Long>) ois.readObject();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				httpget.abort();
				response.close();
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return picIDs;
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		CardPhotoClient pc = new CardPhotoClient();
		String sourcePath1 = "C:\\Users\\Ming\\Desktop\\MenuPic\\blue1.png";
		System.out.println(pc.PostMethod(sourcePath1, "blue1", "Ming"));
		
		ArrayList<Long> picIDs = pc.getImgID("blue1");
		System.out.println(picIDs.size() + " IDs in total");
		for(Long id : picIDs) {
			System.out.println(id);
			HashMap hm = pc.getImgData(id);
			System.out.println(hm.get("pDate"));
			
		}
		
			
	}
}
