package model;
import javax.validation.constraints.*;
import de.sb.java.validation.Inequal;

/**
 * @author Abdel Hady Khalifa
 *
 */
public class Bid {

	@NotNull
	private long price;
	
	@NotNull
	private Auction auction;
	
	@NotNull
	private Person bidder;
	
	
	
	
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
		return 0;
	}
	
	public Person getBidder() {
		return bidder;
	}

	public long getBidderReference() {
		return 0;
	}
	

	
	
}
