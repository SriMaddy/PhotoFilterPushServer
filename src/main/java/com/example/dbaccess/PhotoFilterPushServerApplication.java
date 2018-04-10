package com.example.dbaccess;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.dbaccess.firebase.FirebaseDBHelper;

@SpringBootApplication
public class PhotoFilterPushServerApplication {
	
	@Autowired
	private FirebaseDBHelper dbHelper;

	public static void main(String[] args) {
		SpringApplication.run(PhotoFilterPushServerApplication.class, args);
	}
	
	@PostConstruct
	public void initFirebaseApp() {
		dbHelper.init();
	}
}
