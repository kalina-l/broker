package model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;


/**
 * Address of a Person
 * @author Hady Khalifa
 *
 */
@Embeddable
public class Address {
	
		@Column(name="street", nullable = false, updatable = true, length = 63)
		@NotNull
		@Size(min=0, max=63)
		@XmlElement
		private String street;
	
		@Column(name="postCode", nullable = false, updatable = true, length = 15)
		@NotNull
		@Size(min=0, max=15)
		@XmlElement
		private String postCode;
	
		@Column(name="city", nullable = false, updatable = true, length = 63)
		@NotNull
		@Size(min=1, max=63)
		@XmlElement
		private String city;
		
		public Address(){

		};
		
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getPostCode() {
			return postCode;
		}
		public void setPostCode(String postCode) {
			this.postCode = postCode;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
	}