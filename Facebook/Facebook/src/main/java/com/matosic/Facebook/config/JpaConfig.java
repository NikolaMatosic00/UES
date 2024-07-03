package com.matosic.Facebook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.matosic.Facebook.repository.jpa")
public class JpaConfig {
}
