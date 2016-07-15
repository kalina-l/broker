/**
 * com.broker.SessionContext: broker session context.
 * Copyright (c) 2013-2015 Sascha Baumeister
 */
"use strict";

this.com = this.com || {};
this.com.broker = this.com.broker || {};
(function () {

	/**
	 * Creates an empty session context.
	 */
	com.broker.SessionContext = function () {
		var self = this;

		Object.defineProperty(this, "user", {
			enumerable: true,
			configurable: false,
			writable: true,
			value: null
		});

		Object.defineProperty(this, "userAlias", {
			enumerable: true,
			configurable: false,
			get: function () { return self.user == null ? null : self.user.alias; }
		});

		Object.defineProperty(this, "userPassword", {
			enumerable: true,
			configurable: false,
			writable: true,
			value: null
		});
	}


	/**
	 * Clears this context.
	 */
	com.broker.SessionContext.prototype.clear = function () {
		this.user = null;
		this.userPassword = null;
		
	}
} ());