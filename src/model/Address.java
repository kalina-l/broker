package model;

import javax.validation.constraints.Size;

public class Address {
		
		@Size(min=0, max=63)
		private char street;
		
		@Size(min=0, max=15)
		private char postalCode;
		
		@Size(min=1, max=63)
		private char city;
		
		public Address(char street, char postalCode, char city){
			//init
		};
		
		public char getStreet() {
			return street;
		}
		public void setStreet(char street) {
			this.street = street;
		}
		public char getPostalCode() {
			return postalCode;
		}
		public void setPostalCode(char postalCode) {
			this.postalCode = postalCode;
		}
		public char getCity() {
			return city;
		}
		public void setCity(char city) {
			this.city = city;
		}
	}