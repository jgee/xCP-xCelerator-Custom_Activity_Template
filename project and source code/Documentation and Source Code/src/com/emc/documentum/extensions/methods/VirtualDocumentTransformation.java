package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfProperties;

public class VirtualDocumentTransformation extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String DOC_PATH, String DOC_NAME, boolean OTHER_DOCS, PrintWriter oPrintWriter) throws Exception
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
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(DOCUMENT_PATH, DOC_PATH);
		oParameters.putString(DOCUMENT_NAME, DOC_NAME);
		oParameters.putBoolean(OTHER_DOCUMENTS, OTHER_DOCS);
		
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
		boolean bOtherDocuments = oActivityParameters.getBoolean(OTHER_DOCUMENTS);
		if (bOtherDocuments) {
			System.out.println("Other documents is true");
			DfLogger.trace(this, "PoC:: Other documents is true", null, null);
		} else {
			System.out.println("Other documents is false");
		  	DfLogger.trace(this, "PoC:: Other documents is false", null, null);
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
				        System.out.println(sDocumentName + " is already a Virtual Document");
				        DfLogger.trace(this, sDocumentName + " is already a Virtual Document", null, null);			    	
				    } else {
				    	oDocument.checkout();
				    	oDocument.setIsVirtualDocument(true);
				    	oDocument.save();
				        System.out.println("Virtual Document " + sDocumentName + " created");
				        DfLogger.trace(this, "PoC:: Virtual Document " + sDocumentName + " created", null, null);
				    }
				    if (bOtherDocuments) {
				    	IDfFolder oFolder = getSession().getFolderByPath(sDocumentPath);
		                System.out.println("Scanning folder " + sDocumentPath + " for other documents");
		                DfLogger.trace(this, "PoC:: Scanning folder " + sDocumentPath + " for other documents", null, null);
				    	IDfVirtualDocument oVirtualDocument = oDocument.asVirtualDocument("CURRENT", false );
		                IDfVirtualDocumentNode oRoot = oVirtualDocument.getRootNode();
				    	IDfCollection oOtherDocuments = oFolder.getContents("object_name");
				        try {
				        	int iOtherDocument = 0;
				            while (oOtherDocuments.next()) {
				            	String sOtherDocument = oOtherDocuments.getString("object_name");
				                if (sOtherDocument.compareToIgnoreCase(sDocumentName) != 0) {
					                System.out.println("Found document " + sOtherDocument);
					                DfLogger.trace(this, "PoC:: Found document " + sOtherDocument, null, null);
					                IDfSysObject oOtherDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath + "/" + sOtherDocument);
					                oDocument.checkout();
					                oVirtualDocument.addNode(oRoot, null, oOtherDocument.getChronicleId(), "CURRENT", false, false);
					                oDocument.save();
					                System.out.println("Document " + sOtherDocument + " added as child of " + sDocumentName);
					                DfLogger.trace(this, "PoC:: Document " + sOtherDocument + " added as child of " + sDocumentName, null, null);
					                iOtherDocument ++;
				                }
				            }
			                if (iOtherDocument > 0) {
				                System.out.println(iOtherDocument + " documents added as child of " + sDocumentName);
				                DfLogger.trace(this, "PoC:: " + iOtherDocument + " documents added as child of " + sDocumentName, null, null);
			                } else {
				                System.out.println("No documents added as child of " + sDocumentName);
				                DfLogger.trace(this, "PoC:: No documents added as child of " + sDocumentName, null, null);			                	
			                }
				        } finally {
				            if (oOtherDocuments != null) oOtherDocuments.close();
				        }
		                System.out.println("Folder " + sDocumentPath + " scanned, virtual document " + sDocumentName + " composed");
		                DfLogger.trace(this, "PoC:: Folder " + sDocumentPath + " scanned, virtual document " + sDocumentName + " composed", null, null);
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
	private static final String OTHER_DOCUMENTS = "OTHER_DOCUMENTS";
	
}