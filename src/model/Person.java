package model;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Person who can bid on auctions
 * @author Master Programming Group 6
 *
 */
public class Person extends BaseEntity{

	
	static private final byte[] DEFAULT_PASSWORD_HASH = passwordHash("");

	@NotNull
	@Size(min=1, max=16)
	private String alias;	
	
	@Size(min=32, max=32)
	private byte[] passwordHash;
	
	@NotNull
	private Group group;
	
	@NotNull
	@Valid
	private Name name;
	
	@NotNull
	@Valid
	private Address address;
	
	@NotNull
	@Valid
	private Contact contact;
	
	private Set<Auction> auctions;	
	private Set<Bid> bids;
	
	
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
			//Programmabbruch wenn eine Exception auftritt die nicht auftreten kann
			throw new AssertionError();		
		}
	}
	

	public Person(){
		super();
		this.passwordHash = DEFAULT_PASSWORD_HASH.clone();
		this.group = Group.USER;
		this.name = new Name();
		this.address = new Address();
		this.contact = new Contact();
		this.auctions = new HashSet<Auction>();	
		this.bids = new HashSet<Bid>();
			
	}

	
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
	public byte[] getPasswordHash(){
		return passwordHash;
	}
	
	/**
	 * @param passwordHash
	 */
	public void setPasswordHash(byte[] passwordHash){
		this.passwordHash = passwordHash;
	}
	
	/**
	 * @return
	 */
	public Name getName() {
		return name;
	}


	/**
	 * @return
	 */
	public Address getAddress() {
		return address;
	}


	/**
	 * @return
	 */
	public Contact getContact() {
		return contact;
	}


	/**
	 * @return
	 */
	public Set<Auction> getAuctions() {
		return auctions;
	}

	/**
	 * @return
	 */
	public Set<Bid> getBids() {
		return bids;
	}
	
	
	public Bid getBid(Auction auction) {
		for(Bid bid : bids){
			if(auction.getIdentity() == bid.getAuction().getIdentity()) 
				return bid;
		}
		
		return null;

	}
}

	
