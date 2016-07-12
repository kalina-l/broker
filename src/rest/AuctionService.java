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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;
import model.Auction;
import model.Person;

@Path("/")
public class AuctionService {

	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");
	static private final String QUERYSTRING = "SELECT x.identity FROM Auction x WHERE "
			+ "(:creationTimeLowerLimit is null or x.creationTimeStamp >= :creationTimeLowerLimit) and"
			+ "(:creationTimeUpperLimit is null or x.creationTimeStamp <= :creationTimeUpperLimit) and"
			+ "(:title is null or x.title = :title) and"
			+ "(:description is null or x.description = :description) and"
			+ "(:unitCount is null or x.unitCount = :unitCount) and"
			+ "(:askingPrice is null or x.askingPrice = :askingPrice) and"
			+ "(:closureTimeLowerLimit is null or x.closureTimeStamp >= :closureTimeLowerLimit) and"
			+ "(:closureTimeUpperLimit is null or x.closureTimeStamp <= :closureTimeUpperLimit)";
	@GET
	@Path("/auctions")
	@Produces({"application/xml", "application/json"})
	public Response getAuctions(
			@QueryParam("resultLength") int resultLength,
			@QueryParam("resultOffset") int resultOffset,
			@QueryParam("creationTimeLowerLimit") Long creationTimeLowerLimit,
			@QueryParam("creationTimeUpperLimit") Long creationTimeUpperLimit,
			@QueryParam("closureTimeLowerLimit") Long closureTimeLowerLimit,
			@QueryParam("closureTimeUpperLimit") Long closureTimeUpperLimit,
			@QueryParam("title") String title,
			@QueryParam("description") String description,
			@QueryParam("unitCount") Long unitCount,
			@QueryParam("askingPrice") String askingPrice){
		
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		TypedQuery<Long> query;			
		em.getTransaction().begin();
		try{
			try{
				query = em.createQuery(QUERYSTRING,	Long.class)
							.setParameter("creationTimeLowerLimit", creationTimeLowerLimit)
							.setParameter("creationTimeUpperLimit", creationTimeUpperLimit)
							.setParameter("title", title)
							.setParameter("description", description)
							.setParameter("unitCount", unitCount)
							.setParameter("askingPrice", askingPrice)
							.setParameter("closureTimeLowerLimit", closureTimeLowerLimit)
							.setParameter("closureTimeUpperLimit", closureTimeUpperLimit);	
				if (resultLength > 0) query.setMaxResults(resultLength);
				if (resultOffset > 0) query.setFirstResult(resultOffset);
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
//			try{
//					em.getTransaction().commit();
//			}finally{
//					if (em.getTransaction().isActive()) {
//						em.getTransaction().rollback();
//					}	
//				}
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
   public Response alterAuction(Auction template,
		   @QueryParam("sellerID") Long sellerID){
	  try{
		  
		   EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();	
			
		   boolean createmode = template.getIdentity() == 0;
		   
		   em.getTransaction().begin();

		    Auction auction;	    
		    if (createmode){
		    	if(sellerID == null) throw new NullPointerException();
		    	Person p = em.find(Person.class, sellerID);
		    	auction = new Auction(p);
		    }else{
		    	auction = em.find(Auction.class, template.getIdentity());
		    }

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
