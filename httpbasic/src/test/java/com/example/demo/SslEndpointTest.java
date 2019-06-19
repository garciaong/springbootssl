package com.example.demo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class SslEndpointTest {

	@Value("${server.ssl.trust.store}")
    private Resource keyStore;
    @Value("${server.ssl.trust.store.password}")
    private String keyStorePassword;
	
	@Test
	public void testDemoEndpoint() {
		try {
			ResponseEntity<String> response = restTemplate2().getForEntity("https://localhost:8080/demo", String.class,
					Collections.emptyMap());

			assertEquals("HTTP Basic Authentication working...", response.getBody());
			assertEquals(HttpStatus.OK, response.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private RestTemplate restTemplate2() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			CertificateException, IOException {
		SSLContext sslContext = new SSLContextBuilder()
				.loadTrustMaterial(keyStore.getURL(), keyStorePassword.toCharArray()).build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}

	private RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException,
			CertificateException, FileNotFoundException, IOException, KeyManagementException {
		KeyStore clientStore = KeyStore.getInstance("PKCS12");
		clientStore.load(new FileInputStream("C:/workspace/httpbasic/keystore/sb2.p12"), "123456".toCharArray());// keystore
																													// password

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.useProtocol("TLS");
		sslContextBuilder.loadKeyMaterial(clientStore, "123456".toCharArray());// keystore password
		sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());

		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
				sslContextBuilder.build());
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectTimeout(10000); // 10 seconds
		requestFactory.setReadTimeout(10000); // 10 seconds
		return new RestTemplate(requestFactory);
	}

}
