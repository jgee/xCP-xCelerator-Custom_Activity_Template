package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfProperties;
import com.emc.documentum.extensions.util.WorkflowEventManager;

public class PackageLifecycleSuspend extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String PKG_NAME, String LC_STATE, String LC_OVERRIDE,  PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			DfLogger.error(this, "Workitem Id is blank", null, null);
	  	    return -1;
		}
		if(PKG_NAME == null)
		{
			PKG_NAME = "";
		}
		if(LC_STATE == null)
		{
			LC_STATE = "";
		}
		if(LC_OVERRIDE == null)
		{
			LC_OVERRIDE = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(PACKAGE_NAME, PKG_NAME);
		oParameters.putString(LIFECYCLE_STATE, LC_STATE);
		oParameters.putString(LIFECYCLE_OVERRIDE, LC_OVERRIDE);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oActivityParameters, PrintWriter oPrintWriter) throws Exception {
		boolean bStop = false;
		String sPackageName = oActivityParameters.getString(PACKAGE_NAME);
		if (sPackageName != null && !sPackageName.equals("")) {
			System.out.println("Package name: " + sPackageName);
			DfLogger.trace(this, "PoC:: Package name: " + sPackageName, null, null);
		} else {
			System.out.println("Package name not specified");
		  	DfLogger.trace(this, "PoC:: Package name not specified", null, null);
	  	    bStop = true;
		}	    
		String sLifecycleState = oActivityParameters.getString(LIFECYCLE_STATE);
		if (sLifecycleState != null && !sLifecycleState.equals("")) {
			System.out.println("Lifecycle suspended state: " + sLifecycleState);
			DfLogger.trace(this, "PoC:: Lifecycle suspended state: " + sLifecycleState, null, null);
		} else {
			System.out.println("Lifecycle suspended state not specified");
		  	DfLogger.trace(this, "PoC:: Lifecycle suspended state not specified", null, null);
	  	    bStop = true;
		}	    
		boolean bLifecycleOverride = oActivityParameters.getBoolean(LIFECYCLE_OVERRIDE);
		if (bLifecycleOverride) {
			System.out.println("Lifecycle Override is true");
			DfLogger.trace(this, "PoC:: Lifecycle Override is true", null, null);
		} else {
			System.out.println("Lifecycle Override is false");
		  	DfLogger.trace(this, "PoC:: Lifecycle Override is false", null, null);
		}	    
		if (bStop) {
			System.out.println("Method cannot continue, not enough parameters");
	  	    DfLogger.trace(this, "PoC:: Method cannot continue, not enough parameters", null, null);
		} else {
			try {		
				// DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
				// oDocumentumConnection.beginTransaction();     
				IDfSession oSession = getSession();
				
		        System.out.println("Search " + sPackageName + " workflow package");
		        DfLogger.trace(this, "PoC:: Search " + sPackageName + " workflow package", null, null);
		    	IDfCollection oPackages = oWorkitem.getAllPackages(null);
		        try {
		        	IDfPackage oPackage = null;
		        	IDfDocument oDocument = null;
		            while (oPackages.next()) {
		            	String sPackage = oPackages.getString("r_package_name");
				        System.out.println("Check workflow package " + sPackage);
				        DfLogger.trace(this, "PoC:: Check workflow package " + sPackage, null, null);			            	
		                if (sPackage.equalsIgnoreCase(sPackageName)) {
		                	IDfId oPackageId = oPackages.getId("r_object_id");
			                oPackage = oWorkitem.getPackage(oPackageId);
			                System.out.println("Found package " + sPackage + " with Id " + oPackageId.toString());
			                DfLogger.trace(this, "PoC:: Found package " + sPackage + " with Id " + oPackageId.toString(), null, null);
		    	        	IDfId oDocumentId = oPackage.getComponentId(0);
		    	        	oDocument = (IDfDocument) oSession.getObject(oDocumentId);
					        System.out.println("Suspend document " + oDocument.getObjectName() + " to " + sLifecycleState);
					        DfLogger.trace(this, "PoC:: Suspend document " + oDocument.getObjectName() + " to " + sLifecycleState, null, null);
		    	        	oDocument.suspend(sLifecycleState, bLifecycleOverride, false);

					        // Start lifecycle workflow management
					        String sWorkflowId = oWorkitem.getWorkflowId().toString();
					        String sDocumentId = oDocument.getObjectId().toString();
					        System.out.println("Generate event to suspend the lifecycle workflow to " + sLifecycleState + " for workflow " + sWorkflowId + " and document " + sDocumentId);
					        DfLogger.trace(this, "PoC:: Generate event to suspend the lifecycle workflow to " + sLifecycleState + " for workflow " + sWorkflowId + " and document " + sDocumentId, null, null);
					        WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
					        oWorkflowEventManager.triggerLifecycleEvent(oSession, null, sWorkflowId, sDocumentId, sLifecycleState);
			                // End lifecycle workflow management		    	        	
		                }
		            }
	                if (oPackage == null || oDocument == null) {
		                System.out.println("Package " + sPackageName + " or contained document not found");
		                DfLogger.trace(this, "PoC:: Package " + sPackageName + " or contained document not found", null, null);			                	
	                }
		        } finally {
		            if (oPackages != null) oPackages.close();
		        }
		        			    
			    // DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
				// oDocumentumConnection.commitTransaction();
		    } catch (Exception oException) {
		    	// DON'T USE TRANSACTION TO APPLY A LIFECYCLE TO A DOCUMENT
		    	// oDocumentumConnection.abortTransaction();
		    	System.out.println("Error due to " + oException.getMessage());
		    	DfLogger.error(this, "PoC:: Error due to " + oException.getMessage(), null, oException);
		    } finally {				    	
		    }	    
		}
		return 0;
	}	
	
	private static final String PACKAGE_NAME = "PACKAGE_NAME";
	private static final String LIFECYCLE_STATE = "STATE";
	private static final String LIFECYCLE_OVERRIDE = "OVERRIDE";	
}