/**
 * com.broker.Controller: abstract controller.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.com = this.com || {};
this.com.broker = this.com.broker || {};
(function () {

	/**
	 * Creates an "abstract" controller.
	 * @param viewOrdinal {Number} the ordinal of the view associated with this controller
	 * @param sessionContext {com.broker.SessionContext} a session context
	 */
	com.broker.Controller = function (viewOrdinal, sessionContext) {
		this.viewOrdinal = viewOrdinal;
		this.sessionContext = sessionContext;
	}


	/**
	 * Displays the view associated with this controller by marking said
	 * view's menu item as selected, and removing the main element's
	 * children.
	 */
	com.broker.Controller.prototype.display = function () {
		var menuElements = document.querySelectorAll("nav li");

		for (var viewOrdinal = 0; viewOrdinal < menuElements.length; ++viewOrdinal) {
			if (viewOrdinal == this.viewOrdinal) {
				menuElements[viewOrdinal].classList.add("selected");
			} else {
				menuElements[viewOrdinal].classList.remove("selected");
			}
		}

		var mainElement = document.querySelector("main");
		while (mainElement.lastChild) {
			mainElement.removeChild(mainElement.lastChild);
		}
	}


	/**
	 * Displays the given HTTP status.
	 * @param code {Number} the status code
	 * @param message {String} the status message
	 */
	com.broker.Controller.prototype.displayStatus = function (code, message) {
		var outputElement = document.querySelector("body > footer output");
		outputElement.value = code + " " + (code === 0 ? "unreachable" : message);

		switch (Math.floor(0.01 * code)) {
			case 1:
			case 2:
				outputElement.className = "success";
				break;
			case 3:
				outputElement.className = "warning";
				break;
			default:
				outputElement.className = "error";
				break;
		}
	}
} ());