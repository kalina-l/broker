"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {
	var SUPER = de.sb.broker.Controller;
	var TIMESTAMP_OPTIONS = {
		year: 'numeric', month: 'numeric', day: 'numeric',
		hour: 'numeric', minute: 'numeric', second: 'numeric',
		hour12: false
	};


	/**
	 * Creates a new auctions controller that is derived from an abstract controller.
	 * @param sessionContext {de.sb.broker.SessionContext} a session context
	 */
	de.sb.broker.OpenAuctionsController = function (sessionContext) {
		SUPER.call(this, 1, sessionContext);
	}
	
	de.sb.broker.OpenAuctionsController.prototype = Object.create(SUPER.prototype);
	de.sb.broker.OpenAuctionsController.prototype.constructor = de.sb.broker.OpenAuctionsController;
	
	de.sb.broker.OpenAuctionsController.prototype.display = function () {
		if (!this.sessionContext.user) return;
		SUPER.prototype.display.call(this);
		
		var sectionElement = document.querySelector("#open-auctions-template").content.cloneNode(true).firstElementChild;
		sectionElement.querySelector("button").addEventListener("click", this.showAuctionTemplate.bind(this));
		document.querySelector("main").appendChild(sectionElement);
		
		var indebtedSemaphore = new de.sb.util.Semaphore(1 - 2);
		var statusAccumulator = new de.sb.util.StatusAccumulator();
		var self = this;
		
		var resource = "/services/auctions?closed=false";
		de.sb.util.AJAX.invoke(resource, "GET", {"Accept": "application/json"}, null, this.sessionContext, function (request) {
			if (request.status === 200) {
				var auctions = JSON.parse(request.responseText);
				self.displayOpenAuctions(auctions);
			}
			statusAccumulator.offer(request.status, request.statusText);
			indebtedSemaphore.release();
		});
		
		indebtedSemaphore.acquire(function () {
			self.displayStatus(statusAccumulator.status, statusAccumulator.statusText);
		});
	}
	
	
	/**
	 * Displays open auctions
	 */
	de.sb.broker.OpenAuctionsController.prototype.displayOpenAuctions = function () {
		
		var tableBodyElement = document.querySelector("section.open-auctions tbody");
		var rowTemplate = document.createElement("tr");
		for (var index = 0; index < 7; ++index) {
			var cellElement = document.createElement("td");
			cellElement.appendChild(document.createElement("output"));
			rowTemplate.appendChild(cellElement);
		}
		
		var self = this;
		auctions.forEach(function (auction) {
			var rowElement = rowTemplate.cloneNode(true);
			tableBodyElement.appendChild(rowElement);
			
			var activeElements = rowElement.querySelectorAll("output");
			
			activeElements[0].value = auction.seller.alias;
			activeElements[0].title = auction.seller.name.givenName + " " + auction.seller.name.familyName + " | " + auction.seller.contact.email;
			activeElements[1].value = new Date(auction.creationTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[2].value = new Date(auction.closureTimestamp).toLocaleString(TIMESTAMP_OPTIONS);
			activeElements[3].value = auction.description;
			activeElements[4].value = auction.unitCount;
			activeElements[5].value = auction.askingPrice;
			
			
			var bidField;
			
			if (auction.seller.identity === user.identity) {
				bidField = document.createElement('Button');
				bidField.type = "button";
				
				var text = document.createTextNode("edit");      
				bidField.appendChild(text); 
				
				bidField.addEventListener("click", (e) => { 
					self.showAuctionTemplate(e, auction) 
				});
				
			} else {
				bidField = document.createElement('Input');
				bidField.type = "number";
				bidField.step = "1.00";
				bidField.value = auction.askingPrice;
				bidField.addEventListener("change", (e) => { 
					self.persistBid(e, auction);
				});
				
			}
			
		});
	}	
			
			
		
	
	
	
	de.sb.broker.OpenAuctionsController.prototype.showAuctionTemplate = function(e, auction) {
		
		console.log("e: " + e);
		console.log("auction: " + auction);
		
		var auctionInputElement = document.querySelector("#auction-form-template").content.cloneNode(true).firstElementChild;
		if (document.querySelector("main").lastChild.className == "auction-form"){
			document.querySelector("main").removeChild(document.querySelector("main").lastChild);
		}
		var inputElements = auctionInputElement.querySelectorAll("input");
		
		if (auction) {
			
			inputElements[0].value = de.sb.broker.APPLICATION.createDate(auction.creationTimestamp);
			inputElements[1].value = de.sb.broker.APPLICATION.createDate(auction.closureTimestamp);
			inputElements[2].value = auction.title;
			inputElements[3].value = auction.unitCount;
			inputElements[4].value = auction.askingPrice;
			auctionInputElement.querySelector("textarea").value = auction.description;
		} 
		
		auctionInputElement.querySelector("button").addEventListener("click", this.persistAuction.bind(this, e, auction));
		document.querySelector("main").appendChild(auctionInputElement);
	}
	
	
	
	
	
	
	
	de.sb.broker.OpenAuctionsController.prototype.persistAuction = function(e, auction){
		
		var inputElements = document.querySelectorAll("section.auction-form input");
		
		var newAuction = (auction) ? auction : {};
		var date = new Date().getTime();
		
		if(auction) {
			newAuction = auction;
		}else {
			newAuction = {};
			newAuction.type = "auction";
		}
				
		newAuction.title = inputElements[2].value;
		newAuction.description = document.querySelector("section.auction-form textarea").value;
		newAuction.unitCount = inputElements[3].value;
		newAuction.askingPrice = inputElements[4].value;
		
		var self = this;
		var header = {"Content-type": "application/json"};
		var body = JSON.stringify(newAuction);
		de.sb.util.AJAX.invoke("/services/auctions?sellerID=" + this.sessionContext.user.identity + "", "PUT", header, body, this.sessionContext, function (request) {
			self.displayStatus(request.status, request.statusText);
			document.querySelector("main").removeChild(document.querySelector(".auction-form"));
			if (request.status === 200) {
				self.displayOpenAuctions();
			}
		});
	}
	
	de.sb.broker.OpenAuctionsController.prototype.persistBid = function(auction){
		
	}
	
} ());





