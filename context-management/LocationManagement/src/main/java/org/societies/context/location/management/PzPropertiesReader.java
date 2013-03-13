package org.societies.context.location.management;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PzPropertiesReader {
	private static final Logger log = LoggerFactory.getLogger(PzPropertiesReader.class);
	
	/* Config file name */
	private final static String CONFIG_FILE = "pz.properties";
	
	
	private final static String PZ_UPDATE_CYCLE_SEC = "PZ_UPDATE_CYCLE_SEC";
	private final static String PZ_PRD_SERVER_URL = "PZ_PRD_SERVER_URL";
	private final static String PZ_QUERY_GET_ENTITY_FULL = "PZ_QUERY_GET_ENTITY_FULL";
	private final static String PZ_ENTITY_ID = "PZ_ENTITY_ID";
	private final static String PZ_PRD_SERVER_ADMIN_URL = "PZ_PRD_SERVER_ADMIN_URL";
	private final static String PZ_MAP_QUERY_SUFFIX = "PZ_MAP_QUERY_SUFFIX";
	private final static String PZ_MOCK_GENERATE_LOCATIONS_CYCLE= "PZ_MOCK_GENERATE_LOCATIONS_CYCLE";
	private final static String PZ_UPDATE_TASK_MAX_FAILURES= "PZ_UPDATE_TASK_MAX_FAILURES";
		
	static PzPropertiesReader instance = new PzPropertiesReader();
	private final Properties prop;
	
	public static PzPropertiesReader instance(){
		return instance;
	}
	
	protected PzPropertiesReader(){
		prop = new Properties();
		 
		InputStream inputStream = null;
    	try {
    		inputStream =getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
    		//load a properties file
    		prop.load(inputStream);
    		
    		if (prop == null || prop.size() == 0){
    			log.error("Error ! PZ Properties weren't read from ("+CONFIG_FILE+")");
    		}
    		
    		String propertiesStr = "";
    		for (@SuppressWarnings("rawtypes") Entry entry :prop.entrySet()){
    			propertiesStr += entry.getKey() + " : " + entry.getValue()+ " ;\n ";
    		}
    		log.info("-- PZ Properties -- \n"+ propertiesStr);
    		
    	}catch (IOException ex) {
    		log.error("Exception msg: "+ex.getMessage()+"\t cause: "+ex.getCause(),ex);
    	}catch (Exception e) {
    		log.error("Exception msg: "+e.getMessage()+"\t cause: "+e.getCause(),e);
		}finally{
    		if (inputStream != null){
    			try {
					inputStream.close();
				} catch (IOException e) {}
    		}
    	}
	}
	
	
	public int getUpdateCycle(){
		String updateCycle = prop.getProperty(PZ_UPDATE_CYCLE_SEC);
		return Integer.valueOf(updateCycle);
	}
	
	public String getEntityId(){
		return prop.getProperty(PZ_ENTITY_ID);
	}
	
	public String getPzURL(){
		return prop.getProperty(PZ_PRD_SERVER_URL);
	}
	
	public String getEntityFullQuery(){
		return prop.getProperty(PZ_QUERY_GET_ENTITY_FULL);
	}
	
	public String getPzAdminURL(){
		return prop.getProperty(PZ_PRD_SERVER_ADMIN_URL);
	}
	
	public String getMapQuery(){
		return prop.getProperty(PZ_MAP_QUERY_SUFFIX);
	}
	
	public int getPzMockGenerateLocationsCycle(){
		String value =  prop.getProperty(PZ_MOCK_GENERATE_LOCATIONS_CYCLE);
		return Integer.valueOf(value);
	}
	
	public int getPzUpdateTaskMaxFailures(){
		String value =  prop.getProperty(PZ_UPDATE_TASK_MAX_FAILURES);
		return Integer.valueOf(value);
	}
	
}
