package test;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import model.Auction;
import model.Bid;
import model.Person;

public class BidEntityTest extends EntityTest{

	@Test
	public void testConstraints(){

		
		Validator v = this.getEntityValidatorFactory().getValidator();
		Person seller = new Person();
		Person bidder = new Person();
		Auction auction = new Auction(seller);
		Bid bid = new Bid(auction, bidder);
		
		// PRICE
		auction.setAskingPrice(0);
		bid.setPrice(0);
		Assert.assertEquals(1, v.validate(bid).size()); // 1: cause of same IDs of bidder and seller
		
		auction.setAskingPrice(10);
		bid.setPrice(5);
		Assert.assertEquals(2, v.validate(bid).size()); // 1: cause of same IDs of bidder and seller
														// 2: bid smaller than asking price

		bid.setPrice(-1);
		Assert.assertEquals(3, v.validate(bid).size()); // 1: cause of same IDs of bidder and seller
														// 2: bid smaller than asking price
														// 3: bid smaller than 0
	}
	@Test
	public void testLifeCycle(){
		EntityManagerFactory  emf = super.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		/*
		 * Create bid ressources. 
		 */
		
		em.getTransaction().begin();
		
		Person seller = new Person();
		seller.setAlias("aliasSeller");
		seller.getName().setGiven("Troy");
		seller.getName().setFamily("Testa");
		seller.getAddress().setCity("Hamburg");
		seller.getAddress().setStreet("Testallee 13");
		seller.getAddress().setPostalCode("12345");
		seller.getContact().setEmail("testa@test.com");
		seller.getContact().setPhone("012345678");
		
		Person bidder = new Person();
		bidder.setAlias("aliasBidder");
		bidder.getName().setGiven("Sara");
		bidder.getName().setFamily("Bauer");
		bidder.getAddress().setCity("Stuttgart");
		bidder.getAddress().setStreet("Siegerstr. 1");
		bidder.getAddress().setPostalCode("54321");
		bidder.getContact().setEmail("testaaa@test.com");
		bidder.getContact().setPhone("98765421");
		
		em.persist(seller);
		em.persist(bidder);
		
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}	
			this.getWasteBasket().add(seller.getIdentity());
			this.getWasteBasket().add(bidder.getIdentity());
		}
		
		/*
		 * Create auction. 
		 */
		
		em.getTransaction().begin();
		Auction auction = new Auction(seller);
		auction.setTitle("super auction");
		auction.setDescription("buy super great thing, cheap and fun");
		auction.setAskingPrice(10);
		em.persist(auction);
		
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			this.getWasteBasket().add(auction.getIdentity());
		}
		
		/*
		 * Create bid. 
		 */
		
		em.getTransaction().begin();
		
		Bid bid = new Bid(auction, bidder);
		bid.setPrice(15);
		em.persist(bid);
		
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			this.getWasteBasket().add(bid.getIdentity());
		}
		
		/*
		 * Update bid.
		 */
		em.getTransaction().begin();
		
		bid = em.find(Bid.class, bid.getIdentity());

		bid.setPrice(20);
		
		em.persist(bid);
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
		Assert.assertEquals(20, bid.getPrice());
		 
	}

}
