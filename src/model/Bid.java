 package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.jersey.message.filtering.EntityFiltering;

import de.sb.java.validation.Inequal;
/**
 * A bid of per Person per Auction
 * @author Master Programming Group 6
 *
 */
@Entity
@XmlRootElement // TODO muss man wegmachen? nur baseentity? zirkul�re probleme bei anderen 
@Table(schema = "broker", name = "bid")
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(leftAccessPath = { "price" }, rightAccessPath = { "auction", "askingPrice" } , operator = Inequal.Operator.GREATER_EQUAL )
@Inequal(leftAccessPath = { "bidder" , "identity" }, rightAccessPath = { "auction", "seller" , "identity" } , operator = Inequal.Operator.NOT_EQUAL)
public class Bid extends BaseEntity{



	@Column(name="price", nullable = false, updatable = true)
	@Min(0)
	@XmlAttribute
	private long price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auctionReference", nullable = false, updatable = false)
	private Auction auction;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidderReference", nullable = false, updatable = false)
	private Person bidder;
	
	
	public Bid(Auction auction, Person bidder){
		this.auction = auction;
		this.bidder = bidder;
	}
	
	protected Bid(){
		this(null, null);
	}
	
	
	public long getPrice() {
		return price;
	}
	
	public void setPrice(long price) {
		this.price = price;
	}
	
	//@XmlElement
//	@XmlAuctionAsEntityFilter
	public Auction getAuction() {
		return auction;
	}
	
//	@XmlElement
//	@XmlAuctionAsReferenceFilter
	public long getAuctionReference() {
		return this.auction==null ? 0 : this.auction.getIdentity();
	}
	
//	@XmlElement
//	@XmlBidderAsEntityFilter
	public Person getBidder() {
		return bidder;
	}

//	@XmlElement
//	@XmlBidderAsReferenceFilter
	public long getBidderReference() {
		return this.bidder==null ? 0 : this.bidder.getIdentity();

	}
	
	
	@EntityFiltering
	public @interface XmlAuctionAsEntityFilter { }
	
	@EntityFiltering
	public @interface XmlAuctionAsReferenceFilter { }
	
	@EntityFiltering
	public @interface XmlBidderAsEntityFilter { }
	
	@EntityFiltering
	public @interface XmlBidderAsReferenceFilter { }
	
	
	
}
