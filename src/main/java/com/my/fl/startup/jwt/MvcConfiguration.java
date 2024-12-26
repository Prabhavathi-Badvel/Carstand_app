package com.my.fl.startup.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class MvcConfiguration implements WebMvcConfigurer {

	
//	@Override
//	 public void addCorsMappings(final CorsRegistry registry) {
//		registry.addMapping("/**").allowedOriginPatterns("*")
//			.allowedMethods("*").maxAge(3600);
//			
//	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOriginPatterns("*") // Use allowedOriginPatterns instead of allowedOrigins
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*").allowCredentials(true)
				.maxAge(3600);
	}


}
