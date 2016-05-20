package test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import model.Person;

public class PersonEntityTest extends EntityTest{

	@Test
	public void testConstraints(){
		Validator v = this.getEntityValidatorFactory().getValidator();
		Person person = new Person();
		// all correct values
		person.setAlias("shoppingqueen");
		person.getName().setFamily("Maier");
		person.getName().setGiven("Annika");
		person.getAddress().setStreet("Warschauerstr.");
		person.getAddress().setPostalCode("10429");
		person.getAddress().setCity("Berlin");
		person.getContact().setEmail("annika.maier@gmail.com");
		person.getContact().setPhone("00375934639");
		person.setPasswordHash(new byte[32]);
		
		Assert.assertEquals(0, v.validate(person).size());
		
		// incorrect password hash
		person.setPasswordHash(new byte[31]);
		Assert.assertEquals(1, v.validate(person).size());
		person.setPasswordHash(new byte[32]);
		
		// incorrect name - family
		person.getName().setFamily("");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setFamily("Maierowskimaierowskimaierowskimaierowski");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setFamily("Maier");
		
		// incorrect name - given
		person.getName().setGiven("");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setGiven("Annikaannikaannikaannikaannikaannika");
		Assert.assertEquals(1, v.validate(person).size());
		person.getName().setGiven("Annika");
		
		// incorrect address - street
		person.getAddress().setStreet("WarschauerstrWarschauerstrWarschauerstrWarschauerstrWarschauerstr.");
		Assert.assertEquals(1, v.validate(person).size());
		person.getAddress().setStreet("Warschauerstr.");
		
		// incorrect address - postCode
		person.getAddress().setPostalCode("1042910429104291");
		Assert.assertEquals(1, v.validate(person).size());
		person.getAddress().setPostalCode("10429");
		
		// incorrect addres - city
		person.getAddress().setCity("");
		Assert.assertEquals(1, v.validate(person).size());
		person.getAddress().setCity("Berlin");
		
		// incorrect email
		person.getContact().setEmail("�&%/");
		Assert.assertEquals(1, v.validate(person).size());
		person.getContact().setEmail("annika.maier@gmail.com");
		
		// incorrect phone
		person.getContact().setPhone("003759346390037593463900375934639003759346390037593463900375934639");
		Assert.assertEquals(1, v.validate(person).size());
		person.getContact().setPhone("00375934639");
	}
	
	@Test
	public void testLifeCycle(){
		
		EntityManagerFactory  emf = super.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		
		/*
		 * Create new Person
		 */
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

		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			this.getWasteBasket().add(person.getIdentity());
		}
		
		
		
		/*
		 * Update Street of person
		 */
		em.getTransaction().begin();
		
		person = em.find(Person.class, person.getIdentity());

		person.getAddress().setStreet("NewStreet 23");
		
		em.persist(person);
		try{
			em.getTransaction().commit();
		}
		finally{
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}	
		
		em.close();
	}
	
}
