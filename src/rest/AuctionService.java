package rest;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.*;

@Path("/auctions")
public class AuctionService {

	static private EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("broker");
	
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
	
	
}
