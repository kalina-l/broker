package model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

/**
 * Contact of a Person
 * @author Master Programming Group 6
 *
 */
@Embeddable
public class Contact {

	@Column(name="email", nullable = false, updatable = true, length = 63)
	@Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
	        +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
	        +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
	             message="{invalid.email}") // https://docs.oracle.com/cd/E19798-01/821-1841/gkahq/index.html
	@NotNull
	@Size(min=1, max=63)
	@XmlElement
	private String email;
	
	@Column(name="phone", nullable = false, updatable = true, length = 63)
	@NotNull
	@Size(min=0, max=63)
	@XmlElement
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