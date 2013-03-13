package org.societies.android.platform.ctxclient.comm.callbacks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.context.broker.impl.comm.ICtxCallback;

public class RetrieveCtxCallback implements ICtxCallback{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(RetrieveCtxCallback.class);
	
	private CtxModelObject result;

	@Override
	public void onCreatedEntity(CtxEntity retObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreatedAttribute(CtxAttribute retObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLookupCallback(List<CtxIdentifier> ctxIdsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRetrieveCtx(CtxModelObject ctxObj) {

		LOG.info("onRetrieveCtx retObject " +ctxObj);

		this.result = ctxObj;
		synchronized (this) {	            
			notifyAll();	        
		}
		LOG.info("onRetrieveCtx, notify all done");

	}
	
	@Override
	public void onUpdateCtx(CtxModelObject ctxObj) {
		// TODO Auto-generated method stub
	}
	
	public CtxModelObject getResult() {
		return this.result;
	}

	@Override
	public void onRetrievedEntityId(CtxEntityIdentifier ctxId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreatedAssociation(CtxAssociation retObject) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * @see org.societies.context.broker.impl.comm.ICtxCallback#onRemovedModelObject(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public void onRemovedModelObject(CtxModelObject ctxObj) {
		
		// TODO Auto-generated method stub
	}
}