package org.societies.css.cssRegistry;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.css.cssRegistry.model.CssNodeEntry;
import org.societies.css.cssRegistry.model.CssRegistryEntry;

public class CssRegistry implements ICssRegistry {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(CssRegistry.class);

	public CssRegistry() {
		log.info("CSS registry bundle instantiated.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public CssInterfaceResult registerCss(CssRecord cssRecord)
			throws CssRegistrationException {
		// TODO Auto-generated method stub

		CssInterfaceResult result = new CssInterfaceResult();

		Session session = sessionFactory.openSession();
		CssRegistryEntry tmpRegistryEntry = null;
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			for (CssNode cssNode : cssRecord.getCssNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), false);
				session.save(tmpNodeEntry);
			}

			for (CssNode cssNode : cssRecord.getArchiveCSSNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), true);
				session.save(tmpNodeEntry);
			}

			tmpRegistryEntry = new CssRegistryEntry(
					cssRecord.getCssHostingLocation(),
					cssRecord.getCssIdentity(), cssRecord.getCssInactivation(),
					cssRecord.getCssRegistration(), cssRecord.getCssUpTime(),
					cssRecord.getDomainServer(), cssRecord.getEmailID(),
					cssRecord.getEntity(), cssRecord.getForeName(),
					cssRecord.getHomeLocation(), cssRecord.getIdentityName(),
					cssRecord.getImID(), cssRecord.getName(),
					cssRecord.getPassword(), cssRecord.getPresence(),
					cssRecord.getSex(), cssRecord.getSocialURI(),
					cssRecord.getStatus());

			session.save(tmpRegistryEntry);

			t.commit();
			log.debug("Css Details saved.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		result.setResultStatus(true);
		result.setProfile(cssRecord);

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#unregisterCss
	 * (org.societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public void unregisterCss(CssRecord cssRecord)
			throws CssRegistrationException {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		CssRegistryEntry tmpRegistryEntry = null;
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			for (CssNode cssNode : cssRecord.getCssNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), false);
				session.delete(tmpNodeEntry);
			}

			for (CssNode cssNode : cssRecord.getArchiveCSSNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), true);
				session.delete(tmpNodeEntry);
			}

			tmpRegistryEntry = new CssRegistryEntry(
					cssRecord.getCssHostingLocation(),
					cssRecord.getCssIdentity(), cssRecord.getCssInactivation(),
					cssRecord.getCssRegistration(), cssRecord.getCssUpTime(),
					cssRecord.getDomainServer(), cssRecord.getEmailID(),
					cssRecord.getEntity(), cssRecord.getForeName(),
					cssRecord.getHomeLocation(), cssRecord.getIdentityName(),
					cssRecord.getImID(), cssRecord.getName(),
					cssRecord.getPassword(), cssRecord.getPresence(),
					cssRecord.getSex(), cssRecord.getSocialURI(),
					cssRecord.getStatus());

			session.delete(tmpRegistryEntry);

			t.commit();
			log.debug("Css Details unregistered.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public CssRecord getCssRecord() throws CssRegistrationException {
		Session session = sessionFactory.openSession();
		CssRecord cssDetails = new CssRecord();
		CssNode cssNodeDetails = null;

		Transaction t = session.beginTransaction();
		try {

			List<CssRegistryEntry> tmpRegistryEntryList = session
					.createCriteria(CssRegistryEntry.class).list();

			cssDetails.setDomainServer(tmpRegistryEntryList.get(0)
					.getDomainServer());
			cssDetails.setCssHostingLocation(tmpRegistryEntryList.get(0)
					.getCssHostingLocation());
			cssDetails.setEntity(tmpRegistryEntryList.get(0).getEntity());
			cssDetails.setForeName(tmpRegistryEntryList.get(0).getForeName());
			cssDetails.setName(tmpRegistryEntryList.get(0).getName());
			cssDetails.setIdentityName(tmpRegistryEntryList.get(0)
					.getIdentityName());
			cssDetails.setPassword(tmpRegistryEntryList.get(0).getPassword());
			cssDetails.setEmailID(tmpRegistryEntryList.get(0).getEmailID());
			cssDetails.setImID(tmpRegistryEntryList.get(0).getImID());
			cssDetails.setSocialURI(tmpRegistryEntryList.get(0).getSocialURI());
			cssDetails.setSex(tmpRegistryEntryList.get(0).getSex());
			cssDetails.setHomeLocation(tmpRegistryEntryList.get(0)
					.getHomeLocation());
			cssDetails.setCssIdentity(tmpRegistryEntryList.get(0)
					.getCssIdentity());
			cssDetails.setStatus(tmpRegistryEntryList.get(0).getStatus());
			cssDetails.setCssRegistration(tmpRegistryEntryList.get(0)
					.getCssRegistration());
			cssDetails.setCssInactivation(tmpRegistryEntryList.get(0)
					.getCssInactivation());
			cssDetails.setCssUpTime(tmpRegistryEntryList.get(0).getCssUpTime());
			cssDetails.setPresence(tmpRegistryEntryList.get(0).getPresence());

			List<CssNodeEntry> tmpNodeRegistryEntryList = session
					.createCriteria(CssNodeEntry.class).list();

			for (CssNodeEntry savedNode : tmpNodeRegistryEntryList) {
				cssNodeDetails = new CssNode();
				cssNodeDetails.setIdentity(savedNode.getIdentity());
				cssNodeDetails.setType(savedNode.getType());
				cssNodeDetails.setStatus(savedNode.getStatus());

				if (savedNode.getArchived() == true)
					cssDetails.getArchiveCSSNodes().add(cssNodeDetails);
				else
					cssDetails.getCssNodes().add(cssNodeDetails);
			}

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return cssDetails;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.css.cssRegistry.ICssRegistry#registerCss(org
	 * .societies.api.internal.css.management.CSSRecord)
	 */
	@Override
	public void updateCssRecord(CssRecord cssDetails)
			throws CssRegistrationException {

		Session session = sessionFactory.openSession();
		CssNodeEntry tmpNodeEntry = null;

		Transaction t = session.beginTransaction();
		try {

			// Celar the nodeEntry table, we will repopulate it
			List<CssNodeEntry> tmpNodeRegistryEntryList = session
					.createCriteria(CssNodeEntry.class).list();
			session.delete(tmpNodeRegistryEntryList);

			for (CssNode cssNode : cssDetails.getCssNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), false);
				session.save(tmpNodeEntry);
			}

			for (CssNode cssNode : cssDetails.getArchiveCSSNodes()) {
				tmpNodeEntry = new CssNodeEntry(cssNode.getIdentity(),
						cssNode.getStatus(), cssNode.getType(), true);
				session.save(tmpNodeEntry);
			}

			List<CssRegistryEntry> tmpCssRegistryEntryList = session
					.createCriteria(CssRegistryEntry.class).list();
			
			// Now to update the main table
			tmpCssRegistryEntryList.get(0).setDomainServer(cssDetails.getDomainServer());
			tmpCssRegistryEntryList.get(0).setCssHostingLocation(cssDetails.getCssHostingLocation());
			tmpCssRegistryEntryList.get(0).setEntity(cssDetails.getEntity());
			tmpCssRegistryEntryList.get(0).setForeName(cssDetails.getForeName());
			tmpCssRegistryEntryList.get(0).setName(cssDetails.getName());
			tmpCssRegistryEntryList.get(0).setIdentityName(cssDetails.getIdentityName());
			tmpCssRegistryEntryList.get(0).setPassword(cssDetails.getPassword());
			tmpCssRegistryEntryList.get(0).setEmailID(cssDetails.getEmailID());
			tmpCssRegistryEntryList.get(0).setImID(cssDetails.getImID());
			tmpCssRegistryEntryList.get(0).setSocialURI(cssDetails.getSocialURI());
			tmpCssRegistryEntryList.get(0).setSex(cssDetails.getSex());
			tmpCssRegistryEntryList.get(0).setHomeLocation(cssDetails.getHomeLocation());
			tmpCssRegistryEntryList.get(0).setCssIdentity(cssDetails.getCssIdentity());
			tmpCssRegistryEntryList.get(0).setStatus(cssDetails.getStatus());
			tmpCssRegistryEntryList.get(0).setCssRegistration(cssDetails.getCssRegistration());
			tmpCssRegistryEntryList.get(0).setCssInactivation(cssDetails.getCssInactivation());
			tmpCssRegistryEntryList.get(0).setCssUpTime(cssDetails.getCssUpTime());
			tmpCssRegistryEntryList.get(0).setPresence(cssDetails.getPresence());
			
		
			session.update(tmpCssRegistryEntryList);
			
			t.commit();
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
			throw new CssRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

}