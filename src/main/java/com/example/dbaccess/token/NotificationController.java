package com.example.dbaccess.token;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	private static final String ANDROID_SERVER_KEY = "AIzaSyBlkUD_Q7nzuohr2D4SKo9eJAcUoXdOCMk";
	
	@Autowired
	private TokenRepo tokenRepo;
	
	@Autowired
	private NotificationRepo notificationRepo;
	
	private static final String URL = "https://fcm.googleapis.com/fcm/send";
	
	@RequestMapping(value = "/registerToken", method = org.springframework.web.bind.annotation.RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenBean> registerToken(@RequestBody String payload) {		
		Gson gson = new Gson();
		TokenBean token = gson.fromJson(payload, TokenBean.class);
		
		Iterable<TokenBean> tokenBeans = tokenRepo.findAll();
		System.out.println("TokenBeans" + ((tokenBeans != null) ? tokenBeans : "Null"));
		if(tokenBeans == null || !tokenBeans.iterator().hasNext()) {
			System.out.println("No Token-> Add new Record");
			token = tokenRepo.save(token);
			return new ResponseEntity<>(token, HttpStatus.CREATED);
		}
		
		for(TokenBean bean : tokenBeans) {
			if(bean.getDeviceId().equals(token.getDeviceId())) {
				tokenRepo.deleteById(bean.getId());
				token = tokenRepo.save(token);
				return new ResponseEntity<>(token, HttpStatus.CREATED);
			} else {
				token = tokenRepo.save(token);
				return new ResponseEntity<>(token, HttpStatus.CREATED);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/sendNotification", method = org.springframework.web.bind.annotation.RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotificationBean> sendPushNotificationToFCMServer(@RequestBody String payload) {
		Gson gson = new Gson();
		NotificationDataBean notificationDataBean = gson.fromJson(payload, NotificationDataBean.class);
		NotificationBean notification = notificationDataBean.getNotificationBean();
		notification.setTimestamp(Calendar.getInstance().getTimeInMillis());
		notificationDataBean.setNotificationBean(notification);
		
//		String payloadData = notification.toString();
		String notificationDataStr = gson.toJson(notificationDataBean, NotificationDataBean.class);
		String payloadData = notificationDataStr;
		java.net.URL obj = null;
		HttpURLConnection con = null;
		try {
			obj = new java.net.URL(URL);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; charset-utf8");
			con.setRequestProperty("Authorization", "key=" + ANDROID_SERVER_KEY);
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(payloadData.getBytes("UTF-8"));
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();
//			logger.debug("Response Code : " + responseCode);
//			logger.debug(payloadData);
			System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			con.disconnect();
			
			notification = notificationRepo.save(notification);

			System.out.println("Response : " + response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(notification, HttpStatus.CREATED);
	}
}
