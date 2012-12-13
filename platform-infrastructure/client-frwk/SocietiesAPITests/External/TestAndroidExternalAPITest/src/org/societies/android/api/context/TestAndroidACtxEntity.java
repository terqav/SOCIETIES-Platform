package org.societies.android.api.context;

import javax.xml.datatype.DatatypeConfigurationException;

import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.android.api.context.model.CtxModelBeanTranslator;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;



public class TestAndroidACtxEntity extends AndroidTestCase{

	private static final String LOG_TAG = TestAndroidACtxEntity.class.getName();
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() {
		ACtxEntityIdentifier entityId = new ACtxEntityIdentifier("ownerId", "type", (long) 15);
		ACtxEntity entity = new ACtxEntity(entityId);

		assertNotNull(entityId);
		assertNotNull(entity);

		entityId.setOwnerId("ownerId2");
//		entityId.setScheme();
		entityId.setType("type2");
		entityId.setUri("uri");
		
		assertEquals(0, entityId.describeContents());
		assertEquals(0, entity.describeContents());
		
        Parcel parcel = Parcel.obtain();
        entityId.writeToParcel(parcel, 0);
        entity.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACtxEntityIdentifier createFromParcel = ACtxEntityIdentifier.CREATOR.createFromParcel(parcel);
        ACtxEntity createFromParcelEntity = ACtxEntity.CREATOR.createFromParcel(parcel);
       
        assertEquals(entityId.getOperatorId(), createFromParcel.getOperatorId());
        assertEquals(entityId.getOwnerId(), createFromParcel.getOwnerId());
        assertEquals(entityId.getType(), createFromParcel.getType());
        //assertEquals(entityId.getUri(), createFromParcel.getUri());
        assertEquals(entityId.getModelType(), createFromParcel.getModelType());
        assertEquals(entityId.getObjectNumber(), createFromParcel.getObjectNumber());
        //assertEquals(entityId.getScheme(), createFromParcel.getScheme());
        
        assertEquals(entity.getOwnerId(), createFromParcelEntity.getOwnerId());
        assertEquals(entity.getType(), createFromParcelEntity.getType());
//        assertEquals(entity.getId(), createFromParcelEntity.getId());
	}
	
	@MediumTest
	public void testConvertCtxEntity(){
		
/*		final IndividualCtxEntity ownerEnt = 
				internalCtxBroker.retrieveIndividualEntity(cssMockIdentity).get();
		assertNotNull(ownerEnt);
		assertEquals(OWNER_IDENTITY_STRING, ownerEnt.getId().getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, ownerEnt.getType());
		assertFalse(ownerEnt.getAttributes(CtxAttributeTypes.ID).isEmpty());
		assertEquals(1, ownerEnt.getAttributes(CtxAttributeTypes.ID).size());
		
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
		CtxEntityBean entBean = ctxBeanTranslator.fromCtxEntity(ownerEnt);
		System.out.println("******** entBean "+entBean.toString() );
		System.out.println("******** entBean "+entBean.toString() );
		CtxEntity ctxEntity = ctxBeanTranslator.fromCtxEntityBean(entBean);
		
		System.out.println("******** ent "+ctxEntity.getId().toString() );
		if(ctxEntity.equals(ownerEnt)){
			System.out.println("equal obj ");
		}else System.out.println("not equal obj ");
		
		if(ctxEntity.getId().equals(ownerEnt.getId())){
			System.out.println("equal ids");
		}else System.out.println("not equal ids");
	
		for( CtxAttribute attr : ctxEntity.getAttributes()){
			System.out.println("attrs"+ attr.getId());	
		}
		
		CtxModelObjectBean ctxObjBean = ctxBeanTranslator.fromCtxModelObject(ctxEntity);
		System.out.println("ctxObjBean "+ctxObjBean );
		System.out.println("ctxObjBean  id "+ctxObjBean.getId().toString() );
		
		CtxModelObject ctxObj = ctxBeanTranslator.fromCtxModelObjectBean(ctxObjBean);
		System.out.println("ctxObj "+ctxObj );
		System.out.println("ctxObj  id "+ctxObj.getId().toString() );
	*/	
		
		
		
		ACtxEntityIdentifier entityId = new ACtxEntityIdentifier("ownerId", "type", (long) 15);
//		entityId.setOwnerId("ownerId");
//		entityId.setType("type");
//		entityId.setUri("uri");
		
		ACtxEntity entity = new ACtxEntity(entityId);
		assertNotNull(entity);

		CtxModelBeanTranslator translator = CtxModelBeanTranslator.getInstance();
		CtxEntityBean entityBean;
		try {
			entityBean = translator.fromCtxEntity(entity);
			
			Log.d(LOG_TAG, "******** entityBean "+entityBean.toString());
			ACtxEntity actxEntity;
			actxEntity = translator.fromCtxEntityBean(entityBean);
			Log.d(LOG_TAG, "******** entity "+actxEntity.getId().toString());		
			
			CtxModelObjectBean ctxObjBean = translator.fromCtxModelObject(actxEntity);
			Log.d(LOG_TAG, "ctxObjBean " + ctxObjBean);
			Log.d(LOG_TAG, "ctxObjBean is " + ctxObjBean.getId().toString());
			
			ACtxModelObject actxObj = translator.fromCtxModelObjectBean(ctxObjBean);
			Log.d(LOG_TAG, "ctxObj " + actxObj);
			Log.d(LOG_TAG, "ctxObj id " + actxObj.getId().toString());

		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
/*		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		String serviceIDString = "TEST_SERVICE_ID";
		try {
			serviceID.setIdentifier(new URI(serviceIDString));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		serviceID.setServiceInstanceIdentifier(serviceIDString);
		
		action.setServiceID(serviceID);
		action.setServiceType("TEST_SERVICE_TYPE");
		action.setparameterName("TEST_PARAMETER_NAME");
		action.setvalue("TEST_VALUE");
		
		//convert to aaction and check
		AAction aaction = AAction.convertAction(action);
		assertEquals(aaction.getServiceID().getIdentifier(), action.getServiceID().getIdentifier());
		assertEquals(aaction.getServiceID().getServiceInstanceIdentifier(), action.getServiceID().getServiceInstanceIdentifier());
		assertEquals(aaction.getServiceType(), action.getServiceType());
		assertEquals(aaction.getparameterName(), action.getparameterName());
		assertEquals(aaction.getvalue(), action.getvalue());*/
	}
}
