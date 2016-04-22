package model;

import javax.validation.constraints.Size;


public class Address {
		
		@Size(min=0, max=63)
		private String street;

		@Size(min=0, max=15)
		private String postalCode;
		
		@Size(min=1, max=63)
		private String city;
		
		public Address(){

		};
		
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getPostalCode() {
			return postalCode;
		}
		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
	}