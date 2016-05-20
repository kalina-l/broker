package test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import model.Address;
import model.Auction;
import model.Bid;
import model.Contact;
import model.Name;
import model.Person;

public class PersonEntityTest extends EntityTest{

	@Test
	public void testConstraints(){
		Validator v = this.getEntityValidatorFactory().getValidator();
		Person person = new Person();
		
		// password hash
		person.setPasswordHash(new byte[32]);
		Assert.assertEquals(0, v.validate(person).size());
		person.setPasswordHash(new byte[31]);
		Assert.assertEquals(1, v.validate(person).size());
		
		// name - family
		person.getName().setFamily("Maier");
		Assert.assertEquals(0, v.validate(person).size());
		person.getName().setFamily("");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setFamily("Maierowskimaierowskimaierowskimaierowski");
		Assert.assertEquals(1, v.validate(person).size());
		
		// name - given
		person.getName().setGiven("Annika");
		Assert.assertEquals(0, v.validate(person).size());
		person.getName().setGiven("");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setGiven("Annikaannikaannikaannikaannikaannika");
		Assert.assertEquals(1, v.validate(person).size());
		
		// address - street
		person.getAddress().setStreet("Warschauerstr.");
		Assert.assertEquals(0, v.validate(person).size());
		
		// address - postCode
		person.getAddress().setPostalCode("10429");
		Assert.assertEquals(0, v.validate(person).size());
	}
	
	@Test
	public void testLifeCycle(){
		
		
		EntityManagerFactory  emf = super.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		Person person =  new Person();
		person.setAlias("aliasTest");
		person.getName().setGiven("Troy");
		person.getName().setFamily("Testa");
		person.getAddress().setCity("Hamburg");
		person.getAddress().setStreet("Testallee 13");
		person.getAddress().setPostalCode("12345");
		person.getContact().setEmail("testa@test.com");
		person.getContact().setPhone("012345678");
		
		em.persist(person);
		em.getTransaction().commit();
		
		this.getWasteBasket().add(person.getIdentity());

		em.close();
	}
	
}
