/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hibernate.simple.persist;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ivan.masli
 */
public class PersonTest {

    SessionFactory factory;

    @Before
    public void setup() {
        Configuration config = new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:hsqldb:db2;shutdown=true")
                .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbc.JDBCDriver")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", null)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
        factory = config.buildSessionFactory(serviceRegistry);
    }

    @After
    public void shutdown() {
        factory.close();
    }

    @Test
    public void testSavePerson() {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        Person person = new Person();
        person.setName("Vilvredo");
        session.save(person);
        tx.commit();
        session.close();
    }
}
