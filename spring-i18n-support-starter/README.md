# spring-i18n-support-starter

Spring-I18n-Support module can be configured using Auto-Configuration. This document provides a basic overview on how to utilize the spring-i18n-support-starter. Detailed information on how to work with the starter may be observed in the spring-i18n-support-samples-starter project.

## Step 1: Add the required dependencies

Add the dependency for the module itself (i.e. spring-i18n-support) and the corresponding starter module (i.e. spring-i18n-support-starter) which is responsible for the auto-configuration of the module.

    <dependency>
		<groupId>com.namics.oss.spring.support.i18n</groupId>
		<artifactId>spring-i18n-support</artifactId>
		<version>1.0.0</version>
	</dependency>
	<dependency>
		<groupId>com.namics.oss.spring.support.i18n</groupId>
		<artifactId>spring-i18n-support-web</artifactId>
		<version>1.0.0</version>
	</dependency>
	<dependency>
		<groupId>com.namics.oss.spring.support.i18n</groupId>
		<artifactId>spring-i18n-support-starter</artifactId>
		<version>1.0.0</version>
	</dependency>

### Configuration of the web interface
The starter allows you to override the default settings for servlet-name and servlet-mapping.

    # Optional properties for spring-i18n-support
    com.namics.oss.spring.support.i18n.web.servlet-name=i18nServlet
	com.namics.oss.spring.support.i18n.web.servlet-mapping=/i18n/*
   
Add the iFrame in a view of the application.

	<iframe class="frame" data-th-src="@{'/i18n/list.html'}"></iframe>
	