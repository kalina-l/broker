 package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.glassfish.jersey.message.filtering.EntityFiltering;

import de.sb.java.validation.Inequal;
/**
 * A bid of per Person per Auction
 * @author Master Programming Group 6
 *
 */
@Entity 
@Table(schema = "broker", name = "bid")
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(leftAccessPath = { "price" }, rightAccessPath = { "auction", "askingPrice" } , operator = Inequal.Operator.GREATER_EQUAL )
@Inequal(leftAccessPath = { "bidder" , "identity" }, rightAccessPath = { "auction", "seller" , "identity" } , operator = Inequal.Operator.NOT_EQUAL)
public class Bid extends BaseEntity{



	@Column(name="price", nullable = false, updatable = true)
	@Min(0)
	@XmlElement
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
	
	@XmlElement
	@XmlAuctionAsEntityFilter
	public Auction getAuction() {
		return auction;
	}
	
	@XmlElement
	@XmlAuctionAsReferenceFilter
	public long getAuctionReference() {
		return this.auction==null ? 0 : this.auction.getIdentity();
	}
	
	@XmlElement
	@XmlBidderAsEntityFilter
	public Person getBidder() {
		return bidder;
	}

	@XmlElement
	@XmlBidderAsReferenceFilter
	public long getBidderReference() {
		return this.bidder==null ? 0 : this.bidder.getIdentity();

	}
	
	/**
	 * Filter annotation for associated bidders marshaled as entities.
	 */
	@Target({TYPE, METHOD, FIELD})
	@Retention(RUNTIME)
	@EntityFiltering
	@SuppressWarnings("all")
	static public @interface XmlBidderAsEntityFilter {
		static final class Literal extends AnnotationLiteral<XmlBidderAsEntityFilter> implements XmlBidderAsEntityFilter {}
	}

	/**
	 * Filter annotation for associated bidders marshaled as references.
	 */
	@Target({TYPE, METHOD, FIELD})
	@Retention(RUNTIME)
	@EntityFiltering
	@SuppressWarnings("all")
	static public @interface XmlBidderAsReferenceFilter {
		static final class Literal extends AnnotationLiteral<XmlBidderAsReferenceFilter> implements XmlBidderAsReferenceFilter {};
	}

	/**
	 * Filter annotation for associated auctions marshaled as entities.
	 */
	@Target({TYPE, METHOD, FIELD})
	@Retention(RUNTIME)
	@EntityFiltering
	@SuppressWarnings("all")
	static public @interface XmlAuctionAsEntityFilter {
		static final class Literal extends AnnotationLiteral<XmlAuctionAsEntityFilter> implements XmlAuctionAsEntityFilter {}
	}

	/**
	 * Filter annotation for associated auctions marshaled as references.
	 */
	@Target({TYPE, METHOD, FIELD})
	@Retention(RUNTIME)
	@EntityFiltering
	@SuppressWarnings("all")
	static public @interface XmlAuctionAsReferenceFilter {
		static final class Literal extends AnnotationLiteral<XmlAuctionAsReferenceFilter> implements XmlAuctionAsReferenceFilter {}
	}
	
	
}
