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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager.InvalidIdentity;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

/**
 * @author Eliza
 *
 */
public class IDSPreferenceManager {

	private final PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;


	public IDSPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		
	}
	private IIdentity evaluateIDSPreferenceIrrespectiveOfRequestor(IAgreement agreement, List<IIdentity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);

			if (model!=null){
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					outcomes.add(outcome);
				}
			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}

	private IIdentity askUserToSelectIIdentity(IAgreement agreement, List<IIdentity> candidateIdentities){


		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : candidateIdentities){

			strCandidateIDs.add(userId.toString());
		}

		String s = "";
		if (agreement.getRequestor() instanceof RequestorService){
			s = this.askUserToSelectIdentityForStartingService((RequestorService) agreement.getRequestor(), strCandidateIDs);
		}else if (agreement.getRequestor() instanceof RequestorCis){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCis) agreement.getRequestor(), strCandidateIDs);
		}
		


		for (IIdentity id : candidateIdentities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	private String askUserToSelectIdentityForStartingService(RequestorService requestor, List<String> strCandidates){
		return strCandidates.get(0);
		/*return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for starting session with service:\n",
						"provided by: "+requestor.getRequestorId().toString()+
						"\nwith serviceID: "+requestor.getRequestorServiceId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));*/
	}
	
	private String askUserToSelectIdentityForJoiningCIS(RequestorCis requestor, List<String> strCandidates){
		return strCandidates.get(0);
/*		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for joining CIS:\n", 
						"CIS id: "+requestor.getCisRequestorId().toString()+
						 "\nadministered by: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));*/
	}
	
	private String askUserToSelectIdentityForInteractingWithCSS(Requestor requestor, List<String> strCandidates){
		return strCandidates.get(0);
/*		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for interacting with  CSS:\n", 
						"CSS id: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));
*/	}
	private IIdentity evaluateIDSPreferenceBasedOnProviderDPI(IAgreement agreement, List<IIdentity> identities){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<identities.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(identities.get(i));
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			if (model!=null){
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					outcomes.add(outcome);
				}

			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}
	private IIdentity evaluateIDSPreferenceBasedOnAllInfo(IAgreement agreement, List<IIdentity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			/*			if (model == null){
				JOptionPane.showMessageDialog(null, "prefCache returned null model for details:"+details.toString());
			}*/
			if (model!=null){
				
			
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					//JOptionPane.showMessageDialog(null, "Evaluation returned non-null outcome");
					outcomes.add(outcome);
				}
			}
		}	

		if (outcomes.size()==0){
			//JOptionPane.showMessageDialog(null, "No preference was applicable. returning null dpi");
			return null;
		}

		if (outcomes.size()==1){
			//JOptionPane.showMessageDialog(null, "ONE preference was applicable. returning dpi:"+outcomes.get(0).getIIdentity());
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			//JOptionPane.showMessageDialog(null, "MULTIPLE preferences were applicable. asking the user");
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deleteIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails)
	 */
	@Override
	public void deleteIDSPreference(IDSPreferenceDetails details){
		this.prefCache.removeIDSPreference(details);
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deleteIDSPreference(org.societies.api.identity.IIdentity)
	 */
	@Override
	public void deleteIDSPreference(IIdentity userDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userDPI);
		this.prefCache.removeIDSPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deleteIDSPreference(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void deleteIDSPreference(Requestor requestor, IIdentity userIdentity) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userIdentity);
		details.setRequestor(requestor);
		this.prefCache.removeIDSPreference(details);
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#storeIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails, org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference)
	 */
	@Override
	public void storeIDSPreference(IDSPreferenceDetails details, IPrivacyPreference preference) {
		IPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details.getAffectedDPI(),preference);
		
		if (details.getRequestor()!=null){
			((IDSPrivacyPreferenceTreeModel) model).setRequestor(details.getRequestor());
		}
		this.prefCache.addIDSPreference(details, model);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferenceDetails()
	 */
	@Override
	public List<IDSPreferenceDetails> getIDSPreferenceDetails() {

		return this.prefCache.getIDSPreferenceDetails();
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferences(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences( Requestor requestor, IIdentity affectedIIdentity) {
		return this.prefCache.getIDSPreferences(affectedIIdentity, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferences(org.societies.api.identity.IIdentity)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity userDPI) {
		return this.prefCache.getIDSPreferences(userDPI);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails)
	 */
	@Override
	public IPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetails details) {
		return this.prefCache.getIDSPreference(details);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails)
	 */
	@Override
	public IIdentity evaluateIDSPreference(IDSPreferenceDetails details) {
		IPrivacyPreferenceTreeModel model = this.prefCache.getIDSPreference(details);
		IPrivacyOutcome out = this.evaluatePreference(model.getRootPreference());
		if (out instanceof IdentitySelectionPreferenceOutcome){
			return ((IdentitySelectionPreferenceOutcome) out).getIdentity();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIdSPreference(org.societies.api.identity.Requestor)
	 */
	public IIdentity evaluateIdSPreference(Requestor requestor){
		List<IDSPreferenceDetails> details = this.prefCache.getIDSPreferenceDetails();
		List<IIdentity> identities = new ArrayList<IIdentity>();
		for (IDSPreferenceDetails detail : details){
			if (detail.getRequestor().equals(requestor)){
				identities.add(this.evaluateIDSPreference(detail));
			}
		}
		
		if (identities.size()==0){
			return null;
		}
		if (identities.size()==1){
			return identities.get(0);
		}
		
		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : identities){

			strCandidateIDs.add(userId.toString());
		}
		
		String createNew = "Create new Identity";
		strCandidateIDs.add(createNew);
		String s = "";
		if (requestor instanceof RequestorService){
			s = this.askUserToSelectIdentityForStartingService((RequestorService) requestor, strCandidateIDs);
		}else if (requestor instanceof RequestorCis){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCis) requestor, strCandidateIDs);
		}else{
			s = this.askUserToSelectIdentityForInteractingWithCSS(requestor, strCandidateIDs);
		}
		
		for (IIdentity id : identities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIDSPreferences(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement, java.util.List)
	 */
	@Override
	public IIdentity evaluateIDSPreferences(IAgreement agreement, List<IIdentity> dpis){

		IIdentity selectedIIdentity = this.evaluateIDSPreferenceBasedOnAllInfo(agreement, dpis);

		if (selectedIIdentity==null){
			selectedIIdentity = this.evaluateIDSPreferenceBasedOnProviderDPI(agreement, dpis);
			if (selectedIIdentity == null){
				selectedIIdentity = this.evaluateIDSPreferenceIrrespectiveOfRequestor(agreement, dpis);
				if (selectedIIdentity == null){
					return null;
				}else if (selectedIIdentity instanceof InvalidIdentity){
					//user wants to use a new DPI
					return null;
				}else{
					return selectedIIdentity;
				}
			}else if (selectedIIdentity instanceof InvalidIdentity){
				//user wants to use a new DPI
				return null;
			}else{
				return selectedIIdentity;
			}
		}else if (selectedIIdentity instanceof InvalidIdentity){
			//user wants to use a new DPI
			return null;
		}else{
			return selectedIIdentity;
		}

	}
	
	public void addIDSDecision(Requestor requestor, IIdentity selectedDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails (selectedDPI);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = this.getIDSPreference(details);
	}


	private class InvalidIdentity implements IIdentity{

		public InvalidIdentity(){
			
		}

		@Override
		public String getJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getBareJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDomain() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IdentityType getType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
