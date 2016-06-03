package rest;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.*;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;
import java.util.Set;

import model.*;

public class AuctionService {
	
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");;

	@GET
	@Path("/people/{identity}")
	@Produces({"application/xml", "application/json"})
	public Person getPerson(@PathParam("identity") long identity){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		Person person;
		
		em.getTransaction().begin();
		
		try{
			person = em.find(Person.class, identity);
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}	
		}
		return person;
	}
}
