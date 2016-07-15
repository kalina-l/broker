/**
 * com.broker.PreferencesController: broker preferences controller.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.com = this.com || {};
this.com.broker = this.com.broker || {};
(function () {
	var SUPER = com.broker.Controller;

	/**
	 * Creates a new preferences controller that is derived from an abstract controller.
	 * @param sessionContext {com.broker.SessionContext} a session context
	 */
	com.broker.PreferencesController = function (sessionContext) {
		SUPER.call(this, 3, sessionContext);
	}
	com.broker.PreferencesController.prototype = Object.create(SUPER.prototype);
	com.broker.PreferencesController.prototype.constructor = com.broker.PreferencesController;


	/**
	 * Displays the associated view.
	 */
	com.broker.PreferencesController.prototype.display = function () {
		if (!this.sessionContext.user) return;
		SUPER.prototype.display.call(this);
		this.displayStatus(200, "OK");

		var sectionElement = document.querySelector("#preferences-template").content.cloneNode(true).firstElementChild;
		sectionElement.querySelector("button").addEventListener("click", this.persistUser.bind(this));
		document.querySelector("main").appendChild(sectionElement);

		this.displayUser();
	}


	/**
	 * Displays the session user.
	 */
	com.broker.PreferencesController.prototype.displayUser = function () {
		var user = this.sessionContext.user;
		var activeElements = document.querySelectorAll("section.preferences input");
		activeElements[0].value = user.group;
		activeElements[1].value = user.alias;
		activeElements[2].value = "";
		activeElements[3].value = user.name.given;
		activeElements[4].value = user.name.family;
		activeElements[5].value = user.address.street;
		activeElements[6].value = user.address.postalCode;
		activeElements[7].value = user.address.city;
		activeElements[8].value = user.contact.email;
		activeElements[9].value = user.contact.phone;
	}


	/**
	 * Persists the session user.
	 */
	com.broker.PreferencesController.prototype.persistUser = function () {
		var inputElements = document.querySelectorAll("section.preferences input");

		var user = JSON.parse(JSON.stringify(this.sessionContext.user));
		var password = inputElements[2].value.trim();
		user.name.given = inputElements[3].value.trim();
		user.name.family = inputElements[4].value.trim();
		user.address.street = inputElements[5].value.trim();
		user.address.postCode = inputElements[6].value.trim();
		user.address.city = inputElements[7].value.trim();
		user.contact.email = inputElements[8].value.trim();
		user.contact.phone = inputElements[9].value.trim();

		var self = this;
		var header = {"Content-type": "application/json"};
		if (password) header["Set-password"] = password;
		var body = JSON.stringify(user);
		com.broker.util.invoke("/services/people", "PUT", header, body, this.sessionContext, function (request) {
			self.displayStatus(request.status, request.statusText);
			if (request.status === 200) {
				self.sessionContext.user.version = self.sessionContext.user.version + 1;
				self.sessionContext.user.name = user.name;
				self.sessionContext.user.address = user.address;
				self.sessionContext.user.contact = user.contact;
				if (password.length !== 0) self.sessionContext.userPassword = password;
			} else if (request.status === 409) {
				com.broker.APPLICATION.welcomeController.display(); 
			} else {
				self.displayUser();
			}
		});
	}
} ());