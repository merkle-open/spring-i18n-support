/*
* Copyright 2000-2014 Namics AG. All rights reserved.
*/

package com.namics.oss.spring.support.i18n.dao.jpa;

import com.namics.oss.spring.support.i18n.dao.MessageSourceManagementDao;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * JpaMessageSourceDaoTest.
 *
 * @author aschaefer, Namics AG
 * @since 18.03.14 16:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JpaMessageSourceDaoTest.Config.class)
@Transactional
public class JpaMessageSourceDaoTest extends TestCase {

	@Autowired
	MessageSourceManagementDao dao;

	@Test
	public void testGetAvailableLanguages() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.GERMANY.toString())
				.type("type")
				.message("msg1"));
		assertEquals(1, dao.findDistinctLang().size());
	}

	@Test
	public void testPutResourceMessage() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		assertEquals("msg1_us", dao.findByCodeAndLang("test1", Locale.US.toString()).get(0).getMessage());
	}

	@Test
	public void testGetAllMessageResourceEntries() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.GERMANY.toString())
				.type("type")
				.message("msg1"));
		List<MessageResource> all = dao.findAll();
		assertEquals(2, all.size());
	}

	@Test
	public void testDeleteMessage() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.GERMANY.toString())
				.type("type")
				.message("msg1"));
		List<MessageResource> all = dao.findAll();
		assertEquals(2, all.size());

		assertEquals(2, dao.deleteByCode("test1").longValue());
		assertEquals(0, dao.findAll().size());
	}

	@Test
	public void testDeleteWrongCode() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.GERMANY.toString())
				.type("type")
				.message("msg1"));
		List<MessageResource> all = dao.findAll();
		assertEquals(2, all.size());

		assertEquals(0, dao.deleteByCode("non_existent").longValue());
		assertEquals(2, dao.findAll().size());
	}

	@Test
	public void testGetAllMessages() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.GERMANY.toString())
				.type("type")
				.message("msg1"));
		List<MessageResource> all = dao.findAll();
		assertEquals(2, all.size());
	}

	@Test
	public void testGetResourceMessage() throws Exception {
		dao.save(new MessageResource()
				.code("test1")
				.lang(Locale.US.toString())
				.type("type")
				.message("msg1_us"));
		assertEquals("msg1_us", dao.findByCodeAndLang("test1", Locale.US.toString()).get(0).getMessage());
	}


	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(basePackageClasses = MessageSourceRepository.class)
	public static class Config {

		@Bean
		public PlatformTransactionManager transactionManager() {
			return new JpaTransactionManager();
		}

		@Bean
		public DataSource dataSource() {
			EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
			return builder.setType(EmbeddedDatabaseType.H2).build();
		}

		@Bean
		public FactoryBean<EntityManagerFactory> entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
			factory.setDataSource(dataSource());
			factory.setJpaVendorAdapter(jpaVendorAdapter());
			factory.setPackagesToScan("com.namics.oss.spring.support.i18n.dao.jpa");
			factory.setPersistenceUnitName("jpaPersistenceUnit");
			factory.setJpaProperties(jpaProperties());
			factory.setJpaDialect(new HibernateJpaDialect());

			return factory;
		}

        @Bean
        public EntityManager entityManager() throws Exception {
            return entityManagerFactory().getObject().createEntityManager();
        }


        @Bean
		public Properties jpaProperties() {
			Properties properties = new Properties();
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			properties.setProperty("hibernate.hbm2ddl.auto", "update");
			return properties;
		}

		@Bean
		public HibernateJpaVendorAdapter jpaVendorAdapter() {
			HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
			adapter.setShowSql(false);
			return adapter;
		}
	}
}
