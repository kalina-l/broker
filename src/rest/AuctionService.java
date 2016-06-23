package rest;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;

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
import model.Person;

@Path("/")
public class AuctionService {

	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");
	
	@GET
	@Path("/auctions")
	@Produces({"application/xml", "application/json"})
	public Response getAuctions(){
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Long> query;			
		em.getTransaction().begin();
		try{
			try{
				//Limit Paramter
				int lowerLimit = 1;
				int upperLimit = 100;
				
				//Query limited range
				//TODO - Named Queries Annotation in model?
				query = em.createQuery("SELECT x.identity FROM Auction x WHERE "
							+ "(:lowerNumber is null or x.identity >= :lowerNumber) and"
							+ "(:upperNumber is null or x.identity <= :upperNumber)", 
							Long.class)
							.setParameter("lowerNumber", lowerLimit)
							.setParameter("upperNumber", upperLimit);		
			}
			finally{
				if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
				}	
			}				
			List<Long> idList = query.getResultList();
			
			List<Auction> auctionList = new ArrayList<>();
			for (Long id:  idList){
				Auction temp = em.find(Auction.class, id);
				if (temp != null)
					auctionList.add(temp);
			}
			
			GenericEntity<List<Auction>> entity = 
		            new GenericEntity<List<Auction>>(Lists.newArrayList(auctionList)) {};
		        return Response.ok(entity).build();
			
		        
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	}
	
	
	@GET
	@Path("/auctions/{identity}")
	@Produces({"application/xml", "application/json"})
	public Auction getAuctionByID(@PathParam("identity") long identity){
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
			return auction;
		
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}

	
   @PUT
   @Path("/auctions")
   @Consumes({"application/xml", "application/json"})
   public Response alterAuction(Auction template){
	  try{

		   EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			
		   boolean createmode = template.getIdentity() == 0;
		   
		   em.getTransaction().begin();
		    //woher bekommen wir die seller ID?
		    long tempID = 1;
		    Person p = em.find(Person.class, tempID);
		    
			Auction auction = createmode ? new Auction(p) : em.find(Auction.class, template.getIdentity());	
			
			auction.setTitle(template.getTitle());
			auction.setDescription(template.getDescription());
			auction.setUnitCount(template.getUnitCount());
			auction.setAskingPrice(template.getAskingPrice());
			
			if(createmode)
				em.persist(auction);
			else 
				em.flush();
			
			try{ // Start Commit --------------------
				em.getTransaction().commit();
			}finally{
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}	
			} // End Commit -------------------------

			return Response.ok(auction.getIdentity()).build();
			
		// TODO Errorhandling
		}catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		} 
	   
   }

}
