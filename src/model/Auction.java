package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

import de.sb.java.validation.Inequal;

/**
 * An Auction is open for a specific time.
 * @author Master Programming Group 6
 *
 */
@Entity
@Table(schema = "broker", name = "auction")
@PrimaryKeyJoinColumn(name = "auctionIdentity")
@Inequal(leftAccessPath = { "closureTimestamp" }, rightAccessPath = { "creationTimestamp" }, operator = Inequal.Operator.GREATER)
public class Auction extends BaseEntity {

	static private final long MONTH_MILLIES = 30*24*60*60*1000L;
	
	@Column(name = "title", nullable = false, updatable = true, length = 255)
	@NotNull
	@Size(min=1, max=255)
	@XmlElement
	private String title;
	
	@Column(name = "description", nullable = false, updatable = true, length = 8189)
	@NotNull
	@Size(min=1, max=8189)
	@XmlElement
	private String description;
	
	@Column(name = "unitCount", nullable = false, updatable = true)
	@XmlElement
	private short unitCount;
	
	@Column(name = "askingPrice", nullable = false, updatable = true)
	@Min(0)
	@XmlElement
	private long askingPrice;
	
	@Column(name = "closureTimestamp", nullable = false, updatable = true)
	@XmlElement
	private long closureTimeStamp;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sellerReference", nullable = false, updatable = false)
	@NotNull
	@XmlElement
	private Person seller;
	
	@OneToMany(mappedBy = "auction")
	@XmlElement
	private Set<Bid> bids;
	
	
	public Auction(Person seller){
		super();
		this.seller = seller;
		this.bids = new HashSet<Bid>();
		this.closureTimeStamp = this.getCreationTimeStamp() + MONTH_MILLIES;
	}
	
	protected Auction(){
		this(null);
	}
	
	
	public Person getSeller() {
		return seller;
	}
	
	public long getSellerReference() {	
		return this.seller==null ? 0 : this.seller.getIdentity();
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public long getClosureTimeStamp() {
		return closureTimeStamp;
	}


	public void setClosureTimeStamp(long closureTimeStamp) {
		this.closureTimeStamp = closureTimeStamp;
	}


	public long getAskingPrice() {
		return askingPrice;
	}


	public void setAskingPrice(long askingPrice) {
		this.askingPrice = askingPrice;
	}


	public short getUnitCount() {
		return unitCount;
	}


	public void setUnitCount(short unitCount) {
		this.unitCount = unitCount;
	}
	
	public Set<Bid> getBids(){
		return bids;
	}

	
	public Bid getBid(Person bidder) {
		for(Bid bid : bids){
			if(bidder.getIdentity() == bid.getBidder().getIdentity()) 
				return bid;
		}
		return null;

	}

	@XmlElement
	//virtual Properties
	public boolean isSealed(){
		return isClosed() | !bids.isEmpty() ? true : false;
	}
	
	@XmlElement
	public boolean isClosed(){
		return System.currentTimeMillis() > this.closureTimeStamp ? true : false;
	}

}
