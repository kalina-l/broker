package model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Contact of a Person
 * @author Master Programming Group 6
 *
 */
@Embeddable
public class Contact {

	@Column(name="email", nullable = false, updatable = true, length = 63)
	@Pattern(regexp="\b^[A-Z0-9._%+-]+@[A-Z0-9.-]+\b.[A-Z]{2,63}\b$")
	@NotNull
	@Size(min=1, max=63)
	private String email;
	
	@Column(name="phone", nullable = false, updatable = true, length = 63)
	@NotNull
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