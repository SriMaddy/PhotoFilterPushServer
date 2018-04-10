package com.example.dbaccess.firebase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

@Component
public class FirebaseDBHelper {

	public void init() {
		FileInputStream serviceAccount = null;
		try {
			serviceAccount = new FileInputStream("photofilter-5a456-firebase-adminsdk-8lp3a-9c988476e9.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://photofilter-5a456.firebaseio.com/")
					  .build();

			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
