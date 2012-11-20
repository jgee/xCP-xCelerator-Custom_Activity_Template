package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class DocumentCreation extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String DOCUMENT_PATH, String DOCUMENT_NAME, String DOCUMENT_TYPE, String CONTENT_TYPE, String CONTENT_TEXT, PrintWriter oPrintWriter) throws Exception
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
		if(DOCUMENT_TYPE == null)
		{
			DOCUMENT_TYPE = "";
		}
		if(CONTENT_TYPE == null)
		{
			CONTENT_TYPE = "";
		}
		if(CONTENT_TEXT == null)
		{
			CONTENT_TEXT = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("DOCUMENT_PATH", DOCUMENT_PATH);
		oParameters.putString("DOCUMENT_NAME", DOCUMENT_NAME);
		oParameters.putString("DOCUMENT_TYPE", DOCUMENT_TYPE);
		oParameters.putString("CONTENT_TYPE", CONTENT_TYPE);
		oParameters.putString("CONTENT_TEXT", CONTENT_TEXT);
		
		return doTask(oWorkitem, oParameters, oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("DocumentCreation");
		
		String sDocumentPath = getWorkitemStringParameter(oParameters, "DOCUMENT_PATH");
		String sDocumentName = getWorkitemStringParameter(oParameters, "DOCUMENT_NAME");
		String sDocumentType = getWorkitemStringParameter(oParameters, "DOCUMENT_TYPE");
		String sContentType = getWorkitemStringParameter(oParameters, "CONTENT_TYPE");
		String sContentText = getWorkitemStringParameter(oParameters, "CONTENT_TEXT");
		
		if (isNull(sDocumentPath) || isNull(sDocumentName) ||
			isNull(sDocumentType) || isNull(sContentType)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		} 
		
		try {		
			IDfSession oSession = getSession();				
		    if (foundObject(oSession, sDocumentPath)) {
		    	IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
		        oRepositoryFileManager.createDocument(oSession, sDocumentPath, sDocumentName, sDocumentType, sContentType, sContentText.getBytes());
		    }	    			    
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
}