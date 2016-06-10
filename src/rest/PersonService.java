package rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static model.Group.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;
import model.Person;

@Path("/people")
public class PersonService{
	
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");

	@GET
	@Path("/")
	@Produces({"application/xml", "application/json"})
	public Response getPerson(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Person> query;			
		em.getTransaction().begin();
		try{
			try{
				query  = em.createQuery("SELECT p FROM Person p", Person.class);
			}
			finally{
				if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
				}	
			}			
			// See - http://stackoverflow.com/questions/6081546/jersey-can-produce-listt-but-cannot-response-oklistt-build
			List<Person> list = query.getResultList();
			GenericEntity<List<Person>> entity = 
		            new GenericEntity<List<Person>>(Lists.newArrayList(list)) {};
		        return Response.ok(entity).build();
			
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}
	
	
	
	@GET
	@Path("/{identity}")
	@Produces({"application/xml", "application/json"})
	public Response getPersonByID(@PathParam("identity") long identity){
		try{
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
			return Response.ok(person).build();
		
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}
	
	
//	@PUT
//	@Path("/people")
//	@Produces({"application/xml", "application/json"})
//	public void putPerson() {
//		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
//		Person person;
//		 	
//		em.getTransaction().begin();
//		
//		person =  new Person();
//		person.setAlias("aliasTest");
//		person.getName().setGiven("Troy");
//		person.getName().setFamily("Testa");
//		person.getAddress().setCity("Hamburg");
//		person.getAddress().setStreet("Testallee 13");
//		person.getAddress().setPostalCode("12345");
//		person.getContact().setEmail("testa@test.com");
//		person.getContact().setPhone("012345678");
//		
//		em.persist(person);
//
//		try{
//			em.getTransaction().commit();
//		}
//		finally{
//			if (em.getTransaction().isActive()) {
//				em.getTransaction().rollback();
//			}
//			
//		}
//	 
//	
//	}

}
