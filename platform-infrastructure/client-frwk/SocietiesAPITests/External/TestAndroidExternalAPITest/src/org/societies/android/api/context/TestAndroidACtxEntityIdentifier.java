package org.societies.android.api.context;

//import org.societies.api.internal.css.management.CSSManagerEnums;
//import org.societies.api.schema.cssmanagement.CssNode;

import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;



public class TestAndroidACtxEntityIdentifier extends AndroidTestCase{

//	public static final String TEST_IDENTITY_1 = "node11";
//	public static final String TEST_IDENTITY_2 = "node22";

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

/*	@MediumTest
	public void testConstructor() throws Exception {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());
	}
	
	@MediumTest
	public void testAlternativeConstructor() {
		AndroidCSSNode cssNode = new AndroidCSSNode();
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Rich.ordinal());
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeType.Rich.ordinal(), cssNode.getType());
		assertEquals(CSSManagerEnums.nodeStatus.Hibernating.ordinal(), cssNode.getStatus());
	}
	
	@MediumTest
	public void testConversion() {
		CssNode cssNode = new CssNode();
		assertNotNull(cssNode);
		
		cssNode.setIdentity(TEST_IDENTITY_1);
		cssNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode.setType(CSSManagerEnums.nodeType.Cloud.ordinal());
		
		assertEquals(TEST_IDENTITY_1, cssNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), cssNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), cssNode.getType());

		AndroidCSSNode aNode = AndroidCSSNode.convertCssNode(cssNode);

		assertEquals(TEST_IDENTITY_1, aNode.getIdentity());
		assertEquals(CSSManagerEnums.nodeStatus.Available.ordinal(), aNode.getStatus());
		assertEquals(CSSManagerEnums.nodeType.Cloud.ordinal(), aNode.getType());
		
	}*/
	
	@MediumTest
	public void testParcelable() {
		ACtxEntityIdentifier entityId = new ACtxEntityIdentifier("ownerId", "type", (long) 15);
		assertNotNull(entityId);
		
		entityId.setOwnerId("ownerId2");
//		entityId.setScheme();
		entityId.setType("type2");
		entityId.setUri("uri");
		
		assertEquals(0, entityId.describeContents());
		
        Parcel parcel = Parcel.obtain();
        entityId.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACtxEntityIdentifier createFromParcel = ACtxEntityIdentifier.CREATOR.createFromParcel(parcel);
       
        assertEquals(entityId.getOperatorId(), createFromParcel.getOperatorId());
        assertEquals(entityId.getOwnerId(), createFromParcel.getOwnerId());
        assertEquals(entityId.getType(), createFromParcel.getType());
        //assertEquals(entityId.getUri(), createFromParcel.getUri());
        assertEquals(entityId.getModelType(), createFromParcel.getModelType());
        assertEquals(entityId.getObjectNumber(), createFromParcel.getObjectNumber());
        //assertEquals(entityId.getScheme(), createFromParcel.getScheme());
	}
}
