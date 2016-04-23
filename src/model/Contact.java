package model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Contact of a Person
 * @author Master Programming Group 6
 *
 */
public class Contact {

	@Pattern(regexp="\b^[A-Z0-9._%+-]+@[A-Z0-9.-]+\b.[A-Z]{2,63}\b$")
	@NotNull
	@Size(min=1, max=63)
	private String email;
	
	@Size(min=0, max=63)
	private String phone;
	
	
	public Contact(){
		
	}
	
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}