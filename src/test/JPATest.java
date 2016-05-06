package test;


import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JPATest {


	public static void main(String[] args) {
		
		Properties pros = new Properties();

		pros.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "/META-INF/persistence.xml");
	 
	        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker", pros);
	 
	        EntityManager em = emf.createEntityManager();
	 
	        JPATest test = new JPATest();
	 
	        em.getTransaction().begin();
	        // The Merge method will do a select followed by an insert
	        // while the persist method will make always an insert statement
	        em.merge(test);
	        em.getTransaction().commit();
	     // em.close();
	
	}
}
