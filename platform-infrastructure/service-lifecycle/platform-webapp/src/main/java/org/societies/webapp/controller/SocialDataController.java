package org.societies.webapp.controller;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.socialdata.SocialData;
import org.societies.webapp.models.SocialDataForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import sun.util.logging.resources.logging;


@Controller
public class SocialDataController {

	@Autowired
	private ISocialData socialdata;
	
	private static final String ADD				= "add";
	private static final String REMOVE			= "remove";
	private static final String FRIENDS			= "friends";
	private static final String PROFILES		= "profiles";
	private static final String GROUPS			= "groups";
	private static final String ACTIVITIES		= "activities";
	private static final String LIST			= "list";
	private static final String ID  	= "id";
	private static final String SNNAME  = "snName";
	private static final String TOKEN   = "token";
	
	/**
	 * OSGI service get auto injected
	 */
	
	public class connStub{
		public String image;
		public String id;
		
	}
	
	public ISocialData getSocialData() {
		return socialdata;
	}
	
	public void getSocialData(ISocialData socialData) {
		this.socialdata = socialData;
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SocialDataController.class);
	
	private String getIcon(String data){
		
		if (data==null) return "images/social_network.png";
		if (data=="")   return "images/social_network.png";
	
		if (data.contains("facebook"))      	return "images/Facebook.png";
		else if (data.contains("twitter"))   	return "images/Twitter.jpg";
		else if (data.contains("linkedin"))  	return "images/Linkedin.png";
		else if (data.contains("foursquare"))   return"images/Foursquare.png";
		else return "images/social_network.png";
	}
	
	private String getBaseURL(String data, String id){
		if (data==null) return "#";
		if (data=="")   return "#";
		
		
		data = data.toLowerCase();
		if (data.contains("facebook"))       return  "http://facebook.com/"+id;
		else if (data.contains("twitter"))   return  "http://api.twitter.com/1/users/lookup.json?user_id="+id;
		else if (data.contains("linkedin"))  return  "http://linkedin.com/"+id;
		else if (data.contains("foursquare")) return "http://foursquare.com/"+id;
		else return "#";
	}

	@RequestMapping(value = "/socialdata.html", method = RequestMethod.GET)
	public ModelAndView SocialDataForm() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		
//		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		SocialDataForm sdForm = new SocialDataForm();
		model.put("sdForm", sdForm);

		
		Iterator<ISocialConnector>it = socialdata.getSocialConnectors().iterator();
		String connLI="";
		
		while (it.hasNext()){
			ISocialConnector conn = it.next();
		    
		   
			connLI+="<li><img src='"+getSNIcon(conn)+"'> "+conn.getConnectorName()+" <a href=\"#\" onclick=\"disconnect('"+conn.getID()+"');\">Click here to disconnect</a></li>";
			 
		}
		
		model.put("connectors", connLI);
		return new ModelAndView("socialdata", model);
	}
	
	
	private String getSNIcon(ISocialConnector conn){
		try{
			if (conn.getConnectorName().equalsIgnoreCase("facebook"))     return "images/Facebook.png";
			else if (conn.getConnectorName().equalsIgnoreCase("twitter")) return "images/Twitter.jpg";
			else if (conn.getConnectorName().equalsIgnoreCase("linkedin")) return "images/Linkedin.png";
			else if (conn.getConnectorName().equalsIgnoreCase("foursquare")) return "images/Foursquare.png";
			else return "images/social_network.png";
		}
		catch(Exception ex){}
		return "images/social_network.png";

	}
	
	private ISocialConnector.SocialNetwork getSocialNetowkName(String name){
		
		
		if ("facebook".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Facebook;
		if ("FB".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Facebook;
		if ("twitter".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.twitter;
		if ("TW".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.twitter;
		if ("foursquare".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Foursquare;
		if ("FQ".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.Foursquare;
		if ("linkedin".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.linkedin;
		if ("LK".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.linkedin;
		if ("googleplus".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.googleplus;
		if ("G+".equalsIgnoreCase(name)) return ISocialConnector.SocialNetwork.googleplus;
		
		
		return null;
	}
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/socialdata.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid SocialDataForm sdForm, BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "Social Data Form error");
			return new ModelAndView("socialdata", model);
		}

		if (getSocialData() == null) {
			model.put("errormsg", "Social Data reference not avaiable");
			return new ModelAndView("error", model);
		}

		
		String method = sdForm.getMethod();
		String res		 = "This method is not handled yet";
		String content	 = " --- ";
		
		
			if (ADD.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "[" + method+"] new Social Connector ";
				HashMap <String, String> params = new HashMap<String, String>();
				params.put(ISocialConnector.AUTH_TOKEN, sdForm.getToken());
				
				String error="";
				try {
					
					ISocialConnector con = socialdata.createConnector(getSocialNetowkName(sdForm.getSnName()), params);
					error ="We are not able to create "+con.getConnectorName() +" connector!";
					socialdata.addSocialConnector(con);
				
					content   = "<b>Connector</b> ID:"+sdForm.getId() + " for " + sdForm.getSnName() +" with token: "+ sdForm.getToken() + "<br>";
					model.put("sdForm", sdForm);
					
					Iterator<ISocialConnector>it = socialdata.getSocialConnectors().iterator();
					String connLI="";
					
					while (it.hasNext()){
						ISocialConnector conn = it.next();
					    
						connLI+="<li><img src='"+getSNIcon(conn)+"'> "+
						conn.getConnectorName()+" <a href=\"#\" onclick=\"disconnect('"+
						conn.getID()+"');\">Click here to disconnect</a></li>";
						 
					}
					
					socialdata.updateSocialData();   // this is required to read all the SN Data.... (can take a while).
					
					model.put("connectors", connLI);
					return new ModelAndView("socialdata", model);
				}
				
				catch (Exception e) {
					res       = "Internal Error";
					content  = "<p> Unable to generate a connecotor with those parameters <p>";
					content  +="Error type is "+error + " trace: "+e.getMessage();
					content  += "<ul><li> Social Network:"+sdForm.getSnName()+"</li>";
					content  += "<li> Method:"+sdForm.getMethod() + "</li>";
					Iterator<String>  it = params.keySet().iterator();
					while(it.hasNext()){
						String k = it.next();
						content  += "<li>"+ k +": " +params.get(k)+"</li>";		
					}
					content  += "</ul>";
					e.printStackTrace();
				}
				
				
					
			}
			// This should be deprecated
			else if (LIST.equalsIgnoreCase(method)) {
					
					// DO add Connectore HERE
					res       = "<h4>Connector List  </h4>";
					Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
					
					content   = "<ul>";
					while (it.hasNext()){
					  ISocialConnector conn = it.next();
				  	  content   +="<li>" +conn.getConnectorName() +"- ID: "+conn.getID()+"</li>";
				  	  
					}
				    content+= "<br>";
						
			}
			else if (REMOVE.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res    = "<a href=' socialdata.html'> Back to my Social Area </a>";
				if ("null".equals(sdForm.getId())){
					content = "<p> Please set a valid Connector ID</p>";
				}
				else {
					try {
				
						content +="<h2> Connector REMOVED</h2>";
						socialdata.removeSocialConnector(sdForm.getId());
						content   += "<p> Connector ID:"+sdForm.getId()+  "has been removed correctly</p>";
					} catch (Exception e) {
						res       = "Internal Error";
						content = "<p> Unable to remove this connector due to:</p>";
						content +="<h1>"+e.getMessage()+"</h1>";
						e.printStackTrace();
					}
					
				}
			}
			else if (FRIENDS.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res    = "<a href=' socialdata.html'> Back to my Social Area </a>";
			
				List<Person>friends = (List<Person>)socialdata.getSocialPeople();
				if (friends==null) {
					logger.debug("Social Friends is Null");
					friends= new ArrayList<Person>();  // create empty to avoid nullpointerexception
				}
				
				logger.debug(" PRINT Social Friends:"+friends.size());
				Iterator<Person> it = friends.iterator();
				content ="<h2> My Social Network frinds </h2>";
				content +="<ul>";
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					Person p= it.next();
					
					String name = "Username NA";;
					String domain ="";
					String img = "";
					String link="";
					try{
						if (p.getName()!=null){
							if (p.getName().getFormatted()!=null)
								name = p.getName().getFormatted();
							else {
								if(p.getName().getFamilyName()!=null) name = p.getName().getFamilyName();
								if(p.getName().getGivenName()!=null){
									if (name.length()>0)  name+=" ";
									name +=p.getName().getGivenName();
								}
							}
							
						}
						
						if (p.getAccounts()!=null){
							if (p.getAccounts().size()>0) {
								domain = p.getAccounts().get(0).getDomain();
							}
						}
						String id = p.getId();
						if (p.getId().contains(":")){
							id = p.getId().split(":")[1];
						}
						img   = "<img width='20px' src='"+getIcon(domain) +"'>";
						link  = "<a href='"+ getBaseURL(domain,id)  + "' sn="+domain+">" + name + "</a>";
						
					}
					
					catch(Exception ex){
						logger.error("Error while parsing the Person OBJ");
						ex.printStackTrace();
					}
					
 					content +="<li> "+ img + "[ID]["+p.getId() +"]"+ link +"]</li>" ;
					
					
				}
				content   += "</ul>";
					
			}
			else if (PROFILES.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res    = "<a href='socialdata.html'> Back to my Social Area </a>";
				
				
				List<Person> list = (List<Person>)socialdata.getSocialProfiles();
				
				content ="<h2> My Social Profiles </h2>";
				content +="<ul>";
				for(Person p : list){
					
					String link ="";
					String img  =" --- ";
					String domain = "";
					try{
					
						if (p.getAccounts()!=null){
							if (p.getAccounts().size()>0){
								Account account = p.getAccounts().get(0);
								if (account.getDomain()!=null) domain = account.getDomain();
							}
						}
						String name = p.getId();
						if (p.getName()!=null) {
							if (p.getName().getFormatted() !=null) name= p.getName().getFormatted();
						}
						else if (p.getNickname()!=null) name = p.getNickname();
						
						String id = p.getId();
						if (p.getId().contains(":")){
							id = p.getId().split(":")[1];
						}
						
						img   = "<img width='20px' src='"+getIcon(p.getId()) +"'>";
						link  = "<a href='"+getBaseURL(domain,id) + "'>" + name + "</a>";
						
						content +="<li> "+ img + link +"</li>";
					}
					catch(Exception ex){
						ex.printStackTrace();
						content +="<li> "+ img + link +"</li>";
					}

				}
				content   += "</ul>";
					
			}
			else if (GROUPS.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res    = "<a href='socialdata.html'> Back to my Social Area </a>";
				
				List<Group>list = (List<Group>)socialdata.getSocialGroups();
				
				Iterator<Group> it = list.iterator();
				content ="<h2> My Social Groups </h2>";
				content +="<ul>";
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					Group g= it.next();
					try{
						
						content +="<li> ID:"+g.getId() +" Title:"+ g.getDescription() + "</li>" ;
					}
					catch (Exception ex){
						ex.printStackTrace();
						content +="<li> Title:"+ g.getDescription() + "</li>" ;
					}
					
				}
				content   += "</ul>";
					
			}
			else if (ACTIVITIES.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res    = "<a href='socialdata.html'> Back to my Social Area </a>";
				content ="<h2> My Social Activities </h2>";
				content +="<ul>";
				
				List<ActivityEntry>list = (List<ActivityEntry>)socialdata.getSocialActivity();
				for (ActivityEntry entry :list){
					try{
						
						content +="<li>"
								+"<img width='20px' id='"+entry.getId() + "' src='"+getIcon(entry.getId())+"'>" 
								+ entry.getActor().getDisplayName() + " "
								+ entry.getVerb() + " --> "
								+ entry.getContent() +
								"</li>" ;
					}
					catch(Exception ex){
						content +="<li> " + entry.getActor().getDisplayName() + " "+ entry.getVerb() + " --> "+entry.getContent() +"</li>" ;
						
					}
				}
				content   += "</ul>";
			}
			else {
				content = "<p> Request method:"+method+ " that is not yet implmented [TBD]</p>";
			}

		
			model.put("result_title", 	res);
			model.put("result_content", content);
			
		
		
		
		
		return new ModelAndView("socialdataresult", model);
		

	}
}