# Spring i18n Support

System        | Status
--------------|------------------------------------------------        
CI master     | [![Build Status][travis-master]][travis-url]
CI develop    | [![Build Status][travis-develop]][travis-url]
Dependency    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.namics.oss.spring.support.i18n/spring-i18n-support/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.namics.oss.spring.support.i18n/spring-i18n-support)

This modules aims to provide i18n database resource.

## Usage

### Maven Dependency (Latest Version in `pom.xml`):

	<dependency>
		<groupId>com.namics.oss.spring.support.i18n</groupId>
		<artifactId>spring-i18n-support</artifactId>
		<version>1.1.0</version>
	</dependency>
	
This modules are using spring auto-configured beans. Setup instructions and configuration options can be found in the [spring-i18n-support-starter](spring-i18n-support-starter) module. If you are not using spring boot Auto-Configuration, have a look at [spring-i18n-support](spring-i18n-support) module.

### Requirements	

- Java: JDK 8 
- Spring: Version 4 or higher

[travis-master]: https://travis-ci.org/namics/spring-i18n-support.svg?branch=master
[travis-develop]: https://travis-ci.org/namics/spring-i18n-support.svg?branch=develop
[travis-url]: https://travis-ci.org/namics/spring-i18n-support