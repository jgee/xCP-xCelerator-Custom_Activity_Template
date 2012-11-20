package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfProperties;

public class WorkflowCleaning extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, boolean CLEAN_PACKAGES, boolean REMOVE_ATTACHMENTS, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putBoolean("CLEAN_PACKAGES", CLEAN_PACKAGES);
		oParameters.putBoolean("REMOVE_ATTACHMENTS", REMOVE_ATTACHMENTS);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("WorkflowCleaning");
		
		boolean bCleanPackages = getWorkitemBooleanParameter(oParameters, "CLEAN_PACKAGES");
		boolean bRemoveAttachments = getWorkitemBooleanParameter(oParameters, "REMOVE_ATTACHMENTS");
		
		try {		
			if (bCleanPackages) {
		    	writeLog(LOG_DEBUG, "Scanning workflow packages");
		    	IDfCollection oPackages = oWorkitem.getAllPackages(null);
		    	int iPackage = 0;
		        try {				        	
		            while (oPackages.next()) {
		            	String sWorkitemPackage = oPackages.getString("r_package_name");
		            	writeLog(LOG_DEBUG, "\tFound workitem package " + sWorkitemPackage);
		            	IDfId oPackageId = oPackages.getId("r_object_id");
		                IDfPackage oPackage = oWorkitem.getPackage(oPackageId);
		                int iObjects = oPackage.getComponentIdCount();
		                writeLog(LOG_DEBUG, "\tWorkitem package " + sWorkitemPackage + " contains " + iObjects + "objects");
		                // for (int iObject = 0; iObject < iObjects; iObject ++) {
		                // 	IDfId oObjectId = oPackage.getComponentId(iObject);
		                // 	String sObjectName = oPackage.getComponentName(iObject);
		                // 	writeLog(LOG_DEBUG, "\t\tClean object " + sObjectName + " with Id " + oObjectId + " of workitem package " + sWorkitemPackage);
		                // 	oObjectId = null;
		                // }
		                writeLog(LOG_DEBUG, "\tClean workitem package " + sWorkitemPackage);
		            	oWorkitem.removePackage(sWorkitemPackage);
		            	writeLog(LOG_DEBUG, "\tWorkitem package " + sWorkitemPackage + " cleaned");
		                iPackage ++;
		            }
		        } finally {
		            if (oPackages != null) oPackages.close();
		        }
	            if (iPackage > 0) {
	            	writeLog(LOG_INFO, iPackage + " workflow package(s) cleaned");
	            } else {
	            	writeLog(LOG_DEBUG, "No workflow package(s) cleaned");
	            }
	    	}
            if (bRemoveAttachments) {
                writeLog(LOG_DEBUG, "Scanning workflow attachments");
                IDfCollection oAttachments = oWorkitem.getAttachments();
                int iAttachments = 0;
        		try {				        
                    while (oAttachments.next()) {
                    	IDfId oAttachmentId = oAttachments.getId("r_object_id");
                    	String sAttachment = oAttachments.getString("r_component_name");		    	        	
                    	writeLog(LOG_DEBUG, "\tFound workitem attachment " + sAttachment + " with Id " + oAttachmentId.toString());
                    	writeLog(LOG_DEBUG, "\tRemove workitem attachment " + sAttachment + " with Id " + oAttachmentId.toString());
			            oWorkitem.removeAttachment(oAttachmentId);
			            writeLog(LOG_INFO, "\tAttachment " + sAttachment + " with Id " + oAttachmentId.toString() + " removed from workitem");
		                iAttachments ++;
    	        	}
                } finally {
                    if (oAttachments != null) oAttachments.close();
                }
                if (iAttachments > 0) {
                	writeLog(LOG_DEBUG, iAttachments + " workflow attachment(s) removed");
                } else {
                	writeLog(LOG_WARN, "No workflow attachment(s) removed");
                }
            }	                
			
			
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
}