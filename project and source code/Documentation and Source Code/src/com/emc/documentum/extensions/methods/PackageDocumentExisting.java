package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfProperties;

public class PackageDocumentExisting extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String DOC_PATH, String DOC_NAME, String PCKG_NAME, String ATTR_TO_SET, String PCKG_TO_SET, PrintWriter oPrintWriter) throws Exception
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
		if(PCKG_NAME == null)
		{
			PCKG_NAME = "";
		}
		if(ATTR_TO_SET == null)
		{
			ATTR_TO_SET = "";
		}
		if(PCKG_TO_SET == null)
		{
			PCKG_TO_SET = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(DOCUMENT_PATH, DOC_PATH);
		oParameters.putString(DOCUMENT_NAME, DOC_NAME);
		oParameters.putString(PACKAGE_NAME, PCKG_NAME);
		oParameters.putString(ATTRIBUTE_TO_SET, ATTR_TO_SET);
		oParameters.putString(PACKAGE_TO_SET, PCKG_TO_SET);
		
		return doTask(oWorkitem,  oParameters,  oPrintWriter);
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
		String sPackageName = oActivityParameters.getString(PACKAGE_NAME);
		if (sPackageName != null && !sPackageName.equals("")) {
			System.out.println("Package name: " + sPackageName);
			DfLogger.trace(this, "PoC:: Package name: " + sPackageName, null, null);
		} else {
			System.out.println("Package name not specified");
		  	DfLogger.trace(this, "PoC:: Package name not specified", null, null);
		}
		String sAttributeToSetName = oActivityParameters.getString(ATTRIBUTE_TO_SET);
		if (sAttributeToSetName != null && !sAttributeToSetName.equals("")) {
			System.out.println("Attribute to set name: " + sAttributeToSetName);
			DfLogger.trace(this, "PoC:: Attribute to set name: " + sAttributeToSetName, null, null);
		} else {
			System.out.println("Attribute to set name not specified");
		  	DfLogger.trace(this, "PoC:: Attribute to set name not specified", null, null);
		}	    
		String sPackageToSetName = oActivityParameters.getString(PACKAGE_TO_SET);
		if (sPackageToSetName != null && !sPackageToSetName.equals("")) {
			System.out.println("Package to set name: " + sPackageToSetName);
			DfLogger.trace(this, "PoC:: Package to set name: " + sPackageToSetName, null, null);
		} else {
			System.out.println("Package to set name not specified");
		  	DfLogger.trace(this, "PoC:: Package to set name not specified", null, null);
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
			        if (sPackageToSetName != null && !sPackageToSetName.equals("")
			         && sAttributeToSetName != null && !sAttributeToSetName.equals("")) {
				        System.out.println("Search " + sPackageToSetName + " workflow package to set");
				        DfLogger.trace(this, "Search " + sPackageToSetName + " workflow package to set", null, null);			        	
			        	IDfCollection oPackages = oWorkitem.getAllPackages(null);
				        try {
				        	boolean bFound = false;				        	
				            while (oPackages.next()) {
				            	String sPackage = oPackages.getString("r_package_name");
						        System.out.println("Check workflow package to set " + sPackage);
						        DfLogger.trace(this, "PoC:: Check workflow package to set " + sPackage, null, null);			            	
				                if (sPackage.equalsIgnoreCase(sPackageToSetName)) {
				                	bFound = true;
				                	String sPackageId = oPackages.getString("r_object_id");
					                IDfPackage oPackage = oWorkitem.getPackage(new DfId(sPackageId));
					                System.out.println("Found package to set " + sPackage + " with Id " + sPackageId);
					                DfLogger.trace(this, "PoC:: Found package to set " + sPackage + " with Id " + sPackageId, null, null);
				    	        	IDfId oDocumentId = oPackage.getComponentId(0);
				    	        	IDfDocument oPackageDocument = (IDfDocument) oSession.getObject(oDocumentId);
				    	        	String sCurrentValue = oPackageDocument.getString(sAttributeToSetName);
					                System.out.println("Attribute " + sAttributeToSetName  + " of package " + sPackage + " was set to " + sCurrentValue);
					                DfLogger.trace(this, "PoC:: Attribute " + sAttributeToSetName + " of package " + sPackage + " was set to " + sCurrentValue, null, null);		    	        	
				    	        	oPackageDocument.setString(sAttributeToSetName, "NOTFOUND");
					                System.out.println("Attribute " + sAttributeToSetName + " of package " + sPackage + " now is set to 'NOT FOUND'");
					                DfLogger.trace(this, "PoC:: Attribute " + sAttributeToSetName + " of package " + sPackage + " now is set to 'NOT FOUND'", null, null);				                
					                oPackageDocument.save();
				                }
				            }
			                if (!bFound) {
				                System.out.println("Package to set " + sPackageToSetName + " not found");
				                DfLogger.trace(this, "PoC:: Package to set " + sPackageToSetName + " not found", null, null);			                	
			                }
				        } finally {
				            if (oPackages != null) oPackages.close();
				        }			    			        	
			        }
			    } else {
			        System.out.println("Document " + sDocumentName + " found in path " + sDocumentPath);
			        DfLogger.trace(this, "PoC:: Document " + sDocumentName + " found in path " + sDocumentPath, null, null);
			        if (sPackageName != null && !sPackageName.equals("")) {
				        System.out.println("Search " + sPackageName + " workflow package");
				        DfLogger.trace(this, "Search " + sPackageName + " workflow package", null, null);
				    	IDfCollection oPackages = oWorkitem.getAllPackages(null);
				        try {
				        	boolean bFound = false;
				            while (oPackages.next()) {
				            	String sPackage = oPackages.getString("r_package_name");
						        System.out.println("Check workflow package " + sPackage);
						        DfLogger.trace(this, "PoC:: Check workflow package " + sPackage, null, null);			            	
				                if (sPackage.equalsIgnoreCase(sPackageName)) {
				                	bFound = true;
				                	String sPackageId = oPackages.getString("r_object_id");
					                IDfPackage oPackage = oWorkitem.getPackage(new DfId(sPackageId));
					                System.out.println("Found package " + sPackage + " with Id " + sPackageId);
					                DfLogger.trace(this, "PoC:: Found package " + sPackage + " with Id " + sPackageId, null, null);

					    			IDfClientX oClientx = new DfClientX();
					            	IDfList oDocumentIds = oClientx.getList();
					               
					                oDocumentIds.setElementType(IDfList.DF_ID);
					                oDocumentIds.appendId(oDocument.getObjectId());
					                System.out.println("Add document " + sDocumentName + " of type " + oDocument.getTypeName() + " to package " + sPackage + " of type " + oPackage.getPackageType());
					                DfLogger.trace(this, "PoC:: Add document " + sDocumentName + " of type " + oDocument.getTypeName() + " to package " + sPackage + " of type " + oPackage.getPackageType(), null, null);				                
					                oWorkitem.addPackage(sPackage, oPackage.getPackageType(), oDocumentIds);
				                }
				            }
			                if (!bFound) {
				                System.out.println("Package " + sPackageName + " not found");
				                DfLogger.trace(this, "PoC:: Package " + sPackageName + " not found", null, null);			                	
			                }
				        } finally {
				            if (oPackages != null) oPackages.close();
				        }
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
	private static final String PACKAGE_NAME = "PACKAGE_NAME";
	private static final String ATTRIBUTE_TO_SET = "ATTRIBUTE_TO_SET";
	private static final String PACKAGE_TO_SET = "PACKAGE_TO_SET";	
}