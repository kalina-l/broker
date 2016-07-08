package model;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Person who can bid on auctions
 * @author Master Programming Group 6
 *
 */
//@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@Entity
//@XmlRootElement
@Table(schema = "broker", name = "person")
@PrimaryKeyJoinColumn(name = "personIdentity")
public class Person extends BaseEntity{

	
	static private final byte[] DEFAULT_PASSWORD_HASH = passwordHash("");

	@Column(name = "alias", nullable = false, updatable = false, length = 16, unique = true)
	@NotNull
	@Size(min=1, max=16)
	@XmlElement
	private String alias;	
	
	@Column(name = "passwordHash", nullable = false, updatable = true, length = 32)
	@Size(min=32, max=32)
	private byte[] passwordHash;
	
	@Column(name = "groupAlias", nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	@NotNull
	@XmlElement
	private Group group;
	
	@Embedded
	@NotNull
	@Valid
	@XmlElement
	private Name name;
	
	@Embedded
	@NotNull
	@Valid
	@XmlElement
	private Address address;
	
	@Embedded
	@NotNull
	@Valid
	@XmlElement
	private Contact contact;
	
	//Relationsfelder
	
	@OneToMany(mappedBy ="seller")
	private Set<Auction> auctions;	
	
	@OneToMany(mappedBy ="bidder")
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
	
	
	

	public void setAlias(String alias){
		this.alias = alias;
	}
	
	public String getAlias(){
		return alias;
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

	
