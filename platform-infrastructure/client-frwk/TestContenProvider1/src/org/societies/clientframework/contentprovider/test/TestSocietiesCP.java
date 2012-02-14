package org.societies.clientframework.contentprovider.test;

import org.societies.android.platform.interfaces.IContentProvider;
import org.societies.api.android.internal.model.CredentialTable;
import org.societies.api.android.internal.model.ServiceTable;
import org.societies.clientframework.contentprovider.services.SocietiesCP;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class TestSocietiesCP extends ProviderTestCase2<SocietiesCP> {
 
	
	static int testNumber=1;
	public TestSocietiesCP(String providerAuthority) {
		super(SocietiesCP.class, IContentProvider.AUTHORITY);
		
	}
	
	

	protected void setUp() throws Exception {
		super.setUp();	
		
		
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		ContentProviderClient client = getContext().getContentResolver().acquireContentProviderClient(credentialUri);
		Log.v("TestContentProvider", "Start test "+testNumber++);
		
	}
	
	@SmallTest
	public void testQuery(){
        ContentProvider provider = getProvider();

        Uri uri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);

        Cursor cursor = provider.query(uri, null, null, null, null);

        assertNotNull(cursor);

        cursor = null;
        try {
            cursor = provider.query(Uri.parse("definitelywrong"), null, null, null, null);
            // we're wrong if we get until here!
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	@SmallTest
	public void testOnCreate() {
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		ContentProviderClient client = getContext().getContentResolver().acquireContentProviderClient(credentialUri);
		assertNotNull(client);
		
	}

	@SmallTest
	public void testDeleteUriStringStringArray() {
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		int count = getContext().getContentResolver().delete(credentialUri, "service = ?", new String[]{"test"});
		assertNotNull(count);
	}

	@SmallTest
	public void testGetTypeUri() {
		Uri serUri = Uri.parse(ServiceTable.SERVICE_DATA_URI); 
		String uri = getContext().getContentResolver().getType(serUri);
		assertNotNull(uri);
	}

	@SmallTest
	public void testInsertUriContentValues() {
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		ContentValues data = new ContentValues();
		data.put(CredentialTable.KEY_APPLICATION, "test1");
		data.put(CredentialTable.KEY_COMPANY,     "TI");
		data.put(CredentialTable.KEY_FIRSTNAME,   "AAAA");
		data.put(CredentialTable.KEY_LASTNAME,    "BBBBB");
		data.put(CredentialTable.KEY_PASSWORD,    "CCCCc");
		data.put(CredentialTable.KEY_PROXY_URL,   "");
		data.put(CredentialTable.KEY_SERVICE,     "XXXXX");
		data.put(CredentialTable.KEY_TOKEN,       "");
		data.put(CredentialTable.KEY_USEPROXY,    false);
		data.put(CredentialTable.KEY_USERNAME,    "zzzzzzz");
		Uri res = getContext().getContentResolver().insert(credentialUri, data);
		assertNotNull(res);
	}

	@SmallTest
	public void testQueryUriStringArrayStringStringArrayString() {
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		Cursor c = getContext().getContentResolver().query(credentialUri,null,null,null,null);
		assertNotNull(c);
	}

	@SmallTest
	public void testUpdateUriContentValuesStringStringArray() {
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		
		ContentValues data = new ContentValues();
		data.put(CredentialTable.KEY_APPLICATION, "test3");
		data.put(CredentialTable.KEY_COMPANY, "TI");
		data.put(CredentialTable.KEY_FIRSTNAME, "AAAA");
		data.put(CredentialTable.KEY_LASTNAME, "BBBBB");
		data.put(CredentialTable.KEY_PASSWORD, "CCCCc");
		data.put(CredentialTable.KEY_PROXY_URL, "");
		data.put(CredentialTable.KEY_SERVICE, "XXXXX");
		data.put(CredentialTable.KEY_TOKEN, "");
		data.put(CredentialTable.KEY_USEPROXY, false);
		data.put(CredentialTable.KEY_USERNAME, "zzzzzzz");
		int c = getContext().getContentResolver().update(credentialUri, data, CredentialTable.KEY_COMPANY +" = ?", new String[]{"TI"});
		assertNotNull(c);
	}

}
