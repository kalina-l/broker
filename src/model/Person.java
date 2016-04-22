package model;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.validation.constraints.*;
// import de.sb.java.validation.Inequal;

/**
 * @author Abdel Hady Khalifa
 *
 */
public class Person extends BaseEntity{

	@Size(min=1, max=16)
	private char alias;	
	
	@Size(min=32, max=32)
	private byte passwordHash;
	
	private Group group;
	private Name name;
	private Address address;
	private Contact contact;
	private Auction[] auctions;	
	private Bid[] bids;
	
	
	/**
	 * Constructor
	 */
	public Person(){
		super();
		
	}
	
	
	/**
	 *********** Methodes ************
	 */
	

	/**
	 * @param password
	 * @return
	 * @see <a href="http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java">http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java</a>
	 */
	public static byte[] passwordHash(String password){
		
		
		
		MessageDigest md;
		try {
		md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes("UTF-8")); 	
		byte[] digest = md.digest();
		return digest;
		
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		



	}
	
	/**
	 *********** Getter and Setter ************
	 */
	
	/**
	 * @return
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}


	/**
	 * @return
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(Name name) {
		this.name = name;
	}


	/**
	 * @return
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address
	 */
	public void setAddress(Address address) {
		this.address = address;
	}


	/**
	 * @return
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * @param contact
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	
	/**
	 * @return
	 */
	public Auction[] getAuctions() {
		return auctions;
	}


	/**
	 * @param auctions
	 */
	public void setAuctions(Auction[] auctions) {
		this.auctions = auctions;
	}


	/**
	 * @return
	 */
	public Bid[] getBids() {
		return bids;
	}


	/**
	 * @param bids
	 */
	public void setBids(Bid[] bids) {
		this.bids = bids;
	}




	/**
	 *********** INNER CLASSES ************
	 */

	public static enum Group{
		ADMIN, USER;				
		}
		
	}
	

	
