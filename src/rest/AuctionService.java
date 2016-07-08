package rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.*;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
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

	@GET
	@Path("/auctions")
	@Produces({ "application/xml", "application/json" })
	public Response getAuctions(@QueryParam("creationTimeLowerLimit") Long creationTimeLowerLimit,
			@QueryParam("creationTimeUpperLimit") Long creationTimeUpperLimit,
			@QueryParam("closureTimeLowerLimit") Long closureTimeLowerLimit,
			@QueryParam("closureTimeUpperLimit") Long closureTimeUpperLimit, @QueryParam("title") String title,
			@QueryParam("description") String description, @QueryParam("unitCount") Long unitCount,
			@QueryParam("askingPrice") Long askingPrice) {

		EntityManager em = LifeCycleProvider.brokerManager();
		TypedQuery<Long> query;
		try {
			query = em
					.createQuery(
							"SELECT x.identity FROM Auction x WHERE "
									+ "(:creationTimeLowerLimit is null or x.creationTimeStamp >= :creationTimeLowerLimit) and"
									+ "(:creationTimeUpperLimit is null or x.creationTimeStamp <= :creationTimeUpperLimit) and"
									+ "(:title is null or x.title = :title) and"
									+ "(:description is null or x.description = :description) and"
									+ "(:unitCount is null or x.unitCount = :unitCount) and"
									+ "(:askingPrice is null or x.askingPrice = :askingPrice) and"
									+ "(:closureTimeLowerLimit is null or x.closureTimeStamp >= :closureTimeLowerLimit) and"
									+ "(:closureTimeUpperLimit is null or x.closureTimeStamp <= :closureTimeUpperLimit)",
							Long.class)
					.setParameter("creationTimeLowerLimit", creationTimeLowerLimit)
					.setParameter("creationTimeUpperLimit", creationTimeUpperLimit).setParameter("title", title)
					.setParameter("description", description).setParameter("unitCount", unitCount)
					.setParameter("askingPrice", askingPrice)
					.setParameter("closureTimeLowerLimit", closureTimeLowerLimit)
					.setParameter("closureTimeUpperLimit", closureTimeUpperLimit);

			List<Long> idList = query.getResultList();
			List<Auction> auctionList = new ArrayList<>();
			for (Long id : idList) {
				Auction temp = em.find(Auction.class, id);
				if (temp != null)
					auctionList.add(temp);
			}

			if(idList.isEmpty()){
				throw new EntityNotFoundException();
			}
			
			GenericEntity<List<Auction>> entity = new GenericEntity<List<Auction>>(Lists.newArrayList(auctionList)) {
			};
			return Response.ok(entity).build();

		} catch (Exception exception) {
			if(exception instanceof EntityExistsException) throw new ClientErrorException(NOT_FOUND);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/auctions/{identity}")
	@Produces({ "application/xml", "application/json" })
	public Auction getAuctionByID(@NotNull @Min(1) @PathParam("identity") long identity) {
		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			Auction auction;
			auction = em.find(Auction.class, identity);
			return auction;

		} catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}

	@PUT
	@Path("/auctions")
	@Consumes({ "application/xml", "application/json" })
	public Response alterAuction(Auction template, @QueryParam("sellerID") Long sellerID) {
		try {

			EntityManager em = LifeCycleProvider.brokerManager();

			boolean createmode = template.getIdentity() == 0;

			// woher bekommen wir die seller ID?
			long id = (sellerID != null) ? sellerID : 1;
			Person p = em.find(Person.class, id);

			Auction auction = createmode ? new Auction(p) : em.find(Auction.class, template.getIdentity());

			auction.setTitle(template.getTitle());
			auction.setDescription(template.getDescription());
			auction.setUnitCount(template.getUnitCount());
			auction.setAskingPrice(template.getAskingPrice());

			if (createmode)
				em.persist(auction);
			else
				em.flush();

			try { // Start Commit --------------------
				em.getTransaction().commit();
				em.getTransaction().begin();
			} finally {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} // End Commit -------------------------

			return Response.ok(auction.getIdentity()).build();

			// TODO Errorhandling
		} catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}

	}

}
