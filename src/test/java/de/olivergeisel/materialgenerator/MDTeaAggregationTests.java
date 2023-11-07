package de.olivergeisel.materialgenerator;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Order((Order.DEFAULT - 1) * 2)
@Tag("IntegrationTest")
class MDTeaAggregationTests {

	@Test
	void contextLoads() {
		// success if context loads
	}

}
