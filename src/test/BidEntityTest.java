package test;
import model.*;
import org.junit.*;
import javax.persistence.*;
import javax.validation.Validator;

public class BidEntityTest extends EntityTest{

	@Test
	public void testConstraints(){
		Validator v = this.getEntityValidatorFactory().getValidator();
		Person seller = new Person();
		Person bidder = new Person();
		Auction auction = new Auction(seller);
		Bid bid = new Bid(auction, bidder);
		
		// PRICE
		bid.setPrice(0);
		Assert.assertEquals(0, v.validate(bid).size());
		bid.setPrice(-1);
		Assert.assertEquals(1, v.validate(bid).size());
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
