package org.societies.privacytrust.privacyprotection.api.internal;

import java.util.List;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.PrivacyPreferenceException;
import org.societies.privacytrust.privacyprotection.api.model.preference.Action;
import org.societies.privacytrust.privacyprotection.api.model.preference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.preference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.IAgreement;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponsePolicy;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 19:00:10
 */
public interface IPrivacyPreferenceManager {

	/**
	 * Method to check the access control permission
	 * @return	ALLOW or BLOCK
	 * 
	 * @param ctxId    the affected context identifier
	 * @param action    the action requested
	 * @param requestorIdentity    the identity of requestor
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	public PrivacyOutcomeConstants checkPermission(ICtxAttributeIdentifier ctxId, Action action, EntityIdentifier requestorIdentity)
	  throws PrivacyPreferenceException;

	/**
	 * Method to check the access control permission
	 * @return	ALLOW or BLOCK
	 * 
	 * @param contextType    the affected context identifier
	 * @param action    the action requested
	 * @param requestorIdentity    the identity of requestor
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	public PrivacyOutcomeConstants checkPermission(String contextType, Action action, EntityIdentifier requestorIdentity)
	  throws PrivacyPreferenceException;

	/**
	 * Method to delete the IDS preference referring to this dpi (only deletes the
	 * generic IDS preference)
	 * 
	 * @param userDPI    the dpi of the user to which the preference refers
	 */
	public void deleteIDSPreference(EntityIdentifier userDPI);

	/**
	 * Method to delete the IDS preference referring to the provided parameters
	 * 
	 * @param userIdentity    the DPI of the user to which the preference refers
	 * @param serviceIdentity    the DPI of the provider to which the preference
	 * refers
	 * @param serviceID    the serviceID of the service provided by the given
	 * serviceDPI
	 */
	public void deleteIDSPreference(EntityIdentifier userIdentity, EntityIdentifier serviceIdentity, ServiceResourceIdentifier serviceID);

	/**
	 * Method to delete the IDS preference referring to this user dpi and provider dpi
	 * (only deletes the generic preference for user-provider DPI combination)
	 * 
	 * @param userIdentity    the DPI of the user to which the preference refers
	 * @param serviceIdentity    the DPI of the provider to which the preference refers
	 */
	public void deleteIDSPreference(EntityIdentifier userIdentity, EntityIdentifier serviceIdentity);

	/**
	 * Method to delete an existing PPN preference model (generic to a context type)
	 * 
	 * @param contextType    the context type to which the preference refers
	 */
	public void deletePPNPreference(String contextType);

	/**
	 * Method to delete an existsing PPN preference model
	 * @param ctxID	the affected ctxID. if the affected contextType is symloc then the
	 * ctxID will be the the ctxID of the symloc attribute as stored in the DB. In
	 * order to delete only the generic preference that affects any symloc attribute
	 * stored in the DB, insert null.
	 * 
	 * @param contextType    the affected contextType
	 * @param affectedCtxID
	 * @param requestorIdentity    the DPI of a requestor
	 */
	public void deletePPNPreference(String contextType, ICtxAttributeIdentifier affectedCtxID, EntityIdentifier requestorIdentity);

	/**
	 * Method to delete an existing PPN preference model
	 * @param ctxID	the affected ctxID. if the affected contextType is symloc then the
	 * ctxID will be the the ctxID of the symloc attribute as stored in the DB. In
	 * order to delete only the generic preference that affects any symloc attribute
	 * stored in the DB, insert null.
	 * 
	 * @param contextType    the affected contextType
	 * @param id
	 */
	public void deletePPNPreference(String contextType, ICtxAttributeIdentifier id);

	/**
	 * Method to request evaluation of identity selection preferences for a specific
	 * transaction
	 * @return			one DPI from the list of the DPIs or null if no DPIS in the list
	 * should be used
	 * 
	 * @param agreement    the agreement between the consumer and the provider
	 * @param identities    the list of DPIs that match the agreement.
	 */
	public EntityIdentifier evaluateIDSPreferences(IAgreement agreement, List<EntityIdentifier> identities);

	/**
	 * 
	 * @param request
	 */
	public ResponsePolicy evaluatePPNP(RequestPolicy request);

	/**
	 * Method to retrieve the outcomes of all PPN preferences that affect the given
	 * context type
	 * @return
	 * 
	 * @param contextType    the affected context type
	 */
	public List<IPrivacyOutcome> evaluatePreference(String contextType);

	/**
	 * 
	 * @param service_id
	 */
	public EntityIdentifier getIdSPreference(ServiceResourceIdentifier service_id);

	/**
	 * Method to retrieve the list of IDSPreferences that affect the given DPI and
	 * service provider
	 * @return				the list of preference models (each one containing the preference)
	 * 
	 * @param affectedIdentity    the affected user DPI in the preference
	 * @param serviceIdentity    the affected DPI of the provider (requestor)
	 */
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(EntityIdentifier affectedIdentity, EntityIdentifier serviceIdentity);

	/**
	 * Method to retrieve the list of IDSPreferences that affect the given DPI
	 * @return				the list of preference models (each one containing the preference)
	 * 
	 * @param affectedIdentity    the affected user DPI in the preference
	 */
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(EntityIdentifier affectedIdentity);

	/**
	 * Method to retrieve the PPNP preferences for a context type
	 * @return	the list of PPN models that affect this context type
	 * 
	 * @param contextType    the context type affected
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType);

	/**
	 * Method to retrieve the list of ppn preferences based on the given parameters
	 * @return				the list of PPNP models related to the supplied parameters
	 * 
	 * @param contextType    the context type affected
	 * @param ctxID    the ctxID affected
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, ICtxAttributeIdentifier ctxID);

	/**
	 * Method to retrieve the list of ppn preferences based on the given parameters
	 * @return				the list of PPNP models related to the supplied parameters
	 * 
	 * @param contextType    the context type affected
	 * @param ctxID    the ctxID affected
	 * @param requestorIdentity    the DPI of the requestor
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, ICtxAttributeIdentifier ctxID, EntityIdentifier requestorIdentity);

	/**
	 * Method to retrieve the list of ppn preferences based on the given parameters
	 * @return				the list of PPNP models related to the supplied parameters
	 * 
	 * @param contextType    the context type affected
	 * @param requestorIdentity    the DPI of the requestor
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, EntityIdentifier requestorIdentity);

	/**
	 * Method to retrieve the list of ppn preferences based on the given parameters
	 * @return				the list of PPNP models related to the supplied parameters
	 * 
	 * @param contextType    the context type affected
	 * @param requestorIdentity    the DPI of the requestor
	 * @param serviceID    the serviceID of the service provided by the requestor
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, EntityIdentifier requestorIdentity, ServiceResourceIdentifier serviceID);

}