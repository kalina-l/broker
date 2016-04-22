package model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Contact {

	// ^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,63}$
	@Pattern(regexp="")
	@NotNull
	@Size(min=1, max=63)
	private char email;
	
	@Size(min=0, max=63)
	private char phone;
	
	
	public Contact(char email, char phone){
		
	
	}
	
	public char getEmail() {
		return email;
	}
	public void setEmail(char email) {
		this.email = email;
	}
	public char getPhone() {
		return phone;
	}
	public void setPhone(char phone) {
		this.phone = phone;
	}
	
	
}