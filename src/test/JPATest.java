package test;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPATest {


	public static void main(String[] args) {
		

	        EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker");
	 
	        EntityManager em = emf.createEntityManager();
	 

	        em.getTransaction().begin();
	        // The Merge method will do a select followed by an insert
	        // while the persist method will make always an insert statement
	 
	        em.getTransaction().commit();
	     // em.close();
	
	}
}
