package model;

/**
 * @author Abdel Hady Khalifa
 *
 */
public class Bid extends BaseEntity{

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
