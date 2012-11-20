package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfProperties;

public class DocumentLifecycleApplication extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String PACKAGE_NAME, String DOCUMENT_PATH, String DOCUMENT_NAME, IDfId POLICY_ID, String STATE, String SCOPE, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(PACKAGE_NAME == null)
		{
			PACKAGE_NAME = "";
		}
		if(DOCUMENT_PATH == null)
		{
			DOCUMENT_PATH = "";
		}
		if(DOCUMENT_NAME == null)
		{
			DOCUMENT_NAME = "";
		}
		if(POLICY_ID == null)
		{
			POLICY_ID = new DfId("0000000000000000");
		}
		if(STATE == null)
		{
			STATE = "";
		}
		if(SCOPE == null)
		{
			SCOPE = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		oParameters.putString("DOCUMENT_PATH", DOCUMENT_PATH);
		oParameters.putString("DOCUMENT_NAME", DOCUMENT_NAME);
		oParameters.putId("POLICY_ID", POLICY_ID);
		oParameters.putString("STATE", STATE);
		oParameters.putString("SCOPE", SCOPE);
		
		return doTask(oWorkitem, oParameters, oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {
		
		setClass("xBSFolderProgressCalculation");
		
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");
		String sDocumentPath = getWorkitemStringParameter(oParameters, "DOCUMENT_PATH");
		String sDocumentName = getWorkitemStringParameter(oParameters, "DOCUMENT_NAME");
		IDfId oLifecycleId = getWorkitemIdParameter(oParameters, "POLICY_ID");
		String sLifecycleState = getWorkitemStringParameter(oParameters, "STATE");
		String sLifecycleScope = getWorkitemStringParameter(oParameters, "SCOPE");

		if (((isNull(sPackageName) && isNull(sDocumentPath) && isNull(sDocumentName)) ||
			(isNull(sPackageName) && isNull(sDocumentPath)) ||
			(isNull(sPackageName) && isNull(sDocumentName))) ||
			isNull(oLifecycleId)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			// DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
			// oDocumentumConnection.beginTransaction();     
			
			IDfSession oSession = getSession();
			
			IDfSysObject oDocument;
			if (isNull(sPackageName)) {
				writeLog(LOG_DEBUG, "Get document " + sDocumentName + " by its path");
				oDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath + "/" + sDocumentName);
			} else {
				writeLog(LOG_DEBUG, "Get document " + sDocumentName + " by its package");
				IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
	        	oDocument = getPackageDocument(oSession, oPackage, 0, null);
	        	sDocumentName = oDocument.getObjectName();
			}
		    if (oDocument == null) {
		    	writeLog(LOG_WARN, "Package or document cannot be found");
		    } else {
		        oDocument.attachPolicy(oLifecycleId, sLifecycleState, sLifecycleScope);
		        writeLog(LOG_DEBUG, "Lifecycle " + oLifecycleId.toString() + " (" + sLifecycleState + ") applied to document " + sDocumentPath);
		    }
		    
		    // DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
			// oDocumentumConnection.commitTransaction();
	    } catch (Exception oException) {
	    	// DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
	    	// oDocumentumConnection.abortTransaction();
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}
	
}