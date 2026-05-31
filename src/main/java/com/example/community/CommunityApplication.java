package com.example.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class CommunityApplication implements CommandLineRunner {

	@Autowired
	DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println("✅ DB 연결 성공: " + conn.getMetaData().getURL());
		conn.close();
	}
}
