package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfProperties;

public class VirtualDocumentAdding extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String PACKAGE_NAME, String DOCUMENT_PATH, String DOCUMENT_NAME, IDfList PACKAGE_NAMES, boolean ADD_ATTACHMENTS, boolean REMOVE_ATTACHMENTS, String OTHER_DOCUMENTS_PATH, PrintWriter oPrintWriter) throws Exception
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
		if(PACKAGE_NAMES == null)
		{
			PACKAGE_NAMES = new DfList();
		}
		if(OTHER_DOCUMENTS_PATH == null)
		{
			OTHER_DOCUMENTS_PATH = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		oParameters.putString("DOCUMENT_PATH", DOCUMENT_PATH);
		oParameters.putString("DOCUMENT_NAME", DOCUMENT_NAME);
		oParameters.putList("PACKAGE_NAMES", PACKAGE_NAMES);
		oParameters.putBoolean("ADD_ATTACHMENTS", ADD_ATTACHMENTS);
		oParameters.putBoolean("REMOVE_ATTACHMENTS", REMOVE_ATTACHMENTS);
		oParameters.putString("OTHER_DOCUMENTS_PATH", OTHER_DOCUMENTS_PATH);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("VirtualDocumentAdding");
		
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");
		String sDocumentPath = getWorkitemStringParameter(oParameters, "DOCUMENT_PATH");
		String sDocumentName = getWorkitemStringParameter(oParameters, "DOCUMENT_NAME");
		IDfList oPackageNames = getWorkitemListParameter(oParameters, "PACKAGE_NAMES");
		boolean bAddAttachments = getWorkitemBooleanParameter(oParameters, "ADD_ATTACHMENTS");
		boolean bRemoveAttachments = getWorkitemBooleanParameter(oParameters, "REMOVE_ATTACHMENTS");
		String sOtherDocumentsPath = getWorkitemStringParameter(oParameters, "OTHER_DOCUMENTS_PATH");
		
		if ((isNull(sPackageName) && isNull(sDocumentPath) && isNull(sDocumentName)) ||
			(isNull(sPackageName) && isNull(sDocumentPath)) ||
			(isNull(sPackageName) && isNull(sDocumentName))) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		} 
		
		try {		
			IDfSession oSession = getSession();
			
			IDfSysObject oDocument;
			if (isNull(sPackageName)) {
				writeLog(LOG_DEBUG, "Get document " + sDocumentName + " by its path");
				oDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath + "/" + sDocumentName);
			} else {
				writeLog(LOG_DEBUG, "Get document by its package " + sPackageName);
				IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
	        	oDocument = getPackageDocument(oSession, oPackage, 0, null);
	        	sDocumentName = oDocument.getObjectName();
			}
			if (isNull(oDocument)) {
		    	writeLog(LOG_WARN, "Package or document cannot be found");
		    } else {
		    	if (oDocument.isVirtualDocument()) {
		    		writeLog(LOG_DEBUG, sDocumentName + " is already a Virtual Document");
			    } else {
			    	oDocument.checkout();
			    	oDocument.setIsVirtualDocument(true);
			    	oDocument.save();
			    	writeLog(LOG_INFO, "Virtual Document " + sDocumentName + " created");
			    }
		    	IDfVirtualDocument oVirtualDocument = oDocument.asVirtualDocument("CURRENT", false );
                IDfVirtualDocumentNode oRoot = oVirtualDocument.getRootNode();
                
                if (!isNull(oPackageNames)) {
			    	writeLog(LOG_DEBUG, "Scanning packages");
	                int iObjects = 0;
	                int iPackage = 0;
	                while (iPackage < oPackageNames.getCount()) {
	                	String sListPackage = (String) oPackageNames.get(iPackage);	                	
	                	writeLog(LOG_DEBUG, "\tSearch " + sListPackage  + " workflow package");
				    	IDfCollection oPackages = oWorkitem.getAllPackages(null);
				    	IDfId oObjectId = null;
				        try {				        	
				            while (oPackages.next()) {
				            	String sWorkitemPackage = oPackages.getString("r_package_name");
				            	writeLog(LOG_DEBUG, "\t\tCheck workitem package " + sWorkitemPackage);
				                if (sWorkitemPackage.equalsIgnoreCase(sListPackage)) {
				                	IDfId oPackageId = oPackages.getId("r_object_id");
					                IDfPackage oPackage = oWorkitem.getPackage(oPackageId);
					                writeLog(LOG_INFO, "\t\tFound package " + sWorkitemPackage + " with Id " + oPackageId.toString() + " into workitem");
					                oObjectId = oPackage.getComponentId(0);
				    	        }
				    	    }
				        } finally {
				            if (oPackages != null) oPackages.close();
				        }
	    	        	if (oObjectId != null) {
		    	        	IDfSysObject oObject = (IDfSysObject) oSession.getObject(oObjectId);
		    	        	String sObject = oObject.getObjectName(); 
		    	        	writeLog(LOG_DEBUG, "Found document " + sObject + " inside package " + sPackageName);
			                oDocument.checkout();
			                oVirtualDocument.addNode(oRoot, null, oObject.getChronicleId(), "CURRENT", false, false);
			                oDocument.save();
			                writeLog(LOG_INFO, "Document " + sObject + " added as child of " + sDocumentName);
			                iObjects ++;
	    	        	}
	    	        	iPackage ++;
	                }
	                if (iObjects > 0) {
	                	writeLog(LOG_INFO, iObjects + " documents added as child of " + sDocumentName);
	                } else {
	                	writeLog(LOG_DEBUG, "No documents added as child of " + sDocumentName);
	                }
                }
                
                if (bAddAttachments) {
	                writeLog(LOG_DEBUG, "Scanning workflow attachments");
	                IDfCollection oAttachments = oWorkitem.getAttachments();
	                int iAttachments = 0;
	        		try {				        
	                    while (oAttachments.next()) {
	                    	IDfId oAttachmentId = oAttachments.getId("r_object_id");
	                    	String sAttachment = oAttachments.getString("r_component_name");		    	        	
	                    	writeLog(LOG_DEBUG, "\tFound attachment " + sAttachment + " with Id " + oAttachmentId.toString() + " into workitem");
		    	        	IDfId oObjectId = oAttachments.getId("r_component_id");
		    	        	IDfSysObject oObject = (IDfSysObject) oSession.getObject(oObjectId);
		    	        	String sObject = oObject.getObjectName();
		    	        	writeLog(LOG_DEBUG, "\tFound document " + sObject + " with Id " + oObjectId.toString() + " into attachment " + sAttachment);
			                oDocument.checkout();
			                oVirtualDocument.addNode(oRoot, null, oObject.getChronicleId(), "CURRENT", false, false);
			                oDocument.save();
			                writeLog(LOG_INFO, "\tDocument " + sObject + " added as child of " + sDocumentName);
			                if (bRemoveAttachments) {
				                oWorkitem.removeAttachment(oAttachmentId);
				                writeLog(LOG_INFO, "\tAttachment " + sObject + " with Id " + oAttachmentId.toString() + " removed from workitem and workflow");
			                } else {
			                	writeLog(LOG_DEBUG, "\tAttachment " + sObject + " with Id " + oAttachmentId.toString() + " not removed from workitem and workflow");
			                }
			                iAttachments ++;
	    	        	}
	                } finally {
	                    if (oAttachments != null) oAttachments.close();
	                }
	                if (iAttachments > 0) {
	                	writeLog(LOG_DEBUG, iAttachments + " documents added as child of " + sDocumentName);
	                } else {
	                	writeLog(LOG_WARN, "No documents added as child of " + sDocumentName);
	                }
	                writeLog(LOG_DEBUG, "Attachments scanned, virtual document " + sDocumentName + " composed");
	            }	                
		    }
			
			if (!isNull(sOtherDocumentsPath)) {
		    	IDfFolder oFolder = getSession().getFolderByPath(sOtherDocumentsPath);
		    	writeLog(LOG_DEBUG, "Scanning folder " + sOtherDocumentsPath + " for other documents");
		    	IDfVirtualDocument oVirtualDocument = oDocument.asVirtualDocument("CURRENT", false );
	            IDfVirtualDocumentNode oRoot = oVirtualDocument.getRootNode();
		    	IDfCollection oOtherDocuments = oFolder.getContents("object_name");
		        try {
		        	int iOtherDocument = 0;
		            while (oOtherDocuments.next()) {
		            	String sOtherDocument = oOtherDocuments.getString("object_name");
		                if (!sOtherDocument.equalsIgnoreCase(sDocumentName)) {
		                	writeLog(LOG_DEBUG, "\tFound document " + sOtherDocument);
			                IDfSysObject oOtherDocument = (IDfSysObject) oSession.getObjectByPath(sOtherDocumentsPath + "/" + sOtherDocument);
			                oDocument.checkout();
			                oVirtualDocument.addNode(oRoot, null, oOtherDocument.getChronicleId(), "CURRENT", false, false);
			                oDocument.save();
			                writeLog(LOG_INFO, "\tDocument " + sOtherDocument + " added as child of " + sDocumentName);
			                iOtherDocument ++;
		                }
		            }
	                if (iOtherDocument > 0) {
	                	writeLog(LOG_DEBUG, iOtherDocument + " documents added as child of " + sDocumentName);
	                } else {
	                	writeLog(LOG_WARN, "No documents added as child of " + sDocumentName);
	                }
		        } finally {
		            if (oOtherDocuments != null) oOtherDocuments.close();
		        }
		        writeLog(LOG_DEBUG, "Folder " + sOtherDocumentsPath + " scanned, virtual document " + sDocumentName + " composed");
		    }
			
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
}