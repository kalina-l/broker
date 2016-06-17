package rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jersey.repackaged.com.google.common.collect.Lists;
import model.Auction;
import model.Bid;
import model.Person;

@Path("/")
public class PersonService{
	
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");

	// See - http://www.logicbig.com/tutorials/java-ee-tutorial/jax-rs/put-example/
	@Context
    private UriInfo uriInfo;
	
	@GET
	@Path("/people")
	@Produces({"application/xml", "application/json"})
	public Response getPersons(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Person> query;			
		em.getTransaction().begin();
		try{
			try{
				//Limit Paramter
				int lowerNumber = 1;
				int upperNumber = 100;
				String queryAttr = "identity";
				//Query limited range
				//TODO - Named Queries Annotation in model?
				query  = em.createQuery("SELECT p FROM Person p WHERE "
						+ "(" + lowerNumber + " is null or p." + queryAttr + " >= " + lowerNumber + ") and"
						+ "(" + upperNumber + " is null or p." + queryAttr + " <= " + upperNumber + ")", 
						Person.class);
			}
			finally{
				if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
				}	
			}			
			
			//See - http://stackoverflow.com/questions/6081546/jersey-can-produce-listt-but-cannot-response-oklistt-build
			List<Person> list = query.getResultList();
			GenericEntity<List<Person>> entity = 
		            new GenericEntity<List<Person>>(Lists.newArrayList(list)) {};
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
	public Response getPersonByID(@PathParam("identity") long identity){
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
			return Response.ok(person).build();
		
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
				// TODO - Notwendig?
				if (b.getBidder() != null && b.getAuction() != null && b.getBidder().getIdentity() == person.getIdentity())
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
   public Response createPerson(Person p){
	   try{
		   Person person = new Person();
		   person = p;
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			em.getTransaction().begin();		
			em.persist(person);
			//em.merge(person);
			try{ // Start Commit --------------------
				em.getTransaction().commit();
			}finally{
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}	
			} // End Commit -------------------------

			//status code 201
            //sends back new URI in header key = 'LOCATION'
			return Response.created(uriInfo.getAbsolutePath()).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			// Duplicate entry mail or alias
			throw new ClientErrorException(CONFLICT);
		} 
	   
   }


}
