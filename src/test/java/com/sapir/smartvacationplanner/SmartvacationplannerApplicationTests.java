package com.sapir.smartvacationplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

// Full context load requires a reachable MySQL instance configured via DB_USERNAME and DB_PASSWORD.
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
@SpringBootTest
class SmartvacationplannerApplicationTests {

	@Test
	void contextLoads() {
	}

}
