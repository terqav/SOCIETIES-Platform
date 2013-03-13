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
package org.societies.css.devicemgmt.DeviceDriverSimulator.actions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.css.devicemgmt.model.DeviceActionsConstants;
import org.societies.api.css.devicemgmt.model.DeviceStateVariableConstants;
import org.societies.css.devicemgmt.DeviceDriverSimulator.Screen;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.MessageStateVariable;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class DisplayMessageAction implements IAction{

	final private String NAME = DeviceActionsConstants.SCREEN_DISPLAY_MESSAGE_ACTION;
	
	final private String OUTPUT = null;
	
	final private String INPUT = DeviceStateVariableConstants.MESSAGE_STATE_VARIABLE;

	private static Logger LOG = LoggerFactory.getLogger(DisplayMessageAction.class);
	
	private List<String> outputArguments;
	private List<String> inputArguments;
	
	private Screen screen;
	
	private MessageStateVariable messageStateVariable;
	
	
	
	public DisplayMessageAction(Screen screen , MessageStateVariable messageStateVariable) {

		this.screen = screen;
		this.messageStateVariable = messageStateVariable;
		
		inputArguments = new ArrayList<String>();
		this.inputArguments.add(INPUT);
	}


	@Override
	public List<String> getInputArgumentNames() {
		return inputArguments;
	}


	@Override
	public String getName() {
		return NAME;
	}


	@Override
	public List<String> getOutputArgumentNames() {
		return null;
	}

	
	@Override
	public IDeviceStateVariable getStateVariable(String argumentName) {

		return messageStateVariable;
	}


	@Override
	public Dictionary<String, Object> invokeAction(Dictionary<String, Object> arguments) {
	
		String msg = (String)arguments.get(INPUT);
		
		LOG.info("DisplayMessageAction info: yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy invokeAction " + msg);
		
		screen.sendMessageToScreen(msg);
		
		return null;
	}



	@Override
	public String getActionDescription() {
		
		return "Used to display somthing to the TV screen";
	}

	@Override
	public String getActionName() {
		return "Display Message";
	}

}
