/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.hibernate.simple.persist.Person;
import com.mycompany.hibernate.simple.persist.Ranking;
import com.mycompany.hibernate.simple.persist.Skill;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ivan.masli
 */
public class TestSuite {

    SessionFactory factory;

    public Person findPerson(Session session, String name) {

        Query query = session.createQuery("from Person p where p.name=:name");
        query.setParameter("name", name);
        Person person = (Person) query.uniqueResult();
        return person;
    }

    public Skill findSkill(Session session, String name) {

        Query query = session.createQuery("from Skill s where s.name=:name");
        query.setParameter("name", name);
        Skill skill = (Skill) query.uniqueResult();
        return skill;
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
        factory = config
                .addPackage("com.mycompany.hibernate.simple.persist")
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Ranking.class)
                .addAnnotatedClass(Skill.class)
                .buildSessionFactory(serviceRegistry);
    }

    @After
    public void shutdown() {
        factory.close();
    }
    
    public void createData(Session session, String observer, String subject, String skill, Integer ranking) {

        Transaction tx = session.getTransaction();
        Person subjectPerson = savePerson(session, subject);
        Person observerPerson = savePerson(session, observer);
        Skill skillPerson = saveSkill(session, skill);
        Ranking rankPerson = new Ranking();
        rankPerson.setObserver(observerPerson);
        rankPerson.setSubject(subjectPerson);
        rankPerson.setSkill(skillPerson);
        rankPerson.setRanking(ranking);
        session.save(rankPerson);
        tx.commit();
        
    }
    @Test
    public void populateRankingData() {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        createData(session, "J. C. Smell", "Gene Showrama", "Java", 6);
        createData(session, "J. C. Smell", "Scottball Most", "Java", 7);
        createData(session, "J. C. Smell", "Drew Lombardo", "Java", 8);
        tx.commit();
        session.close();
    }

    private Skill saveSkill(Session session, String name) {
        Skill skill = findSkill(session, name);
        if (skill == null) {
            skill = new Skill();
            skill.setName(name);
            session.save(skill);
        }
        return skill;
    }

   
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
    
    public void removePerson(){
        populateRankingData();
        Session removeSession = factory.openSession();
        Transaction tx = removeSession.beginTransaction();
        Person person = findPerson(removeSession, "Drew Lombardo");
        removeSession.delete(person);
        tx.commit();
        assertNull(findPerson(removeSession,"Drew Lombardo"));
        removeSession.close();
    }
}
