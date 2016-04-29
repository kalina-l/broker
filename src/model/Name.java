package model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Name of a Person
 * @author Master Programming Group 6
 *
 */
public class Name {
	@NotNull
	@Size(min=1, max=31)
	private String family;
	
	@NotNull
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
