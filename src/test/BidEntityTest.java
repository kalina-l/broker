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
		Person bidder = new Person();
		Auction auction = new Auction(seller);
		Bid bid = new Bid(auction, bidder);
		em.persist(bid);
		em.getTransaction().commit();
		
		em.close();
	}

}
