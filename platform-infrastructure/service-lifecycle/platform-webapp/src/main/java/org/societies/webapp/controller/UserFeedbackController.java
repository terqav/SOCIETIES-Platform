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

package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

@Controller
public class UserFeedbackController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IUserFeedback userFeedback;
	
	Logger LOG = LoggerFactory.getLogger(UserFeedbackController.class);
	
	//GUI types
	private static final String RADIO = "radio";
	private static final String CHECK = "check";
	private static final String ACK = "ack";
	private static final String ABORT = "abort";
	private static final String NOTIFICATION = "notification";
	
	Gson gsonMgr = new Gson();
	
	public IUserFeedback getUserFeedback(){
		return userFeedback;
	}
	
	public void setUserFeedback(IUserFeedback userFeedback){
		this.userFeedback = userFeedback;
	}
	
	//TODO : Quick fix for pilot
	@RequestMapping(value = "/{cssId}/get_form.html", method = RequestMethod.GET)
	public ModelAndView getFormwithid(){
		//retrieve next request from UF service
		String returnString = "";
		FeedbackForm form = userFeedback.getNextRequest();
		if(form != null){
			returnString = gsonMgr.toJson(form);
		}else{
			returnString = gsonMgr.toJson(new FeedbackForm().generateEmptyFeedbackForm());
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("data", returnString);
		return new ModelAndView("data", model);
	}

	@RequestMapping(value = "/get_form.html", method = RequestMethod.GET)
	public ModelAndView getForm(){
		//retrieve next request from UF service
		String returnString = "";
		FeedbackForm form = userFeedback.getNextRequest();
		if(form != null){
			returnString = gsonMgr.toJson(form);
		}else{
			returnString = gsonMgr.toJson(new FeedbackForm().generateEmptyFeedbackForm());
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("data", returnString);
		return new ModelAndView("data", model);
	}
	
	@RequestMapping(value = "/get_form.html", method = RequestMethod.POST)
	public ModelAndView submitResponse(@RequestParam("data") String jsonString){
		Map<String, Object> model = new HashMap<String, Object>();
		LOG.info("[Submit response]"+jsonString);
		
		//convert json string back to Java types
		FeedbackForm responseForm; 
		try{
			responseForm = gsonMgr.fromJson(jsonString, FeedbackForm.class);
		}catch(Exception e){
			LOG.error("Submitted response - incorrect format");
			String returnString = gsonMgr.toJson((new FeedbackForm().generateFaillureFeedbackResultForm()));
			model.put("data", returnString);
			return new ModelAndView("data", model);
		}

		//respond back to UF service
		String type = responseForm.getType();
		if(type.equalsIgnoreCase(RADIO)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(CHECK)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(ACK)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(ABORT)){
			String[] data = responseForm.getData();
			if(data[0].equalsIgnoreCase("true")){
				userFeedback.submitImplicitResponse(responseForm.getID(), true);
			}else{
				userFeedback.submitImplicitResponse(responseForm.getID(), false);
			}
		}else if(type.equalsIgnoreCase(NOTIFICATION)){
			userFeedback.submitImplicitResponse(responseForm.getID(), true);
		}else{
			LOG.error("Did not recognise response form type from AJAX");
			String returnString = gsonMgr.toJson((new FeedbackForm().generateFaillureFeedbackResultForm()));
			model.put("data", returnString);
			return new ModelAndView("data", model);
		}
		
		String returnString = gsonMgr.toJson((new FeedbackForm().generateSuccessFeedbackResultForm()));
		model.put("data", returnString);
		return new ModelAndView("data", model);
	}
	
	//TODO : Quick fix for pilot
	@RequestMapping(value = "/{cssId}/get_form.html", method = RequestMethod.POST)
	public ModelAndView submitResponseWithId(@RequestParam("data") String jsonString){
		Map<String, Object> model = new HashMap<String, Object>();
		LOG.info("[Submit response]"+jsonString);
		
		//convert json string back to Java types
		FeedbackForm responseForm; 
		try{
			responseForm = gsonMgr.fromJson(jsonString, FeedbackForm.class);
		}catch(Exception e){
			LOG.error("Submitted response - incorrect format");
			String returnString = gsonMgr.toJson((new FeedbackForm().generateFaillureFeedbackResultForm()));
			model.put("data", returnString);
			return new ModelAndView("data", model);
		}

		//respond back to UF service
		String type = responseForm.getType();
		if(type.equalsIgnoreCase(RADIO)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(CHECK)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(ACK)){
			String[] data = responseForm.getData();
			List<String> result = new ArrayList<String>();
			for(int i = 0; i<data.length; i++){
				result.add(data[i]);
			}
			userFeedback.submitExplicitResponse(responseForm.getID(), result);
		}else if(type.equalsIgnoreCase(ABORT)){
			String[] data = responseForm.getData();
			if(data[0].equalsIgnoreCase("true")){
				userFeedback.submitImplicitResponse(responseForm.getID(), true);
			}else{
				userFeedback.submitImplicitResponse(responseForm.getID(), false);
			}
		}else if(type.equalsIgnoreCase(NOTIFICATION)){
			userFeedback.submitImplicitResponse(responseForm.getID(), true);
		}else{
			LOG.error("Did not recognise response form type from AJAX");
			String returnString = gsonMgr.toJson((new FeedbackForm().generateFaillureFeedbackResultForm()));
			model.put("data", returnString);
			return new ModelAndView("data", model);
		}
		
		String returnString = gsonMgr.toJson((new FeedbackForm().generateSuccessFeedbackResultForm()));
		model.put("data", returnString);
		return new ModelAndView("data", model);
	}
	
}
