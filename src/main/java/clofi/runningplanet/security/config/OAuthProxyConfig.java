package clofi.runningplanet.security.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Profile("prod")
@Configuration
public class OAuthProxyConfig {

	@Bean
	public DefaultAuthorizationCodeTokenResponseClient authorizationCodeAccessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
			new DefaultAuthorizationCodeTokenResponseClient();
		RestTemplate proxyRestTemplate = new RestTemplate(getProxyFactory());
		proxyRestTemplate.setMessageConverters(
			Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
		proxyRestTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

		accessTokenResponseClient.setRestOperations(proxyRestTemplate);

		return accessTokenResponseClient;
	}

	private static SimpleClientHttpRequestFactory getProxyFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
		factory.setProxy(proxy);
		return factory;
	}
}
