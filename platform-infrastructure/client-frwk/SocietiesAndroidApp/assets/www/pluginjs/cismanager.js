phonegapdesktop.internal.parseConfigFile('pluginjs/cismanager.json');


window.plugins.SocietiesLocalCISManager = {
	createCIS: function(successCallback, failureCallback) {
		console.log("create CIS desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'createdCIS'));
		}
	},
	listCIS: function(successCallback, failureCallback) {
		console.log("list CIS desktop invoked");
		if (phonegapdesktop.internal.randomException("CISDesktopManagerService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CISDesktopManagerService', 'listOfCISs'));
		}
	}	


}