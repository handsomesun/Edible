package com.edible.main;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


public class CardConsole {

	public CardConsole() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		CardConsole console = new CardConsole();
		HashMap hm = console.getSingleData("MUSCADET");
		if(hm == null)
			System.out.println("Discovery!");
		else {
			System.out.println(((Card)hm.get("card")).toString());
			if(hm.get("imgID") == null) 
				System.out.println("No Photos!");
			else {
				System.out.println(hm.get("imgID").toString());
			}
			
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap saveSingleData(Card card, String imgPath) {
		HashMap hm = new HashMap();
		//Step 1. save card to datastore
		CardInfoClient c = new CardInfoClient();
		ArrayList<Card> cards = new ArrayList<Card> ();
		cards.add(card);
		hm.put("successData", c.postMethod(cards));
		//Step 2. save image to cloud sql
		CardPhotoClient pc = new CardPhotoClient();
		File file = new File(imgPath);
		if(!file.exists()) //if image does not exist
			hm.put("successImg", -1);
		else {
			hm.put("successImg", pc.PostMethod(imgPath, card.en, card.editor));
		}
		return hm;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getSingleData(String en) {
		HashMap hm = new HashMap();
		//Step 1. get card data from datastore
		CardInfoClient c = new CardInfoClient();
		ArrayList<Card> cards = c.getMethod(en);
		if(cards == null || cards.size() == 0) return null;// if no such record, return null
		hm.put("card", cards.get(0));
		//Step 2. get image IDs from cloud sql
		CardPhotoClient pc = new CardPhotoClient();
		ArrayList<Long> ids = pc.getImgID(en);
		if(ids == null || ids.size() == 0)
			hm.put("imgID", null);
		else
			hm.put("imgID", ids);
		return hm;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getPhotoById(long id) {
		CardPhotoClient pc = new CardPhotoClient();
		return pc.getImgData(id);
	}
	
	@SuppressWarnings("rawtypes")
	public void SaveDataFromFile(String databasePath, String picFilePath) {
		Connection con = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");//加载Access驱动    
			Properties prop = new Properties();     
			prop.put("charSet", "gb2312");  //设置编码防止中文出现乱码  
			con = DriverManager.getConnection("jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + databasePath.replace("\\", "//"), prop);    
			System.out.println(con);
			stat = con.prepareStatement("select * from card");
			rs = stat.executeQuery();
			int i = 1;
			while(rs.next()) {
				Card card = new Card();
				card.en = rs.getString("en").toUpperCase();
				card.cn = rs.getString("cn");
				card.bg = rs.getString("bg");
				card.cat = rs.getString("cat");
				card.info = rs.getString("info");
				card.memo = rs.getString("memo");
				HashMap hm = saveSingleData(card, picFilePath + "\\" + card.en.toLowerCase() + ".png");
				if((Integer)hm.get("successData") == 1)
					System.out.print("No." + i + " Data Succeess!\t");
				else 
					System.out.print("No." + i + " Data Failed!\t");
				if((Integer)hm.get("successImg") == -1)
					System.out.println("No." + i + " Image does not exist!");
				else 
					System.out.println("No." + i + " Image Success!");
				i++;
			}
			System.out.println("finish");
		
		} catch(Exception e) {
			e.printStackTrace();  
		} finally {
			try {
				rs.close();
				stat.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
	

}
