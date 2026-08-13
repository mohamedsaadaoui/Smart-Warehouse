package com.saadaoui.smartwarehouse;

import com.saadaoui.smartwarehouse.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class SmartWarehouseApplicationTests {

	@Test
	void contextLoads() {
	}

}
