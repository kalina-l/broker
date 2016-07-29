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
import model.Auction.XmlBidsAsEntityFilter;
import model.Bid;
import model.Person;

@Path("/")
public class PersonService {

	static private final String QUERYSTRING = "SELECT p.identity FROM Person p WHERE "
			+ "(:creationTimeLowerLimit is null or p.creationTimeStamp >= :creationTimeLowerLimit) and"
			+ "(:creationTimeUpperLimit is null or p.creationTimeStamp <= :creationTimeUpperLimit) and"
			+ "(:alias is null or p.alias = :alias) and" + "(:givenName is null or p.name.given = :givenName) and"
			+ "(:familyName is null or p.name.family = :familyName) and"
			+ "(:street is null or p.address.street = :street) and"
			+ "(:postCode is null or p.address.postCode = :postCode) and"
			+ "(:city is null or p.address.city = :city) and" + "(:phone is null or p.contact.phone = :phone) and"
			+ "(:email is null or p.contact.email = :email)";

	@GET
	@Path("/people/requester")
	@Produces({ "application/xml", "application/json" })
	public Response getRequester(@HeaderParam("Authorization") final String authentication) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new ClientErrorException(401);
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// end authenticate

		return Response.ok(requester).build();
	}

	// object zurück geben wenn code 200
	// response wenn zb jpg oder png, oder relationen
	@GET
	@Path("/people")
	@Produces({ "application/xml", "application/json" })
	public Response getPersons(@HeaderParam("Authorization") final String authentication,
			@QueryParam("resultOffset") int resultOffset, @QueryParam("resultLength") int resultLength,
			@QueryParam("creationTimeLowerLimit") Long creationTimeLowerLimit,
			@QueryParam("creationTimeUpperLimit") Long creationTimeUpperLimit, @QueryParam("alias") String alias,
			@QueryParam("givenName") String givenName, @QueryParam("familyName") String familyName,
			@QueryParam("street") String street, @QueryParam("postCode") String postCode,
			@QueryParam("city") String city, @QueryParam("phone") String phone, @QueryParam("email") String email) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new ClientErrorException(401);
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			TypedQuery<Long> query;
			query = em.createQuery(QUERYSTRING, Long.class)
					.setParameter("creationTimeLowerLimit", creationTimeLowerLimit)
					.setParameter("creationTimeUpperLimit", creationTimeUpperLimit).setParameter("alias", alias)
					.setParameter("givenName", givenName).setParameter("familyName", familyName)
					.setParameter("street", street).setParameter("postCode", postCode).setParameter("city", city)
					.setParameter("phone", phone).setParameter("email", email);
			if (resultLength > 0)
				query.setMaxResults(resultLength);
			if (resultOffset > 0)
				query.setFirstResult(resultOffset);

			List<Long> idList = query.getResultList();
			List<Person> personList = new ArrayList<>();
			for (Long id : idList) {
				Person temp = em.find(Person.class, id);
				if (temp != null)
					personList.add(temp);
			}

			if (idList.isEmpty()) {
				throw new EntityNotFoundException();
			}

			GenericEntity<List<Person>> entity = new GenericEntity<List<Person>>(Lists.newArrayList(personList)) {
			};
			return Response.ok(entity).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/people/{identity}")
	@Produces({ "application/xml", "application/json" })
	public Person getPersonByID(@HeaderParam("Authorization") final String authentication,
			@NotNull @Min(1) @PathParam("identity") long identity) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new ClientErrorException(401);
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			Person person = null;
			person = em.find(Person.class, identity);

			if (person == null) {
				throw new EntityNotFoundException();
			}
			return person;

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/people/{identity}/auctions")
	@Produces({ "application/xml", "application/json" })
	@XmlBidsAsEntityFilter
	public Response getAuctionsByPersonID(@HeaderParam("Authorization") final String authentication,
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

			final Person person = em.find(Person.class, identity);
			Set<Auction> allAuctions = new HashSet<Auction>();
			allAuctions.addAll(person.getAuctions());

			Set<Bid> bids = person.getBids();
			// Get auctions with bidder reference
			for (Bid b : bids) {
				allAuctions.add(b.getAuction());
			}

			if (bids.isEmpty()) {
				throw new EntityNotFoundException();
			}

			GenericEntity<List<Auction>> wrapper = new GenericEntity<List<Auction>>(Lists.newArrayList(allAuctions)) {
			};
			Annotation[] filterAnnotations = new Annotation[] { new Auction.XmlSellerAsEntityFilter.Literal(), new Auction.XmlBidsAsEntityFilter.Literal()};
			return Response.ok().entity(wrapper, filterAnnotations).build();
		

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/people/{identity}/bids")
	@Produces({ "application/xml", "application/json" })
	@Bid.XmlBidderAsReferenceFilter
	@Bid.XmlAuctionAsReferenceFilter
	public Response getClosedBidsByPersonID(@HeaderParam("Authorization") final String authentication,
			@NotNull @Min(1) @PathParam("identity") long identity) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new ClientErrorException(401);
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// end authenticate

		try {
			EntityManager em = LifeCycleProvider.brokerManager();
			Person person = em.find(Person.class, identity);
			Set<Bid> closedBids = new HashSet<Bid>();
			Set<Bid> bids = person.getBids();
			for (Bid b : bids) {
				if (b.getAuction().isClosed())
					closedBids.add(b);
			}
			if (bids.isEmpty()) {
				throw new EntityNotFoundException();
			}

			GenericEntity<List<Bid>> wrapper = new GenericEntity<List<Bid>>(Lists.newArrayList(closedBids)) {
			};Annotation[] filterAnnotations = new Annotation[] { new Bid.XmlBidderAsReferenceFilter.Literal(), new Bid.XmlAuctionAsReferenceFilter.Literal()};
			return Response.ok().entity(wrapper, filterAnnotations).build();

		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			throw new InternalServerErrorException();
		}
	}

	@PUT
	@Path("/people")
	@Consumes({ "application/xml", "application/json" })
	public Response alterPerson(@HeaderParam("Authorization") final String authentication,
			@NotNull @Valid Person template) {

		// authenticate
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester == null) throw new ClientErrorException(401);
		if (requester.getGroup() != ADMIN && requester.getGroup() != USER) throw new ClientErrorException(403);
		// User changing data of others?
		boolean selfAltering = requester.getIdentity() == template.getIdentity();
		if (requester.getGroup() == USER && !selfAltering) throw new ClientErrorException(403);
		// end authenticate
		try {

			EntityManager em = LifeCycleProvider.brokerManager();

			boolean createmode = template.getIdentity() == 0;
			Person person = createmode ? new Person() : em.find(Person.class, template.getIdentity());

			if (person == null) {
				throw new EntityNotFoundException();
			}

			person.setAlias(template.getAlias());
			if (requester.getGroup() == USER && template.getGroup() == ADMIN) {
				throw new ClientErrorException(FORBIDDEN);
			} else {
				person.setGroup(template.getGroup());
			}
			person.getName().setFamily(template.getName().getFamily());
			person.getName().setGiven(template.getName().getGiven());
			person.getAddress().setStreet(template.getAddress().getStreet());
			person.getAddress().setPostCode(template.getAddress().getPostCode());
			person.getAddress().setCity(template.getAddress().getCity());
			person.getContact().setEmail(template.getContact().getEmail());
			person.getContact().setPhone(template.getContact().getPhone());

			if (createmode)
				em.persist(person);
			else
				em.flush();

			try { // Start Commit --------------------
				em.getTransaction().commit();
			} finally {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} // End Commit -------------------------

			return Response.ok(person.getIdentity()).build();
			
		} catch (Exception exception) {
			if (exception instanceof EntityNotFoundException)
				throw new ClientErrorException(404);
			if (exception instanceof ConstraintViolationException)
				throw new ClientErrorException(400);
			if (exception instanceof RollbackException)
				throw new ClientErrorException(409);
			if (exception instanceof IllegalArgumentException)
				throw new ClientErrorException(400);
			throw new InternalServerErrorException();
		}
	}
}
