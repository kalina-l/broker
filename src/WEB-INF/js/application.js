"use strict";

// Namespaces (äquivalent zu packages in java) 
// erzeugt leeres object wenn this.com undefined ist
this.com = this.com || {};
this.com.broker = this.com.broker || {};

//Scope
(function () {
	var SESSION_CONTEXT = new com.broker.SessionContext();

	/**
	 * The broker application singleton maintaining the view controllers.
	 */
	com.broker.APPLICATION = {
		welcomeController: "WelcomeController" in com.broker
			? new com.broker.WelcomeController(SESSION_CONTEXT)
			: new com.broker.Controller(0),
		openAuctionsController: "OpenAuctionsController" in com.broker
			? new com.broker.OpenAuctionsController(SESSION_CONTEXT)
			: new com.broker.Controller(1),
		closedAuctionsController: "ClosedAuctionsController" in com.broker
			? new com.broker.ClosedAuctionsController(SESSION_CONTEXT)
			: new com.broker.Controller(2),
		preferencesController: "PreferencesController" in com.broker
			? new com.broker.PreferencesController(SESSION_CONTEXT)
			: new com.broker.Controller(3)
	}
	var APPLICATION = com.broker.APPLICATION;


	/**
	 * Register DOM menu callbacks, and display welcome view.
	 */
	window.addEventListener("load", function () {
		var menuAnchors = document.querySelectorAll("header > nav a");
		menuAnchors[0].addEventListener("click", APPLICATION.welcomeController.display.bind(APPLICATION.welcomeController));
		menuAnchors[1].addEventListener("click", APPLICATION.openAuctionsController.display.bind(APPLICATION.openAuctionsController));
		menuAnchors[2].addEventListener("click", APPLICATION.closedAuctionsController.display.bind(APPLICATION.closedAuctionsController));
		menuAnchors[3].addEventListener("click", APPLICATION.preferencesController.display.bind(APPLICATION.preferencesController));

		APPLICATION.welcomeController.display();
		APPLICATION.welcomeController.displayStatus(200, "OK");
	});
} ());