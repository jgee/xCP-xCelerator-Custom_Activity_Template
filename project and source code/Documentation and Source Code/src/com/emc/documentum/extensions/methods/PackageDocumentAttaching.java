package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfProperties;

public class PackageDocumentAttaching extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String DOCUMENT_PATH, String DOCUMENT_NAME, String PACKAGE_NAME, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(DOCUMENT_PATH == null)
		{
			DOCUMENT_PATH = "";
		}
		if(DOCUMENT_NAME == null)
		{
			DOCUMENT_NAME = "";
		}
		if(PACKAGE_NAME == null)
		{
			PACKAGE_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("DOCUMENT_PATH", DOCUMENT_PATH);
		oParameters.putString("DOCUMENT_NAME", DOCUMENT_NAME);
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("PackageDocumentAttaching");
		
		String sDocumentPath = getWorkitemStringParameter(oParameters, "DOCUMENT_PATH");
		String sDocumentName = getWorkitemStringParameter(oParameters, "DOCUMENT_NAME");
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");

		if (isNull(sDocumentPath) || isNull(sDocumentName) || isNull(sPackageName)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			IDfSession oSession = getSession();
			IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
			String sDocument = sDocumentPath + "/" + sDocumentName;
		    if (foundPackage(oPackage) && foundObject(oSession, sDocument)) {
		    	addDocumentToPackage(getSession(), oWorkitem, oPackage, sDocument);
		    }
		    
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
}