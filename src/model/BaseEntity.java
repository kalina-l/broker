package model;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.*;

/**
 * Abstract BaseEntity super class for all persitance entities. 
 * @author Master Programming Group 6
 *
 */
@Entity
@XmlRootElement
@Table(schema = "broker", name = "baseentity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
@XmlSeeAlso({Auction.class, Person.class, Bid.class})
public abstract class BaseEntity implements Comparable<BaseEntity>{
	
	@Id
	@GeneratedValue (strategy=GenerationType.IDENTITY)
	@Min(0)
	@XmlID
	private long identity;
	
	@Column(name="version", nullable = false, updatable = false)
	@Min(1)
	@XmlElement
	private int version;
	
	@Column(name="creationTimestamp", nullable = false, updatable = false)
	@XmlElement
	private long creationTimeStamp;
	
	
	public BaseEntity(){

		this.identity = 0;
		this.version = 1;
		this.creationTimeStamp = System.currentTimeMillis();
		
	}
	
	
	@Override
	public int compareTo(BaseEntity other){
		return Long.compare(this.getIdentity(), other.getIdentity());	
	}


	/**
	 * @return identity number of the entity
	 */
	public long getIdentity() {
		return identity;
	}

	/**
	 * @return version number of the entity
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version version number of the entity
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return creation timestamp of the entity
	 */
	public long getCreationTimeStamp() {
		return creationTimeStamp;
	}
	
}
