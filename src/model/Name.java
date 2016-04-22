package model;

import javax.validation.constraints.Size;

public class Name {
	
	@Size(min=1, max=31)
	private char family;
	
	@Size(min=1, max=31)
	private char given;
	
	public Name(char family, char given){
		//init
	}

	public char getFamily() {
		return family;
	}

	public void setFamily(char family) {
		this.family = family;
	}

	public char getGiven() {
		return given;
	}

	public void setGiven(char given) {
		this.given = given;
	}
}
