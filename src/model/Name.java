package model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

/**
 * Name of a Person
 * @author Master Programming Group 6
 *
 */
@Embeddable
public class Name {

	@Column(name="familyName", nullable = false, updatable = true, length = 31)
	@NotNull
	@Size(min=1, max=31)
	@XmlElement
	private String family;
	
	@Column(name="givenName", nullable = false, updatable = true, length = 31)
	@NotNull
	@Size(min=1, max=31)
	@XmlElement
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
