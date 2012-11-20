package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfProperties;
import com.emc.documentum.extensions.util.WorkflowEventManager;

public class DocumentLifecycleMotion extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String DOC_PATH, boolean LC_MOTION, String LC_STATE, boolean LC_OVERRIDE, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
            DfLogger.error(this, "Workitem Id is blank", null, null);
	  	    return -1;
		}
		if(DOC_PATH == null)
		{
			DOC_PATH = "";
		}
		if(LC_STATE == null)
		{
			LC_STATE = "";
		}
		
		IDfProperties oActivityParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oActivityParameters.putString(DOCUMENT_PATH, DOC_PATH);
		oActivityParameters.putBoolean(LIFECYCLE_MOTION, LC_MOTION);
		oActivityParameters.putString(LIFECYCLE_STATE, LC_STATE);
		oActivityParameters.putBoolean(LIFECYCLE_OVERRIDE, LC_OVERRIDE);
		
		return doTask(oWorkitem, oActivityParameters, oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oActivityParameters, PrintWriter oPrintWriter) throws Exception {
		boolean bStop = false;
		String sDocumentPath = oActivityParameters.getString(DOCUMENT_PATH);
		if (sDocumentPath != null && !sDocumentPath.equals("")) {
			System.out.println("Document path: " + sDocumentPath);
			DfLogger.trace(this, "PoC:: Document path: " + sDocumentPath, null, null);
		} else {
			System.out.println("Document path not specified");
		  	DfLogger.trace(this, "PoC:: Document path not specified", null, null);
	  	    bStop = true;
		}
		boolean bLifecycleMotion = oActivityParameters.getBoolean(LIFECYCLE_MOTION);
		if (bLifecycleMotion) {
			System.out.println("Lifecycle Motion is true");
			DfLogger.trace(this, "PoC:: Lifecycle Motion is true", null, null);
		} else {
			System.out.println("Lifecycle Motion is false");
		  	DfLogger.trace(this, "PoC:: Lifecycle Motion is false", null, null);
		}	    
		String sLifecycleState = oActivityParameters.getString(LIFECYCLE_STATE);
		if (sLifecycleState != null && !sLifecycleState.equals("")) {
			System.out.println("Lifecycle " + ((bLifecycleMotion) ? "next" : "previous") + " state: " + sLifecycleState);
			DfLogger.trace(this, "PoC:: Lifecycle " + ((bLifecycleMotion) ? "next" : "previous") + " state: " + sLifecycleState, null, null);
		} else {
			System.out.println("Lifecycle " + ((bLifecycleMotion) ? "next" : "previous") + " state not specified");
		  	DfLogger.trace(this, "PoC:: Lifecycle " + ((bLifecycleMotion) ? "next" : "previous") + " state not specified", null, null);
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
				
				IDfSysObject oDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath);
			    if (oDocument == null) {
			        System.out.println("Document " + sDocumentPath + " can not be found");
			        DfLogger.trace(this, "PoC:: Document " + sDocumentPath + " can not be found", null, null);
			    } else {
			    	if (oDocument.isVirtualDocument()) {
				        System.out.println(sDocumentPath + " is a Virtual Document");
				        DfLogger.trace(this, sDocumentPath + " is a Virtual Document", null, null);			    	
				    }
			    	if (bLifecycleMotion) {
			    		oDocument.promote(sLifecycleState, bLifecycleOverride, false);
			    	} else {
			    		oDocument.demote(sLifecycleState, false);
			    	}
			        System.out.println("Document " + sDocumentPath + ((bLifecycleMotion) ? " promoted to " : " demoted to ") + sLifecycleState);
			        DfLogger.trace(this, "PoC:: Document " + sDocumentPath + ((bLifecycleMotion) ? " promoted to " : " demoted to ") + sLifecycleState, null, null);
			        
			        // Start lifecycle workflow management
			        String sWorkflowId = oWorkitem.getWorkflowId().toString();
			        String sDocumentId = oDocument.getObjectId().toString();
			        System.out.println("Generate event to" + ((bLifecycleMotion) ? " promote " : " demote ") + "the lifecycle workflow to " + sLifecycleState + " for workflow " + sWorkflowId + " and document " + sDocumentId);
			        DfLogger.trace(this, "PoC:: Generate event to" + ((bLifecycleMotion) ? " promote " : " demote ") + "the lifecycle workflow to " + sLifecycleState + " for workflow " + sWorkflowId + " and document " + sDocumentId, null, null);
			        WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
			        oWorkflowEventManager.triggerLifecycleEvent(oSession, null, sWorkflowId, sDocumentId, sLifecycleState);
	                // End lifecycle workflow management    
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
	
	private static final String DOCUMENT_PATH = "DOCUMENT_PATH";
	private static final String LIFECYCLE_MOTION = "MOTION";
	private static final String LIFECYCLE_STATE = "STATE";
	private static final String LIFECYCLE_OVERRIDE = "OVERRIDE";	
}