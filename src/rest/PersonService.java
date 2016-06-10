package rest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import model.Person;

@Path("people")
public class PersonService{
	
	
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");

	@GET
	@Path("/")
	@Produces({"application/xml", "application/json"})
	public List<Person> getPerson(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Person> query;			
		em.getTransaction().begin();
 
		try{
			query  = em.createNamedQuery("Person.findAll", Person.class);
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}	
		}
        return query.getResultList();
	}
	
	
	@GET
	@Path("/{identity}")
	@Produces({"application/xml", "application/json"})
	public Response getPersonByID(@PathParam("identity") long identity){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		Person person;
		
		em.getTransaction().begin();
		
		try{
			person = (Person)em.find(Person.class, identity);
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}	
		}
		 
		return Response.ok(person).build();
	}
	
	
	@PUT
	@Path("/people")
	@Produces({"application/xml", "application/json"})
	public void putPerson() {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		Person person;
		 	
		em.getTransaction().begin();
		
		person =  new Person();
		person.setAlias("aliasTest");
		person.getName().setGiven("Troy");
		person.getName().setFamily("Testa");
		person.getAddress().setCity("Hamburg");
		person.getAddress().setStreet("Testallee 13");
		person.getAddress().setPostalCode("12345");
		person.getContact().setEmail("testa@test.com");
		person.getContact().setPhone("012345678");
		
		em.persist(person);

		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	 
	
	}

}
