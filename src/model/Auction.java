package model;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.sb.java.validation.Inequal;

/**
 * An Auction is open for a specific time.
 * @author Master Programming Group 6
 *
 */
@Inequal(leftAccessPath = { "closureTimestamp" }, rightAccessPath = { "creationTimestamp" }, operator = Inequal.Operator.GREATER)
public class Auction extends BaseEntity {

	static private final long MONTH_MILLIES = 30*24*60*60*1000L;
	
	@NotNull
	@Size(min=1, max=255)
	private String title;
	
	@NotNull
	@Size(min=1, max=8189)
	private String description;
	
	private short unitCount;
	
	@Min(0)
	private long askingPrice;
	
	private long closureTimeStamp;
	
	@NotNull
	private Person seller;
	
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

	//virtual Properties
	public boolean isSealed(){
		return isClosed() | !bids.isEmpty() ? true : false;
	}
	
	
	public boolean isClosed(){
		return System.currentTimeMillis() > this.closureTimeStamp ? true : false;
	}

}
