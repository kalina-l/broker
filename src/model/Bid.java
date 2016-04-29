package model;

import javax.validation.constraints.Min;

import de.sb.java.validation.Inequal;

/**
 * A bid of per Person per Auction
 * @author Master Programming Group 6
 *
 */
@Inequal(leftAccessPath = { "price" }, rightAccessPath = { "auction", "askingPrice" } , operator = Inequal.Operator.GREATER_EQUAL )
@Inequal(leftAccessPath = { "bidder" , "identity" }, rightAccessPath = { "auction", "seller" , "identity" } , operator = Inequal.Operator.NOT_EQUAL)
public class Bid extends BaseEntity{

	@Min(0)
	private long price;
	

	private Auction auction;
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
	
	public Auction getAuction() {
		return auction;
	}
	
	public long getAuctionReference() {
		return this.auction==null ? 0 : this.auction.getIdentity();
	}
	
	public Person getBidder() {
		return bidder;
	}

	public long getBidderReference() {
		return this.bidder==null ? 0 : this.bidder.getIdentity();

	}
	

	
	
}
