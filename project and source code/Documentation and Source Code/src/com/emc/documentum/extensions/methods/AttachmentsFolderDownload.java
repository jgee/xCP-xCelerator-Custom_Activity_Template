package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class AttachmentsFolderDownload extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String FOLDER_PATH, String FOLDER_NAME, boolean CREATE_FOLDER, boolean MOVE, boolean REMOVE_FRM_WF, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(FOLDER_PATH == null)
		{
			FOLDER_PATH = "";
		}
		if(FOLDER_NAME == null)
		{
			FOLDER_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("DOWNLOAD_PATH", FOLDER_PATH);
		oParameters.putString("DOWNLOAD_FOLDER", FOLDER_NAME);
		oParameters.putBoolean("CREATE_FOLDER", CREATE_FOLDER);
		oParameters.putBoolean("MOVE_OR_COPY", MOVE);
		oParameters.putBoolean("REMOVE", REMOVE_FRM_WF);
		
		return doTask(oWorkitem, oParameters, oPrintWriter);
	}
	
	private int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception 
	{

		setClass("AttachmentsFolderDownload");
		
		String sDownloadPath = getWorkitemStringParameter(oParameters, "DOWNLOAD_PATH");
		String sDownloadFolder = getWorkitemStringParameter(oParameters, "DOWNLOAD_FOLDER");
		boolean bCreateFolder = getWorkitemBooleanParameter(oParameters, "CREATE_FOLDER");
		boolean bMove = getWorkitemBooleanParameter(oParameters, "MOVE_OR_COPY");
		boolean bRemove = getWorkitemBooleanParameter(oParameters, "REMOVE");

		if (isNull(sDownloadPath) || isNull(sDownloadFolder)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {
			IDfSession oSession = getSession();	
			if (foundObject(oSession, sDownloadPath)) {
				String sTargetDocumentPath = sDownloadPath + "/" + sDownloadFolder;
			    if (foundObject(oSession, sTargetDocumentPath)) 
			    {
			    	writeLog(LOG_DEBUG, "Download folder " + sDownloadFolder + " found");
			    } 
			    else 
			    {
			    	writeLog(LOG_WARN, "Download folder " + sDownloadFolder + " does not exist");
			        if (bCreateFolder) {
			        	IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
			        	oRepositoryFileManager.createFolder(oSession, sDownloadPath, sDownloadFolder, "dm_folder");
				    }
			    }
			    IDfCollection oAttachments = oWorkitem.getAttachments();
			    int iAttachments = 0;
			    int iDownloaded = 0;
		        try {        	
		            while (oAttachments.next()) 
		            {
		            	IDfDocument oDocument = (IDfDocument) oSession.getObject(new DfId(oAttachments.getString("r_component_id")));
		            	String[] aPaths = getLinkedPaths(oSession, oDocument);
		            	String sDocumentName = oAttachments.getString("r_component_name");
	    	        	if (!isNull(aPaths)) {
			            	try {
			            		String sSourceDocumentPath = aPaths[0];
			            		IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
						        if (bMove) {
						        	oRepositoryFileManager.moveDocument(oSession, sSourceDocumentPath, sDocumentName, sTargetDocumentPath, sDocumentName);
						        } else {
						        	oRepositoryFileManager.copyDocument(oSession, sSourceDocumentPath, sDocumentName, sTargetDocumentPath, sDocumentName);
						        }
			        	    } catch (Exception oException) {
			        	    	writeLog(LOG_ERROR, "Cannot " + ((bMove) ? "move" : "copy") + " attachment " + sDocumentName + ", Error due to " + oException.getMessage(), oException);
			        	    }
	    	        	}
	    	        	if (bRemove) {
	    	        		writeLog(LOG_DEBUG, "Remove attachment " + sDocumentName + " from workitem");
							IDfId oAttachmentId = oAttachments.getId("r_object_id");
							oWorkitem.removeAttachment(oAttachmentId);
							writeLog(LOG_INFO, "Attachment " + sDocumentName + " removed from workitem");
						}
		            	iAttachments ++;
		            }
			        if (iDownloaded > 0) {
			        	writeLog(LOG_INFO, iAttachments + " attachments, " + iDownloaded + " downloaded to path " + sDownloadPath + "/" + sDownloadFolder);
	                } else {
	                	writeLog(LOG_WARN, "No attachments downloaded to path " + sDownloadPath + "/" + sDownloadFolder);
	                }
		        } finally {
		            if (oAttachments != null) oAttachments.close();
		        }
		    }
		    
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }    
		return 0;
	}	
	
}