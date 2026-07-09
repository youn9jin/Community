package com.example.community;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("DB 연결 없이 실행되는 환경이라 임시 비활성화")
class CommunityApplicationTests {

	@Test
	void contextLoads() {
	}

}
