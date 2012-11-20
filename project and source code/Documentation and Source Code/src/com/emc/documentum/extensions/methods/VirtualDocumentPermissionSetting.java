package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfProperties;

public class VirtualDocumentPermissionSetting extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String DOC_PATH, String DOC_NAME, String DOCS_START, IDfId PERM_SET, PrintWriter oPrintWriter) throws Exception
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
		if(DOC_NAME == null)
		{
			DOC_NAME = "";
		}
		if(DOCS_START == null)
		{
			DOCS_START = "";
		}
		if(PERM_SET == null)
		{
			PERM_SET = new DfId("0000000000000000");
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(DOCUMENT_PATH, DOC_PATH);
		oParameters.putString(DOCUMENT_NAME, DOC_NAME);
		oParameters.putString(DOCUMENTS_START, DOCS_START);
		oParameters.putId(PERMISSION_SET, PERM_SET);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
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
		String sDocumentName = oActivityParameters.getString(DOCUMENT_NAME);
		if (sDocumentName != null && !sDocumentName.equals("")) {
			System.out.println("Document path: " + sDocumentName);
			DfLogger.trace(this, "PoC:: Document name: " + sDocumentName, null, null);
		} else {
			System.out.println("Document name not specified");
		  	DfLogger.trace(this, "PoC:: Document name not specified", null, null);
	  	    bStop = true;
		}
		String sDocumentsStart = oActivityParameters.getString(DOCUMENTS_START);
		if (sDocumentsStart != null && !sDocumentsStart.equals("")) {
			System.out.println("Children documents prefix: " + sDocumentsStart);
			DfLogger.trace(this, "PoC:: Children documents prefix: " + sDocumentsStart, null, null);
		} else {
			System.out.println("Children documents prefix not specified");
		  	DfLogger.trace(this, "PoC:: Children documents prefix not specified", null, null);
		}	    
		IDfId oPermissionId = oActivityParameters.getId(PERMISSION_SET);
		if (oPermissionId != null) {
			System.out.println("Permission set Id: " + oPermissionId.toString());
			DfLogger.trace(this, "PoC:: Permission set Id: " + oPermissionId.toString(), null, null);
		} else {
			System.out.println("Permission set Id not specified");
		  	DfLogger.trace(this, "PoC:: Permission set Id not specified", null, null);
	  	    bStop = true;
		}	    
		if (bStop) {
			System.out.println("Method cannot continue, not enough parameters");
	  	    DfLogger.trace(this, "PoC:: Method cannot continue, not enough parameters", null, null);			
		} else {
			try {
				IDfSession oSession = getSession();				
				IDfSysObject oDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath + "/" + sDocumentName);
			    if (oDocument == null ) {
			        System.out.println("Document " + sDocumentName + " can not be found in path " + sDocumentPath);
			        DfLogger.warn(this, "PoC:: Document " + sDocumentName + " can not be found in path " + sDocumentPath, null, null);
			    } else {
			    	if (oDocument.isVirtualDocument()) {
				        System.out.println(sDocumentName + " is a Virtual Document");
				        DfLogger.trace(this, sDocumentName + " is a Virtual Document", null, null);				        
				    	IDfVirtualDocument oVirtualDocument = oDocument.asVirtualDocument("CURRENT", false );
		                IDfVirtualDocumentNode oRoot = oVirtualDocument.getRootNode();
		                int iChildCount = oRoot.getChildCount();
				        System.out.println(sDocumentName + " has " + iChildCount + " children");
				        DfLogger.trace(this, sDocumentName + " has " + iChildCount + " children", null, null);				        
				        for (int iChild = 0; iChild < iChildCount; iChild ++) {
				        	IDfVirtualDocumentNode oChild = oRoot.getChild(iChild);
				        	IDfSysObject oChildDocument = oChild.getSelectedObject();
				        	String sChildName = oChildDocument.getObjectName();
			                System.out.println("Document " + sChildName + " found as child of " + sDocumentName);
			                DfLogger.trace(this, "PoC:: Document " + sChildName + " found as child of " + sDocumentName, null, null);
				        	if ((sDocumentsStart != null && !sDocumentsStart.equals("") && sChildName.startsWith(sDocumentsStart))
				        	 || (sDocumentsStart == null || sDocumentsStart.equals(""))) {
				        		System.out.println("Getting permission of Id " + oPermissionId.toString());
				        		DfLogger.trace(this, "PoC:: Getting permission of Id " + oPermissionId.toString(), null, null);
				        		IDfACL oAcl = (IDfACL) oSession.getObject(oPermissionId);
				        		String sPermission = oAcl.getObjectName();
				                System.out.println("Set permission " + sPermission + " to child document " + sChildName + sDocumentName);
				                DfLogger.trace(this, "PoC:: Set permission " + sPermission + " to child document " + sChildName, null, null);
				                oChildDocument.setACL(oAcl);
				                oChildDocument.save();
				                System.out.println("Permission " + sPermission + " set to child document " + sChildName + sDocumentName);
				                DfLogger.trace(this, "PoC:: Permission " + sPermission + " set to child document " + sChildName, null, null);
				        	} else {
				                System.out.println("Skip permission setting to child document " + sChildName);
				                DfLogger.trace(this, "PoC:: Skip permission setting to child document " + sChildName, null, null);
				        	}
				        }
			    	} else {
				        System.out.println("Virtual Document " + sDocumentName + " is not a virtual document, cannot continue");
				        DfLogger.trace(this, "PoC:: Virtual Document " + sDocumentName + " is not a virtual document, cannot continue", null, null);
				    }
			    }
			    
		    } catch (Exception oException) {
		    	System.out.println("Error due to " + oException.getMessage());
		    	DfLogger.error(this, "PoC:: Error due to " + oException.getMessage(), null, oException);
		    } finally {				    	
		    }	    
		}
		return 0;
	}	
	
	private static final String DOCUMENT_PATH = "DOCUMENT_PATH";
	private static final String DOCUMENT_NAME = "DOCUMENT_NAME";
	private static final String DOCUMENTS_START = "DOCUMENTS_START";
	private static final String PERMISSION_SET = "PERMISSION_SET";
	
}