package test;
import model.*;
import org.junit.*;


import java.util.Set;
import javax.persistence.*;
import javax.validation.*;

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
		
		Auction auction = new Auction(seller);
		auction.setTitle("super auction");
		auction.setDescription("buy super great thing, cheap and fun");
		auction.setAskingPrice(10);

		
		Bid bid = new Bid(auction, bidder);
		bid.setPrice(15);
		
//		em.persist(seller);
//		em.persist(bidder);
//		em.persist(auction);
		em.persist(bid);
		
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}	
			this.getWasteBasket().add(seller.getIdentity());
			this.getWasteBasket().add(bidder.getIdentity());
			this.getWasteBasket().add(auction.getIdentity());
			this.getWasteBasket().add(bid.getIdentity());
		}
		
		em.close();
	}

}
