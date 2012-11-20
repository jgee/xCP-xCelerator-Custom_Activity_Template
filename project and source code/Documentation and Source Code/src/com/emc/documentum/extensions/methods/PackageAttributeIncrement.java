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

public class PackageAttributeIncrement extends DfSingleDocbaseModule {
	
	public int doTask(String workitemid, String PKG_NAME, String PKG_ATTRIBUTE, int ATTR_INCREMENT,  PrintWriter oPrintWriter) throws Exception
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
		if(PKG_ATTRIBUTE == null)
		{
			PKG_ATTRIBUTE = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(PACKAGE_NAME, PKG_NAME);
		oParameters.putString(PACKAGE_ATTRIBUTE, PKG_ATTRIBUTE);
		oParameters.putInt(ATTRIBUTE_INCREMENT, ATTR_INCREMENT);
		
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
		String sPackageAttribute = oActivityParameters.getString(PACKAGE_ATTRIBUTE);
		if (sPackageAttribute != null && !sPackageAttribute.equals("")) {
			System.out.println("Package attribute: " + sPackageAttribute);
			DfLogger.trace(this, "PoC:: Package attribute: " + sPackageAttribute, null, null);
		} else {
			System.out.println("Package attribute not specified");
		  	DfLogger.trace(this, "PoC:: Package attribute not specified", null, null);
	  	    bStop = true;
		}
		int lAttributeIncrement = oActivityParameters.getInt(ATTRIBUTE_INCREMENT);
		System.out.println("Attribute increment: " + lAttributeIncrement);
  	    DfLogger.trace(this, "PoC:: Attribute increment: " + lAttributeIncrement, null, null);
		if (bStop) {
			System.out.println("Method cannot continue, not enough parameters");
	  	    DfLogger.trace(this, "PoC:: Method cannot continue, not enough parameters", null, null);			
		} else {
			try {		
				IDfSession oSession = getSession();
				
		        System.out.println("Search " + sPackageName + " workflow package");
		        DfLogger.trace(this, "PoC:: Search " + sPackageName + " workflow package", null, null);
		    	IDfCollection oPackages = oWorkitem.getAllPackages(null);
		        try {
		        	IDfPackage oPackage = null;
		        	IDfDocument oDocument = null;
		            while (oPackages.next()) {
		            	String sPackage = oPackages.getString("r_package_name");
				        System.out.println("\tCheck workflow package " + sPackage);
				        DfLogger.trace(this, "PoC:: \tCheck workflow package " + sPackage, null, null);			            	
		                if (sPackage.equalsIgnoreCase(sPackageName)) {
		                	IDfId oPackageId = oPackages.getId("r_object_id");
			                oPackage = oWorkitem.getPackage(oPackageId);
			                System.out.println("\tFound package " + sPackage + " with Id " + oPackageId.toString());
			                DfLogger.trace(this, "PoC:: Found package " + sPackage + " with Id " + oPackageId.toString(), null, null);
		    	        	IDfId oDocumentId = oPackage.getComponentId(0);
		    	        	oDocument = (IDfDocument) oSession.getObject(oDocumentId);
		    	        	int lCurrentValue = oDocument.getInt(sPackageAttribute);
			                System.out.println("\tAttribute " + sPackageAttribute + " was set to " + lCurrentValue);
			                DfLogger.trace(this, "PoC:: \tAttribute " + sPackageAttribute + " was set to " + lCurrentValue, null, null);		    	        	
		    	        	oDocument.setInt(sPackageAttribute, lCurrentValue + lAttributeIncrement);
		    	        	lCurrentValue = oDocument.getInt(sPackageAttribute);
			                System.out.println("\tAttribute " + sPackageAttribute + " now is set to " + lCurrentValue);
			                DfLogger.trace(this, "PoC:: \tAttribute " + sPackageAttribute + " now is set to " + lCurrentValue, null, null);
			                oDocument.save();
		                }
		            }
	                if (oPackage == null || oDocument == null) {
		                System.out.println("Package " + sPackageName + " or contained document not found");
		                DfLogger.trace(this, "PoC:: Package " + sPackageName + " or contained document not found", null, null);			                	
	                }
		        } finally {
		            if (oPackages != null) oPackages.close();
		        }
			    
		    } catch (Exception oException) {
		    	System.out.println("Error due to " + oException.getMessage());
		    	DfLogger.error(this, "PoC:: Error due to " + oException.getMessage(), null, oException);
		    } finally {				    	
		    }	    
		}
		return 0;
	}	
	
	private static final String PACKAGE_NAME = "PACKAGE_NAME";
	private static final String PACKAGE_ATTRIBUTE = "PACKAGE_ATTRIBUTE";
	private static final String ATTRIBUTE_INCREMENT = "ATTRIBUTE_INCREMENT";
	
}