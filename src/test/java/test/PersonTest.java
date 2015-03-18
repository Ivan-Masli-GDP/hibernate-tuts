/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;


import com.mycompany.hibernate.simple.persist.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import static org.junit.Assert.assertTrue;
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
                
                .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");
                
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
        factory = config.addPackage("com.mycompany.hibernate.simple.persist").addAnnotatedClass(Person.class).buildSessionFactory(serviceRegistry);
        
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
        assertTrue(findPerson(session, "Vilvredo").getName().equals("Vilvredo"));
        session.close();

        
    }
//    @Test
//    public void testSaveRanking(){
//        Session session = factory.openSession();
//        Transaction tx = session.beginTransaction();
//        Person subject = savePerson(session,"Vilvredo");
//        Person observer = savePerson(session,"Paretto");
//        Skill skill = saveSkill(session,"Java");
//    }
    public Person findPerson(Session session, String name) {

        Query query = session.createQuery("from Person p where p.name=:name");
        query.setParameter("name", name);
        Person person = (Person) query.uniqueResult();
        return person;
    }

    private Person savePerson(Session session, String name) {
        Person person = findPerson(session, name);
        if (person == null) {
            person = new Person();
            person.setName(name);
            session.save(person);
        }
        return person;
    }
}
