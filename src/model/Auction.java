package model;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Size;

/**
 * An Auction is open for a specific time.
 * @author Master Programming Group 6
 *
 */
public class Auction extends BaseEntity {

	static private final long AUCTION_DURATION = 100000;
	
	@Size(min=1, max=255)
	private char title;

	@Size(min=1, max=8189)
	private char description;
	private short unitCount;
	private long askingPrice;
	private long closureTimeStamp;
	private Person seller;
	private Set<Bid> bids;
	
	
	public Auction(Person seller){
		super();
		this.seller = seller;
		this.bids = new HashSet<Bid>();
		this.closureTimeStamp = this.getCreationTimeStamp() + AUCTION_DURATION;
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

	
	public Bid getBid(Person bidder) {
		for(Bid bid : bids){
			if(bidder.getBids().contains(bid)) 
				return bid;
		}
		return null;

	}

	public boolean isSealed(){
		return isClosed() | !bids.isEmpty() ? true : false;
	}
	
	public boolean isClosed(){
		return System.currentTimeMillis() > this.closureTimeStamp ? true : false;
	}

	
	
	/**
	 * Getter Setter
	 */
	

	public char getTitle() {
		return title;
	}


	public void setTitle(char title) {
		this.title = title;
	}
	
	public char getDescription() {
		return description;
	}


	public void setDescription(char description) {
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


}
