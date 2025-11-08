package dev.twiceb.instanceservice;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.twiceb.instanceservice.domain.repository.InstanceRepository;

class InstanceServiceApplicationTests extends IntegrationTestBase {

	@Autowired
	private InstanceRepository instanceRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void checkInstanceRegistration() {
		long count = instanceRepository.count();
		assertEquals(1l, count);
	}

}
