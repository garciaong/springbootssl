package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SslEndpointTest {

	@Value("${server.ssl.trust.store}")
	private Resource keyStore;
	@Value("${server.ssl.trust.store.password}")
	private String keyStorePassword;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testEndpoint() throws Exception {
		mvc.perform(get("/demo").secure(true).header("Authorization", "Basic YWRtaW46MTIz"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/text;charset=UTF-8"))
				.andExpect(content().string("HTTP Basic Authentication working..."));
	}

}
