package com.example.dbaccess.token;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	private static final String ANDROID_SERVER_KEY = "AIzaSyDmRAY_cW-qjNXmA-r28k0pTLdeXZnovpc";
	private static final String URL = "https://fcm.googleapis.com/fcm/send";

	@RequestMapping(value = "/registerToken", method = org.springframework.web.bind.annotation.RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenBean> registerToken(@RequestBody String payload) {
		Gson gson = new Gson();
		TokenBean token = gson.fromJson(payload, TokenBean.class);

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("monitor").child("token");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				if (arg0.getChildrenCount() > 0) {
					for (DataSnapshot snapshot : arg0.getChildren()) {
						TokenBean tokenSnapShot = snapshot.getValue(TokenBean.class);

						if (token.getDeviceId().equals(tokenSnapShot.getDeviceId())
								&& token.getToken().equals(tokenSnapShot.getToken())) {
							break;
						} else {
							for (DataSnapshot snapshot1 : arg0.getChildren()) {
								TokenBean tokenSnapShot1 = snapshot1.getValue(TokenBean.class);

								if (token.getDeviceId().equals(tokenSnapShot1.getDeviceId())) {
									if (!token.getToken().equals(tokenSnapShot1.getToken())) {
										DatabaseReference ref = database.getReference("monitor").child("token")
												.child(snapshot.getKey());
										ref.child("token").setValueAsync(token.getToken());
										break;
									}
								} else {
									DatabaseReference ref1 = database.getReference("monitor").child("token").push();
									String key = ref1.getKey();
									token.setId(key);
									ref1.setValueAsync(token);
									break;
								}
							}
						}
					}
				} else {
					DatabaseReference ref1 = database.getReference("monitor").child("token").push();
					String key = ref1.getKey();
					token.setId(key);
					ref1.setValueAsync(token);
				}
			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub

			}
		});

		return null;
	}

	@RequestMapping(value = "/sendNotification", method = org.springframework.web.bind.annotation.RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotificationBean> sendPushNotificationToFCMServer(@RequestBody String payload) {
		Gson gson = new Gson();
		NotificationDataBean notificationDataBean = gson.fromJson(payload, NotificationDataBean.class);
		NotificationBean notification = notificationDataBean.getNotificationBean();
		notification.setTimestamp(Calendar.getInstance().getTimeInMillis());
		notificationDataBean.setNotificationBean(notification);

		// String payloadData = notification.toString();
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
			// logger.debug("Response Code : " + responseCode);
			// logger.debug(payloadData);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			con.disconnect();

			// notification = notificationRepo.save(notification);

			System.out.println("Response : " + response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(notification, HttpStatus.CREATED);
	}
}
