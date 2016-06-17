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
import model.Person;

@Path("/auctions")
public class AuctionService {

	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");
	
	// See - http://www.logicbig.com/tutorials/java-ee-tutorial/jax-rs/put-example/
	@Context
    private UriInfo uriInfo;
	
	@GET
	@Path("/")
	@Produces({"application/xml", "application/json"})
	public Response getAuctions(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Auction> query;			
		em.getTransaction().begin();
		try{
			try{
				//Limit Paramter
				String queryArg0 = "identity";
				long lowerLimit0 = 1;
				long upperLimit0 = 100;
				
				//Query limited range
				//TODO - Named Queries Annotation in model?
				query  = em.createQuery("SELECT x FROM Auction x WHERE "
						+ "(" + lowerLimit0 + " is null or x." + queryArg0 + " >= " + lowerLimit0 + ") and"
						+ "(" + upperLimit0 + " is null or x." + queryArg0 + " <= " + upperLimit0 + ")", 
						Auction.class);
			}
			finally{
				if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
				}	
			}				
			
			//See - http://stackoverflow.com/questions/6081546/jersey-can-produce-listt-but-cannot-response-oklistt-build
			List<Auction> list = query.getResultList();
			GenericEntity<List<Auction>> entity = 
		            new GenericEntity<List<Auction>>(Lists.newArrayList(list)) {};
		        return Response.ok(entity).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}
	
	
	@GET
	@Path("/{identity}")
	@Produces({"application/xml", "application/json"})
	public Response getAuctionByID(@PathParam("identity") long identity){
		try{
			EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
			Auction auction;
			em.getTransaction().begin();			
			auction = em.find(Auction.class, identity);
			try{
					em.getTransaction().commit();
			}finally{
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}	
				}
			return Response.ok(auction).build();
		
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}
	
	
	  @PUT
	   @Path("/")
	   @Consumes({"application/xml", "application/json"})
	   public Response createRandomAuction(){
		   try{
				EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
				em.getTransaction().begin();
				
				long sellerID = 7;
				Person seller =  em.find(Person.class, sellerID);
				
				Auction auction = new Auction(seller);
				auction.setTitle("super auction number "+randomIdentifier());
				auction.setDescription(randomIdentifier() + " buy super great thing, cheap and fun");
				auction.setAskingPrice(10);
				
				em.persist(auction);
					
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
				throw new ClientErrorException(CONFLICT);
			} 
		   
	   }
	
	   /*
	    * RANDOM NAME FUNCTION
	    * - todo seperate
	    */
	   
	//class variable
	final String lexicon = "abcdefghijklmnopqrstuvwxyz12345674890";
	final java.util.Random rand = new java.util.Random();
	//consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final Set<String> identifiers = new HashSet<String>();

	public String randomIdentifier() {
	 StringBuilder builder = new StringBuilder();
	 while(builder.toString().length() == 0) {
	     int length = rand.nextInt(5)+5;
	     for(int i = 0; i < length; i++)
	         builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	     if(identifiers.contains(builder.toString()))
	         builder = new StringBuilder();
	 }
	 return builder.toString();
	}
	
}
