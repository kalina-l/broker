package model;

import javax.validation.constraints.Size;

public class Auction {


	@Size(min=1, max=255)
	private char title;

	@Size(min=1, max=8189)
	private char description;
	private short unitCount;
	private long askingPrice;
	private long closureTimeStamp;


	private Person seller;
	private Bid[] bids;
	
	
	public Auction(Person seller){
		
	}
	
	
	public Person getSeller() {
		return seller;
	}
	
	public long getSellerReference() {
		return 0;
	}


	public Bid getBid(Person bidder) {
		Bid bid = null;
		return bid;
	}

	public boolean isSealed(){
		return true;
	}
	
	public boolean isClosed(){
		return false;
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


}
