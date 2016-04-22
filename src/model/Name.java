package model;

import javax.validation.constraints.Size;

public class Name {
	
	@Size(min=1, max=31)
	private String family;
	
	@Size(min=1, max=31)
	private String given;
	
	
	
	public Name(){
		
	}

	
	
	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.given = given;
	}
}
