package rest;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static model.Group.ADMIN;
import static model.Group.USER;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jersey.repackaged.com.google.common.collect.Lists;
import model.Auction;
import model.Bid;
import model.Person;

@Path("/")
public class AuctionService {

	static private final String QUERYSTRING = "SELECT x.identity FROM Auction x WHERE "
			+ "(:creationTimeLowerLimit is null or x.creationTimeStamp >= :creationTimeLowerLimit) and"
			+ "(:creationTimeUpperLimit is null or x.creationTimeStamp <= :creationTimeUpperLimit) and"
			+ "(:title is null or x.title = :title) and" + "(:description is null or x.description = :description) and"
			+ "(:unitCount is null or x.unitCount = :unitCount) and"
			+ "(:askingPrice is null or x.askingPrice = :askingPrice) and"
			+ "(:closureTimeLowerLimit is null or x.closureTimeStamp >= :closureTimeLowerLimit) and"
			+ "(:closureTimeUpperLimit is null or x.closureTimeStamp <= :closureTimeUpperLimit)";

	@GET
	@Path("/auctions")
	@Produces({ "application/xml", "application/json" })
	@Auction.XmlSellerAsEntityFilter
	public Response getAuctions(@HeaderParam("Authorization") final String authentication,
			@QueryParam("closed") Boolean closed,
			@QueryParam("sealed") Boolean sealed,
			@QueryParam("resultLength") int resultLength, @QueryParam("resultOffset") int resultOffset,
			@QueryParam("creationTimeLowerLimit") Long creationTimeLowerLimit,
			@QueryParam("creationTimeUpperLimit") Long creationTimeUpperLimit,
			@QueryParam("closureTimeLowerLimit") Long closureTimeLowerLimit,
			@QueryParam("closureTimeUpperLimit") Long closureTimeUpperLimit, @QueryParam("title") String title,
			@QueryParam("description") String description, @QueryParam("unitCount") Long unitCount,
			@QueryParam("askingPrice") String askingPrice) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null)throw new NotAuthorizedException("You need to log in.");
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			TypedQuery<Long> query;

			query = em.createQuery(QUERYSTRING, Long.class)
					.setParameter("creationTimeLowerLimit", creationTimeLowerLimit)
					.setParameter("creationTimeUpperLimit", creationTimeUpperLimit).setParameter("title", title)
					.setParameter("description", description).setParameter("unitCount", unitCount)
					.setParameter("askingPrice", askingPrice)
					.setParameter("closureTimeLowerLimit", closureTimeLowerLimit)
					.setParameter("closureTimeUpperLimit", closureTimeUpperLimit);
			if (resultLength > 0)
				query.setMaxResults(resultLength);
			if (resultOffset > 0)
				query.setFirstResult(resultOffset);

			List<Long> idList = query.getResultList();
			List<Auction> auctionList = new ArrayList<>();
		
			for (Long id : idList) {
				Auction temp = em.find(Auction.class, id);
				if(closed != null){
					if (temp != null && temp.isClosed() == closed )
						auctionList.add(temp);
				} else if (temp != null)
					auctionList.add(temp);
			}

			if (idList.isEmpty()) {
				throw new EntityNotFoundException();
			}
			

			GenericEntity<List<Auction>> wrapper = new GenericEntity<List<Auction>>(Lists.newArrayList(auctionList)) {};
			Annotation[] filterAnnotations = new Annotation[] { new Auction.XmlSellerAsEntityFilter.Literal()};
			return Response.ok().entity(wrapper, filterAnnotations).build();

		}
		catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);	
			if (exception instanceof NotAuthorizedException)
				throw new ClientErrorException(401);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/auctions/{identity}")
	@Produces({ "application/xml", "application/json" })
	@Auction.XmlSellerAsReferenceFilter
	public Response getAuctionByID(@HeaderParam("Authorization") final String authentication,
			@NotNull @Min(1) @PathParam("identity") long identity) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new NotAuthorizedException("You need to log in.");
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ForbiddenException();
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			Auction auction = null;
			auction = em.find(Auction.class, identity);

			if (auction == null)
				throw new EntityNotFoundException();
			
			 Annotation[] filterAnnotations = new Annotation[] { new Auction.XmlSellerAsReferenceFilter.Literal()}; 
			return Response.ok().entity(auction, filterAnnotations).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				
			if (exception instanceof ForbiddenException)
				throw new ClientErrorException(403);
			if (exception instanceof NotAuthorizedException)
				throw new ClientErrorException(401);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/auctions/{identity}/bids")
	@Produces({ "application/xml", "application/json" })
	@Bid.XmlBidderAsReferenceFilter
	@Bid.XmlAuctionAsReferenceFilter
	public Response getBidsByAuctionID(@HeaderParam("Authorization") final String authentication,
			@NotNull @Min(1) @PathParam("identity") long identity) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null)
			throw new NotAuthorizedException("You need to log in.");
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER)
			throw new ForbiddenException();
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			Auction auction = em.find(Auction.class, identity);
			Set<Bid> requesterBids = new HashSet<Bid>();
			Set<Bid> bids = auction.getBids();
			for (Bid b : bids) {
				if (b.getBidderReference() == requester.getIdentity())
					requesterBids.add(b);
			}
			if (bids.isEmpty()) {
				throw new EntityNotFoundException();
			}

			
			GenericEntity<List<Bid>> wrapper = new GenericEntity<List<Bid>>(Lists.newArrayList(requesterBids)) {};
			Annotation[] filterAnnotations = new Annotation[] { new Bid.XmlBidderAsReferenceFilter.Literal(), new Bid.XmlAuctionAsReferenceFilter.Literal()};
			return Response.ok().entity(wrapper, filterAnnotations).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			if (exception instanceof ForbiddenException)
				throw new ClientErrorException(403);
			if (exception instanceof NotAuthorizedException)
				throw new ClientErrorException(401);
			throw new InternalServerErrorException();
		}
	}

	@PUT
	@Path("/auctions")
	@Consumes({ "application/xml", "application/json" })
	public Response alterAuction(@HeaderParam("Authorization") final String authentication,
			@NotNull @Valid Auction template, @QueryParam("sellerID") Long sellerID) {

		// Authentification
		// Logged in?
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null)
			throw new NotAuthorizedException("You need to log in.");
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER)
			throw new ForbiddenException();
		
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();

			boolean createmode = template.getIdentity() == 0;

			long id = (sellerID != null) ? sellerID : 1;
			Person p = em.find(Person.class, id);

			if (p == null)
				throw new EntityNotFoundException("Seller not found!");

			Auction auction = createmode ? new Auction(p) : em.find(Auction.class, template.getIdentity());

			auction.setTitle(template.getTitle());
			auction.setDescription(template.getDescription());
			auction.setUnitCount(template.getUnitCount());
			auction.setAskingPrice(template.getAskingPrice());
			
			// User changing data of others?
			if (requester.getGroup() == USER && !(requester.getIdentity() == template.getSeller().getIdentity()))
				throw new ClientErrorException(FORBIDDEN);
			
			if (createmode)
				em.persist(auction);
			else
				em.flush();

			try { // Start Commit --------------------
				em.getTransaction().commit();
			} finally {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} // End Commit -------------------------

			return Response.ok(auction.getIdentity()).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof ConstraintViolationException)
				throw new ClientErrorException(400);
			if (exception instanceof RollbackException)
				throw new ClientErrorException(409);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			if (exception instanceof ClientErrorException)
				throw new ClientErrorException(403);
			if (exception instanceof ForbiddenException)
				throw new ClientErrorException(403);
			if (exception instanceof NotAuthorizedException)
				throw new ClientErrorException(401);
			throw new InternalServerErrorException();
		}

	}

	@POST
	@Path("/auctions/{identity}/bids")
	@Consumes({ "application/xml", "application/json" })
	public Response alterRequesterBid(@HeaderParam("Authorization") final String authentication,
			@NotNull @Min(1) @PathParam("identity") long identity, @NotNull @Valid Bid template) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null)
			throw new NotAuthorizedException("You need to log in.");
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER)
			throw new ForbiddenException();
		// end authenticate

		Bid bid = null;
		try {

			EntityManager em = LifeCycleProvider.brokerManager();
			Auction auction = em.find(Auction.class, identity);
			boolean createmode = template.getIdentity() == 0;


			bid = createmode ? new Bid(auction, requester) : em.find(Bid.class, template.getIdentity());
			bid.setPrice(template.getPrice());
			

			if (template.getPrice() == 0){
				if(!createmode)
					em.remove(bid);
				else
					throw new IllegalArgumentException();
			}
			else{
				if (createmode)
					em.persist(bid);
				else
					em.flush();
			}
			try { // Start Commit --------------------
				em.getTransaction().commit();
			} finally {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} // End Commit -------------------------

			return Response.ok(bid.getIdentity()).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof ConstraintViolationException)
				//auction.askingprice should be smaller than bit price
				throw new ClientErrorException(401);
			if (exception instanceof RollbackException)
				throw new ClientErrorException(409);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			if (exception instanceof ForbiddenException)
				throw new ClientErrorException(403);
			if (exception instanceof NotAuthorizedException)
				throw new ClientErrorException(401);
			throw new InternalServerErrorException();
		}

	}
}
