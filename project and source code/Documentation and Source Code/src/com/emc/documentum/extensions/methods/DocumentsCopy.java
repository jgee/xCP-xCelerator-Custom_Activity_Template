package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;
import java.util.*;

public class DocumentsCopy extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String SOURCE_PATH, String TARGET_PATH, String DOCUMENT_ACTION, String SOURCE_TYPE, String TARGET_TYPE, String PACKAGE_NAME, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(SOURCE_PATH == null)
		{
			SOURCE_PATH = "";
		}
		if(TARGET_PATH == null)
		{
			TARGET_PATH = "";
		}
		if(DOCUMENT_ACTION == null)
		{
			DOCUMENT_ACTION = "";
		}
		if(SOURCE_TYPE == null)
		{
			SOURCE_TYPE = "";
		}
		if(TARGET_TYPE == null)
		{
			TARGET_TYPE = "";
		}
		if(PACKAGE_NAME == null)
		{
			PACKAGE_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("SOURCE_PATH", SOURCE_PATH);
		oParameters.putString("TARGET_PATH", TARGET_PATH);
		oParameters.putString("DOCUMENT_ACTION", DOCUMENT_ACTION);
		oParameters.putString("SOURCE_TYPE", SOURCE_TYPE);
		oParameters.putString("TARGET_TYPE", TARGET_TYPE);
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		
		return doTask(oWorkitem, oParameters, oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {
		
		setClass("DocumentsCopy");
		
		String sSourcePath = getWorkitemStringParameter(oParameters, "SOURCE_PATH");
		String sTargetPath = getWorkitemStringParameter(oParameters, "TARGET_PATH");
		String sDocumentAction = getWorkitemStringParameter(oParameters, "DOCUMENT_ACTION");
		String sSourceType = getWorkitemStringParameter(oParameters, "SOURCE_TYPE");
		String sTargetType = getWorkitemStringParameter(oParameters, "TARGET_TYPE");
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");
		
		if (isNull(sSourcePath) || isNull(sSourcePath) || isNull(sDocumentAction)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		if (isNull(sSourceType)) sSourceType = "dm_document";
		Vector<String> oDocuments = new Vector<String>();
		
		try {		
			IDfSession oSession = getSession();		
			IDfFolder oSourceFolder = (IDfFolder) oSession.getObjectByPath(sSourcePath);
			IDfFolder oTargetFolder = (IDfFolder) oSession.getObjectByPath(sTargetPath);
		    if (foundObject(oSourceFolder) && foundObject(oTargetFolder)) {
		    	IDfCollection oObjects = oSourceFolder.getContents("object_name");
		    	while (oObjects.next()) {
					String sObject = oObjects.getString("object_name");
					IDfSysObject oObject = (IDfSysObject) oSession.getObjectByPath(sSourcePath + "/" + sObject);
					if (oObject.getTypeName().equals(sSourceType)) {
						IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
						if (sDocumentAction.equals("MOVE")) {
							oRepositoryFileManager.moveOperation(getSession(), sObject, sSourcePath, sTargetPath);
			            	oDocuments.add(sTargetPath + "/" + sObject);
				        } else if (sDocumentAction.equals("COPYSOURCE")) {
				        	writeLog(LOG_DEBUG, "Copy document " + sObject + " in path " + sSourcePath + " to path " + sTargetPath);
				        	oRepositoryFileManager.copyDocument(oSession, sSourcePath, sObject, sTargetPath, sObject);
					        writeLog(LOG_INFO, "Document " + sObject + " in path " + sSourcePath + " copied to path " + sTargetPath);
					        oDocuments.add(sTargetPath + "/" + sObject);
				        } else if (sDocumentAction.equals("COPYTARGET")) {
				        	byte[] aSourceContent = oRepositoryFileManager.readBinaryDocument((IDfDocument) oObject);
				    		String sSourceContentType = oObject.getContentType();
				    		oRepositoryFileManager.createDocument(oSession, 
																			sTargetPath,
																			sObject,
																			sTargetType,
																			sSourceContentType,
																			aSourceContent);
				    		writeLog(LOG_INFO, "Document " + sObject + " in path " + sSourcePath + " copied to new object of type " + sTargetType + " in path " + sTargetPath);
				    		oDocuments.add(sTargetPath + "/" + sObject);
				        } else {
				        	writeLog(LOG_WARN, "Action not valid on document " + sObject + " in path " + sSourcePath);
				        }
			    	} else {
			    		writeLog(LOG_INFO, "Document " + sObject + " in path " + sSourcePath + " is not of type " + sSourceType + ", it is not moved/copied");
			    	}
		    	}
		    }		    			    

		    if (!isNull(sPackageName)) {
				IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
	            addDocumentsToPackage(getSession(), oWorkitem, oPackage, oDocuments);
		    }
		    
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally{   }
	    
		return 0;
	}
	
}