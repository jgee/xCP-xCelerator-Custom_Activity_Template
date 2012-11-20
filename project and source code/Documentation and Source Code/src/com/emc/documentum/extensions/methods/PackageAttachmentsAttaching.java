package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfProperties;

public class PackageAttachmentsAttaching extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String PACKAGE_NAME, boolean ADD_ATTACHMENTS, boolean REMOVE_ATTACHMENTS, String OTHER_DOCUMENTS_PATH, PrintWriter oPrintWriter) throws Exception
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
		if(OTHER_DOCUMENTS_PATH == null)
		{
			OTHER_DOCUMENTS_PATH = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		oParameters.putBoolean("ADD_ATTACHMENTS", ADD_ATTACHMENTS);
		oParameters.putBoolean("REMOVE_ATTACHMENTS", REMOVE_ATTACHMENTS);
		oParameters.putString("OTHER_DOCUMENTS_PATH", OTHER_DOCUMENTS_PATH);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("PackageAttachmentsAttaching");
		
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");
		boolean bAddAttachments = getWorkitemBooleanParameter(oParameters, "ADD_ATTACHMENTS");
		boolean bRemoveAttachments = getWorkitemBooleanParameter(oParameters, "REMOVE_ATTACHMENTS");
		String sOtherDocumentsPath = getWorkitemStringParameter(oParameters, "OTHER_DOCUMENTS_PATH");
		
		if (isNull(sPackageName)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			IDfSession oSession = getSession();

			IDfClientX oClientx = new DfClientX();
        	IDfList oDocumentIds = oClientx.getList();
        	
            oDocumentIds.setElementType(IDfList.DF_ID);

            int iAttachments = 0;
            if (bAddAttachments) {
                writeLog(LOG_DEBUG, "Scanning workflow attachments");
                IDfCollection oAttachments = oWorkitem.getAttachments();
        		try {				        
                    while (oAttachments.next()) {
                    	IDfId oAttachmentId = oAttachments.getId("r_object_id");
                    	String sAttachment = oAttachments.getString("r_component_name");		    	        	
                    	writeLog(LOG_DEBUG, "\tFound attachment " + sAttachment + " with Id " + oAttachmentId.toString() + " into workitem");
	    	        	IDfId oObjectId = oAttachments.getId("r_component_id");
	    	        	IDfSysObject oObject = (IDfSysObject) oSession.getObject(oObjectId);
	    	        	String sObject = oObject.getObjectName();
	    	        	writeLog(LOG_DEBUG, "\tFound document " + sObject + " into attachment " + sAttachment);
		                oDocumentIds.appendId(oObjectId);
		                writeLog(LOG_INFO, "\tDocument " + sObject + " with Id " + oObjectId.toString() + " and type " + oObject.getTypeName() + " added to package " + sPackageName);
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
                	writeLog(LOG_DEBUG, iAttachments + " documents added to package " + sPackageName);
                } else {
                	writeLog(LOG_WARN, "No documents added to package " + sPackageName);
                }
            }	                
		
            int iOtherDocument = 0;
			if (!isNull(sOtherDocumentsPath)) {
		    	IDfFolder oFolder = getSession().getFolderByPath(sOtherDocumentsPath);
		    	writeLog(LOG_DEBUG, "Scanning folder " + sOtherDocumentsPath + " for other documents");
		    	IDfCollection oOtherDocuments = oFolder.getContents("object_name");
		        try {
		            while (oOtherDocuments.next()) {
		            	String sOtherDocument = oOtherDocuments.getString("object_name");
	                	writeLog(LOG_DEBUG, "\tFound document " + sOtherDocument);
		                IDfSysObject oOtherDocument = (IDfSysObject) oSession.getObjectByPath(sOtherDocumentsPath + "/" + sOtherDocument);
		                IDfId oDocumentId = oOtherDocument.getObjectId();
		                oDocumentIds.appendId(oDocumentId);
		                writeLog(LOG_INFO, "\tDocument " + sOtherDocument + " with Id " + oDocumentId.toString() + " and type " + oOtherDocument.getTypeName() + " added to package " + sPackageName);
		                iOtherDocument ++;
		            }
	                if (iOtherDocument > 0) {
	                	writeLog(LOG_DEBUG, iOtherDocument + " documents added to package " + sPackageName);
	                } else {
	                	writeLog(LOG_WARN, "No documents added as to package " + sPackageName);
	                }
		        } finally {
		            if (oOtherDocuments != null) oOtherDocuments.close();
		        }
		        writeLog(LOG_DEBUG, "Folder " + sOtherDocumentsPath + " scanned, package " + sPackageName + " filled");
		    }

			IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
			oWorkitem.addPackage(sPackageName, oPackage.getPackageType(), oDocumentIds);
			writeLog(LOG_INFO, "Package " + sPackageName + " saved with new " + (iAttachments + iOtherDocument) + " attached document(s)");
			    
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
}