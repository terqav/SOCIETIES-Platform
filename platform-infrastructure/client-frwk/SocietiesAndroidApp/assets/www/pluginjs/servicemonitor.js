phonegapdesktop.internal.parseConfigFile('pluginjs/servicemonitor.json');


window.plugins.SocietiesCoreServiceMonitor = {
	connectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'connectService'));
		}
		
	},
	disconnectService: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'disconnectService'));
		}
		
	},
	activeServices: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'activeServices'));
		}
		
	},
	activeTasks: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'activeTasks'));
		}
		
	},
	getInstalledApps: function(successCallback, errorCallback){
		if (phonegapdesktop.internal.randomException("CoreServiceMonitorService")) {
			errorCallback('A random error was generated');
		}
		else {
			successCallback(phonegapdesktop.internal.getDebugValue('CoreServiceMonitorService', 'getInstalledApps'));
		}
	}
	
}

var SocietiesCoreServiceMonitorHelper = {
		/**
		 * @methodOf SocietiesCoreServiceMonitorHelper#
		 * @description Connect to Service Monitor native service
		 * @param {Object} function to be executed if connection successful
		 * @returns null
		 */

		connectToCoreServiceMonitor: function(actionFunction) {
			console.log("Connect to CoreServiceMonitorService");
				
			function success(data) {
				actionFunction();
			}
			
			function failure(data) {
				alert("CoreServiceMonitorService - failure: " + data);
			}

			window.plugins.SocietiesCoreServiceMonitor.connectService(success, failure);
		}
}