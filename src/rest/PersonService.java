package rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;
import model.Auction;
import model.Bid;
import model.Person;

@Path("/")
public class PersonService{
	
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");

	// See - http://www.logicbig.com/tutorials/java-ee-tutorial/jax-rs/put-example/

	
	// object zurück geben wenn code 200
	// response wenn zb jpg oder png, oder relationen
	@GET
	@Path("/people")
	@Produces({"application/xml", "application/json"})
	public Response getPersons(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Long> query;		
		em.getTransaction().begin();
		try{
			try{
				//Limit Paramter
				int lowerNumber = 1;
				int upperNumber = 100;
				// nur id zurückgeben
				query = em.createQuery("SELECT p.identity FROM Person p WHERE "
						+ "(:lowerNumber is null or p.identity >= :lowerNumber) and"
						+ "(:upperNumber is null or p.identity <= :upperNumber)", 
						Long.class)
						.setParameter("lowerNumber", lowerNumber)
						.setParameter("upperNumber", upperNumber);			
			}
			finally{
				// Notweding bei GET ???
				if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
				}	
			}			
			
			List<Long> idList = query.getResultList();
			List<Person> personList = new ArrayList<>();
			for (Long id:  idList){
				Person temp = em.find(Person.class, id);
				if (temp != null)
						personList.add(temp);
			}
			
			GenericEntity<List<Person>> entity = 
		            new GenericEntity<List<Person>>(Lists.newArrayList(personList)) {};
		        return Response.ok(entity).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}
	
	
	
	@GET
	@Path("/people/{identity}")
	@Produces({"application/xml", "application/json"})
	public Person getPersonByID(@PathParam("identity") long identity){
		try{
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
			Person person;
			em.getTransaction().begin();			
			person = em.find(Person.class, identity);
			try{
					em.getTransaction().commit();
			}finally{
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}	
			}
			return person;
		
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}
	
	
	
	@GET
	@Path("/people/{identity}/auctions")
	@Produces({"application/xml", "application/json"})
	public Response getAuctionsByPersonID(@PathParam("identity") long identity){
		
		try{
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			em.getTransaction().begin();
			
			Person person = em.find(Person.class, identity);
			Set<Auction> auctions = person.getAuctions();
			Set<Bid> bids = person.getBids();
			// Get auctions with bidder reference
			for(Bid b : bids){
				auctions.add(b.getAuction());
			}
						
			try{ // Start Commit --------------------
				em.getTransaction().commit();
			}finally{
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}	
			} // End Commit -------------------------

			GenericEntity<List<Auction>> entity = 
		            new GenericEntity<List<Auction>>(Lists.newArrayList(auctions)) {};
		        return Response.ok(entity).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}

	
	
	@GET
	@Path("/people/{identity}/bids")
	@Produces({"application/xml", "application/json"})
	public Response getClosedBidsByPersonID(@PathParam("identity") long identity){
		
		try{
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			em.getTransaction().begin();
			
			Person person = em.find(Person.class, identity);
			Set<Bid> closedBids = new HashSet<Bid>();
			Set<Bid> bids = person.getBids();
			for(Bid b : bids){
				if (b.getAuction().isClosed())
					closedBids.add(b);
			}
				
			try{ // Start Commit --------------------
				em.getTransaction().commit();
			}finally{
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}	
			} // End Commit -------------------------

			GenericEntity<List<Bid>> entity = 
		            new GenericEntity<List<Bid>>(Lists.newArrayList(closedBids)) {};
		        return Response.ok(entity).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}
	
   @PUT
   @Path("/people")
   @Consumes({"application/xml", "application/json"})
   public Response alterPerson(Person template){
	   try{
		   
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			
			 boolean createmode = template.getIdentity() == 0;
			em.getTransaction().begin();		
			Person person =  createmode ? new Person() : em.find(Person.class, template.getIdentity());
			
			person.setAlias(template.getAlias());
			person.setGroup(template.getGroup());
			person.getName().setFamily(template.getName().getFamily());
			person.getName().setGiven(template.getName().getGiven());
			person.getAddress().setStreet(template.getAddress().getStreet());
			person.getAddress().setPostalCode(template.getAddress().getPostalCode());
			person.getAddress().setCity(template.getAddress().getCity());
			person.getContact().setEmail(template.getContact().getEmail());
			person.getContact().setPhone(template.getContact().getPhone());
			
			if (createmode)
				em.persist(person);
			else 
				em.flush();

			try{ // Start Commit --------------------
				em.getTransaction().commit();
			}finally{
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}	
			} // End Commit -------------------------


			return Response.ok(person.getIdentity()).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			// Duplicate entry mail or alias
			throw new ClientErrorException(CONFLICT);
		} 
	   
   }


}
