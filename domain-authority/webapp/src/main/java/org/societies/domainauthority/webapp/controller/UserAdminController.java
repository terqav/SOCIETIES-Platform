/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.domainauthority.webapp.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.societies.domainauthority.webapp.models.UserAdminForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * This class shows the example of annotated controller
 * 
 * @author Perumal Kuppuudaiyar
 * 
 */
@Controller
public class UserAdminController {

	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	DaRegistry daRegistry;
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	/**
	 * @return the commManagerControl
	 */
	public ICommManagerController getCommManagerControl() {
		return commManagerControl;
	}

	/**
	 * @param commManagerControl
	 *            the commManagerControl to set
	 */
	public void setCommManagerControl(ICommManagerController commManagerControl) {
		this.commManagerControl = commManagerControl;
	}

	/**
	 * Displays login Page
	 * 
	 * @return
	 */
	public UserAdminController() {

	}

	
	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/adminlogin.html", method = RequestMethod.GET)
	public ModelAndView adminlogin() {
		
		// Adminstrator has to log on first
		// Check to ensure that at least one admin account exists, if it doesn't create a default admin account
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();

		boolean bAdminfound = false;
		if ((userRecords != null) && (userRecords.size() > 0))
		{
			for ( int i = 0; i < userRecords.size(); i++)
			{
				if (userRecords.get(i).getUserType() != null)
				{
					if (userRecords.get(i).getUserType().contentEquals("admin"))
						bAdminfound = true;
				}
			}
		}
		
	
		Map<String, Object> model = new HashMap<String, Object>();
		

		
		if (bAdminfound == false)
		{
			// create default admin account
			DaUserRecord adminRecord = new DaUserRecord();
			adminRecord.setName("Administrator");
			adminRecord.setPassword("Administrator");
			adminRecord.setUserType("admin");
			adminRecord.setStatus("active");
			adminRecord.setHost("");
			adminRecord.setPort("");
			adminRecord.setId("admin.societies.local");
			daRegistry.addXmppIdentityDetails(adminRecord);
			

		}
		
		// model is nothing but a standard Map object
		
		UserAdminForm userForm = new UserAdminForm();
		model.put("loginForm", userForm);
		
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("adminlogin", model);
	}
	

	/**
	 * This method get called when user submit the login page using submit
	 * button
	 * 
	 * @param loginForm
	 *            java object with data entered by user
	 * @param result
	 *            boolean result to check the data binding with object
	 * @param model
	 *            Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/adminlogin.html", method = RequestMethod.POST)
	public ModelAndView processAdminLogin(@Valid UserAdminForm loginForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "Login form error");
			return new ModelAndView("error", model);
		}
		String userName = loginForm.getUserName();
		String password = loginForm.getPassword();

		DaUserRecord userRecord = new DaUserRecord();
		

		// Check that the account exists
		userRecord = daRegistry.getXmppIdentityDetails(userName);
		
		if (userRecord == null)
		{
			//account doesn't exist, direct to new user page
			model.put("errormsg", "account not found");
			return new ModelAndView("error", model);
			
		}
		
		if ((userRecord.getUserType() == null) || (userRecord.getUserType().contentEquals("user") == true))
		{
			//account doesn't exist, direct to new user page
			model.put("errormsg", "access denied");
			return new ModelAndView("error", model);
		}
		
		
		if ((userRecord.getPassword().contentEquals(password)) == false)
		{
				//account doesn't exist, direct to new user page
				model.put("errormsg", "incoorect usename or password");
				return new ModelAndView("error", model);
		}
		
		// model is nothing but a standard Map object
				Map<String, Object> modelnew = new HashMap<String, Object>();
				List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
				UserAdminForm userForm = new UserAdminForm();
				userForm.setUserDetails(userRecords);
				
				modelnew.put("userForm", userForm);
				modelnew.put("userrecords", userRecords);
				
				Map<String, String> userTypes = new LinkedHashMap<String, String>();
				userTypes.put("user", "user");
				userTypes.put("admin", "admin");
				modelnew.put("userTypes", userTypes);
				
				Map<String, String> userStatusTypes = new LinkedHashMap<String, String>();
				userStatusTypes.put("new", "new");
				userStatusTypes.put("active", "active");
				userStatusTypes.put("deleted", "deleted");
				modelnew.put("userStatusTypes", userStatusTypes);
				
		
			return new ModelAndView("useradmin", modelnew);
		
	}
	
	  
	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/useradmin.html", method = RequestMethod.GET)
	public ModelAndView useradmin() {
		
			
		// model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		UserAdminForm userForm = new UserAdminForm();
		userForm.setUserDetails(userRecords);
		
		model.put("userForm", userForm);
		model.put("userrecords", userRecords);
		
		Map<String, String> userTypes = new LinkedHashMap<String, String>();
		userTypes.put("user", "user");
		userTypes.put("admin", "admin");
		model.put("userTypes", userTypes);
		
		Map<String, String> userStatusTypes = new LinkedHashMap<String, String>();
		userStatusTypes.put("new", "new");
		userStatusTypes.put("active", "active");
		userStatusTypes.put("deleted", "deleted");
		model.put("userStatusTypes", userStatusTypes);
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("useradmin", model);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/useradmin.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid UserAdminForm userForm,
			BindingResult result, Map model) {

		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		
		// check was has changed!
		DaUserRecord currentDBRec = null;
		DaUserRecord updatedRec = null;
		boolean reload = false;

		for(int i=0; i<userRecords.size(); i++){
			boolean bUpdated = false;
			
			 currentDBRec = userRecords.get(i);
			 updatedRec = userForm.getUserDetails().get(i);
			 
			 if (!(currentDBRec.getHost().contentEquals(updatedRec.getHost())))
			{
				 currentDBRec.setHost(updatedRec.getHost());
				 bUpdated = true;
			}
			if (!(currentDBRec.getPort().contentEquals(updatedRec.getPort())))
			{
				 currentDBRec.setPort(updatedRec.getPort());
				 bUpdated = true;
			}			 
			if (!(currentDBRec.getStatus().contentEquals(updatedRec.getStatus())))
			{
				 currentDBRec.setStatus(updatedRec.getStatus());
				 bUpdated = true;
			}
			if (!(currentDBRec.getUserType().contentEquals(updatedRec.getUserType())))
			{
				 currentDBRec.setUserType(updatedRec.getUserType());
				 bUpdated = true;
			}
			if (bUpdated)// changed
			{
				 daRegistry.updateXmppIdentityDetails(currentDBRec);
				 reload = true;
			}
		 }
	
		
		Map<String, Object> modelnew = new HashMap<String, Object>();
		if (reload)
			userRecords = daRegistry.getXmppIdentityDetails();
		UserAdminForm userFormNew = new UserAdminForm();
		userFormNew.setUserDetails(userRecords);

		
		modelnew.put("userForm", userFormNew);
		modelnew.put("userrecords", userRecords);
		
		Map<String, String> userTypes = new LinkedHashMap<String, String>();
		userTypes.put("user", "user");
		userTypes.put("admin", "admin");
		modelnew.put("userTypes", userTypes);
		
		Map<String, String> userStatusTypes = new LinkedHashMap<String, String>();
		userStatusTypes.put("new", "new");
		userStatusTypes.put("active", "active");
		userStatusTypes.put("deleted", "deleted");
		modelnew.put("userStatusTypes", userStatusTypes);
		
		
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("useradmin", modelnew);
	}
	
	
    @RequestMapping(value="/useradmin_old.html",method=RequestMethod.POST)
    public @ResponseBody JsonResponse UpdateUserAccount(
    		@ModelAttribute(value="userName") String userName,
    		@ModelAttribute(value="userHost") String userHost,
    		@ModelAttribute(value="userPort") String userPort,
    		@ModelAttribute(value="userStatus") String userStatus,
    		BindingResult result ){
    	JsonResponse res = new JsonResponse();
              if(!result.hasErrors()){
            	  DaUserRecord userDetails = new DaUserRecord();
            	  userDetails = daRegistry.getXmppIdentityDetails(userName);
            	  userDetails.setHost(userHost);
            	  userDetails.setPort(userPort);
            	  userDetails.setStatus(userStatus);
            	  daRegistry.updateXmppIdentityDetails(userDetails);
                  res.setStatus("SUCCESS");
                  res.setResult(userDetails);
            }else{
                    res.setStatus("FAIL");
                    //res.setResult(result.getAllErrors());
            }

            return res;
    }
    
	
}