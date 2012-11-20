package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class DocumentCopy extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String SOURCE_DOCUMENT_PATH, String SOURCE_DOCUMENT_NAME, String TARGET_DOCUMENT_PATH, String TARGET_DOCUMENT_NAME, boolean MOVE, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(SOURCE_DOCUMENT_PATH == null)
		{
			SOURCE_DOCUMENT_PATH = "";
		}
		if(SOURCE_DOCUMENT_NAME == null)
		{
			SOURCE_DOCUMENT_NAME = "";
		}
		if(TARGET_DOCUMENT_PATH == null)
		{
			TARGET_DOCUMENT_PATH = "";
		}
		if(TARGET_DOCUMENT_NAME == null)
		{
			TARGET_DOCUMENT_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("SOURCE_DOCUMENT_PATH", SOURCE_DOCUMENT_PATH);
		oParameters.putString("SOURCE_DOCUMENT_NAME", SOURCE_DOCUMENT_NAME);
		oParameters.putString("TARGET_DOCUMENT_PATH", TARGET_DOCUMENT_PATH);
		oParameters.putString("TARGET_DOCUMENT_NAME", TARGET_DOCUMENT_NAME);
		oParameters.putBoolean("MOVE_OR_COPY", MOVE);
		
		return doTask(oWorkitem, oParameters, oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {
		
		setClass("DocumentCopy");
		
		String sSourceDocumentPath = getWorkitemStringParameter(oParameters, "SOURCE_DOCUMENT_PATH");
		String sSourceDocumentName = getWorkitemStringParameter(oParameters, "SOURCE_DOCUMENT_NAME");
		String sTargetDocumentPath = getWorkitemStringParameter(oParameters, "TARGET_DOCUMENT_PATH");
		String sTargetDocumentName = getWorkitemStringParameter(oParameters, "TARGET_DOCUMENT_NAME");
		boolean bMove = getWorkitemBooleanParameter(oParameters, "MOVE_OR_COPY");
		
		if (isNull(sSourceDocumentPath) || isNull(sSourceDocumentName) || 
			isNull(sTargetDocumentPath) || isNull(sTargetDocumentName)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			IDfSession oSession = getSession();				
			IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
	        if (bMove) {
	        	oRepositoryFileManager.moveOperation(getSession(), sSourceDocumentName, sSourceDocumentPath, sTargetDocumentPath);
	        } else {
	        	oRepositoryFileManager.copyDocument(oSession, sSourceDocumentPath, sSourceDocumentName, sTargetDocumentPath, sTargetDocumentName);
	        }

	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}
	
}