/**
 * com.broker.WelcomeController: broker welcome controller.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.com = this.com || {};
this.com.broker = this.com.broker || {};
(function () {
	var SUPER = com.broker.Controller;

	/**
	 * Creates a new welcome controller that is derived from an abstract controller.
	 * @param sessionContext {com.broker.SessionContext} a session context
	 */
	com.broker.WelcomeController = function (sessionContext) {
		SUPER.call(this, 0, sessionContext);
	}
	com.broker.WelcomeController.prototype = Object.create(SUPER.prototype);
	com.broker.WelcomeController.prototype.constructor = com.broker.WelcomeController;


	/**
	 * Displays the associated view.
	 */
	com.broker.WelcomeController.prototype.display = function () {
		//TODO - lazier handling of login credentials
		this.sessionContext.clear();
		SUPER.prototype.display.call(this);

		var sectionElement = document.querySelector("#login-template").content.cloneNode(true).firstElementChild;
		sectionElement.querySelector("button").addEventListener("click", this.login.bind(this));
		document.querySelector("main").appendChild(sectionElement);
	}


	/**
	 * Performs a login check on the given user data, initializes the controller's
	 * session context if the login was successful, and initiates rendering of the
	 * preferences view.
	 */
	com.broker.WelcomeController.prototype.login = function () {
		var inputElements = document.querySelectorAll("section.login input");
		var credentials = {
			userAlias: inputElements[0].value.trim(),
			userPassword: inputElements[1].value.trim()
		};

		if (!credentials.userAlias | !credentials.userPassword) {
			this.displayStatus(401, "Unauthorized");
			return;
		}

		var self = this;
		com.broker.util.AJAX.invoke("/services/people/requester", "GET", {"Accept": "application/json"}, null, credentials, function (request) {
			self.displayStatus(request.status, request.statusText);
			if (request.status === 200) {
				self.sessionContext.user = JSON.parse(request.responseText);
				self.sessionContext.userPassword = credentials.userPassword;
				com.broker.APPLICATION.preferencesController.display();
			}
		});
	}
} ());