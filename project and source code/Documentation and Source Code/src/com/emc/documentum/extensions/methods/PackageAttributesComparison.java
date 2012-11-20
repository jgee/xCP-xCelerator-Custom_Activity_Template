package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class PackageAttributesComparison extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String PACKAGE_NAME_1, String ATTRIBUTE_NAME_1, String REPEATING_INDEX_1, String PACKAGE_NAME_2, String ATTRIBUTE_NAME_2, String REPEATING_INDEX_2, String PACKAGE_TO_SET, String ATTRIBUTE_TO_SET, String ATTRIBUTES_TYPE, boolean COMPARE_UNSENSITIVE, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(PACKAGE_NAME_1 == null)
		{
			PACKAGE_NAME_1 = "";
		}
		if(ATTRIBUTE_NAME_1 == null)
		{
			ATTRIBUTE_NAME_1 = "";
		}
		if(REPEATING_INDEX_1 == null)
		{
			REPEATING_INDEX_1 = "";
		}
		if(PACKAGE_NAME_2 == null)
		{
			PACKAGE_NAME_2 = "";
		}
		if(ATTRIBUTE_NAME_2 == null)
		{
			ATTRIBUTE_NAME_2 = "";
		}
		if(REPEATING_INDEX_2 == null)
		{
			REPEATING_INDEX_2 = "";
		}
		if(PACKAGE_TO_SET == null)
		{
			PACKAGE_TO_SET = "";
		}
		if(ATTRIBUTE_TO_SET == null)
		{
			ATTRIBUTE_TO_SET = "";
		}
		if(ATTRIBUTES_TYPE == null)
		{
			ATTRIBUTES_TYPE = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("PACKAGE_NAME_1", PACKAGE_NAME_1);
		oParameters.putString("ATTRIBUTE_NAME_1", ATTRIBUTE_NAME_1);
		oParameters.putString("REPEATING_INDEX_1", REPEATING_INDEX_1);
		oParameters.putString("PACKAGE_NAME_2", PACKAGE_NAME_2);
		oParameters.putString("ATTRIBUTE_NAME_2", ATTRIBUTE_NAME_2);
		oParameters.putString("REPEATING_INDEX_2", REPEATING_INDEX_2);
		oParameters.putString("PACKAGE_TO_SET", PACKAGE_TO_SET);
		oParameters.putString("ATTRIBUTE_TO_SET", ATTRIBUTE_TO_SET);
		oParameters.putString("ATTRIBUTES_TYPE", ATTRIBUTES_TYPE);
		oParameters.putBoolean("COMPARE_UNSENSITIVE", COMPARE_UNSENSITIVE);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("PackageAttributesComparison");
		
		String sPackage1 = getWorkitemStringParameter(oParameters, "PACKAGE_NAME_1");
		String sAttribute1 = getWorkitemStringParameter(oParameters, "ATTRIBUTE_NAME_1");
		String sIndex1 = getWorkitemStringParameter(oParameters, "REPEATING_INDEX_1");
		String sPackage2 = getWorkitemStringParameter(oParameters, "PACKAGE_NAME_2");
		String sAttribute2 = getWorkitemStringParameter(oParameters, "ATTRIBUTE_NAME_2");
		String sIndex2 = getWorkitemStringParameter(oParameters, "REPEATING_INDEX_2");
		String sPackage = getWorkitemStringParameter(oParameters, "PACKAGE_TO_SET");
		String sAttribute = getWorkitemStringParameter(oParameters, "ATTRIBUTE_TO_SET");
		String sTypes = getWorkitemStringParameter(oParameters, "ATTRIBUTES_TYPE");
		boolean bUnsensitive = getWorkitemBooleanParameter(oParameters, "COMPARE_UNSENSITIVE");

		if (isNull(sPackage1) || isNull(sAttribute1) ||
			isNull(sPackage2) || isNull(sAttribute2)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		} 

		try {		
			IDfSession oSession = getSession();			

	    	String sCurrentValue1 = null;
	    	String sCurrentValue2 = null;
	    	double iCurrentValue1 = 0;
	    	double iCurrentValue2 = 0;
	    	double dCurrentValue1 = 0;
	    	double dCurrentValue2 = 0;
	    	
        	IDfPackage oPackage1 = getWorkitemPackage(oWorkitem, sPackage1);
        	IDfDocument oDocument1 = getPackageDocument(oSession, oPackage1, 0, null);
        	if (oDocument1 != null) {
        		if (oDocument1.isAttrRepeating(sAttribute1)) {
        			int iIndex = StringUtils.toInt(sIndex1);
        			writeLog(LOG_DEBUG, "Attribute " + sAttribute1 + " of package " + sPackage1 + " is repeating, extracting position is " + iIndex);
        			if (sTypes.equals("String")) {
	        			sCurrentValue1 = oDocument1.getRepeatingString(sAttribute1, iIndex);
	        			writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute1 + "[" + iIndex + "] of package " + sPackage1 + " is set to " + sCurrentValue1);
	        		} else if (sTypes.equals("Numeric")) { 	
	        			iCurrentValue1 = oDocument1.getRepeatingInt(sAttribute1, iIndex);
        				writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute1 + "[" + iIndex + "] of package " + sPackage1 + " is set to " + iCurrentValue1);
        			} else if (sTypes.equals("Decimal")) { 	
	        			dCurrentValue1 = oDocument1.getRepeatingDouble(sAttribute1, iIndex);
        				writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute1 + "[" + iIndex + "] of package " + sPackage1 + " is set to " + dCurrentValue1);
        			} else {
        				writeLog(LOG_WARN, "Repeating attribute type " + sTypes + " is not handle yet, PLEASE ADD YOUR CODE TO THIS METHOD!");
        			}
        		} else {
        			if (sTypes.equals("String")) {
        				sCurrentValue1 = oDocument1.getString(sAttribute1);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute1 + " of package " + sPackage1 + " is set to " + sCurrentValue1);
            		} else if (sTypes.equals("Numeric")) {
        				iCurrentValue1 = oDocument1.getInt(sAttribute1);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute1 + " of package " + sPackage1 + " is set to " + iCurrentValue1);
            		} else if (sTypes.equals("Decimal")) {
        				dCurrentValue1 = oDocument1.getDouble(sAttribute1);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute1 + " of package " + sPackage1 + " is set to " + dCurrentValue1);
        			} else {
        				writeLog(LOG_WARN, "Attribute type " + sTypes + " is not handle yet, PLEASE ADD YOUR CODE TO THIS METHOD!");
        			}
       			}
        	}
        	IDfPackage oPackage2 = getWorkitemPackage(oWorkitem, sPackage2);
        	IDfDocument oDocument2 = getPackageDocument(oSession, oPackage2, 0, null);
        	if (oDocument2 != null) {
        		if (oDocument2.isAttrRepeating(sAttribute2)) {
        			int iIndex = StringUtils.toInt(sIndex2);
        			writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute2 + " of package " + sPackage2 + " is repeating, extracting position is " + iIndex);
        			if (sTypes.equals("String")) {
	        			sCurrentValue2 = oDocument2.getRepeatingString(sAttribute2, iIndex);
	        			writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute2 + "[" + iIndex + "] of package " + sPackage2 + " is set to " + sCurrentValue2);
	        		} else if (sTypes.equals("Numeric")) { 	
	        			iCurrentValue2 = oDocument2.getRepeatingInt(sAttribute2, iIndex);
        				writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute2 + "[" + iIndex + "] of package " + sPackage2 + " is set to " + iCurrentValue2);
	        		} else if (sTypes.equals("Decimal")) { 	
	        			dCurrentValue2 = oDocument2.getRepeatingDouble(sAttribute2, iIndex);
        				writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute2 + "[" + iIndex + "] of package " + sPackage2 + " is set to " + dCurrentValue2);
        			} else {
        				writeLog(LOG_WARN, "Repeating attribute type " + sTypes + " is not handle yet, PLEASE ADD WORKING CODE TO THIS METHOD!");
        			}
        		} else {
        			if (sTypes.equals("String")) {
        				sCurrentValue2 = oDocument2.getString(sAttribute2);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute2 + " of package " + sPackage2 + " is set to " + sCurrentValue2);
            		} else if (sTypes.equals("Numeric")) {
        				iCurrentValue2 = oDocument2.getInt(sAttribute2);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute2 + " of package " + sPackage2 + " is set to " + iCurrentValue2);
            		} else if (sTypes.equals("Decimal")) {
        				dCurrentValue2 = oDocument2.getDouble(sAttribute2);
        				writeLog(LOG_DEBUG, "Attribute " + sAttribute2 + " of package " + sPackage2 + " is set to " + dCurrentValue2);
        			} else {
        				writeLog(LOG_WARN, "Attribute type " + sTypes + " is not handle yet, PLEASE ADD WORKING CODE TO THIS METHOD!");
        			}
       			}
        	}

        	IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackage);
        	IDfDocument oDocument = getPackageDocument(oSession, oPackage, 0, null);
        	if (oDocument != null) {
	        	int iCurrentCompare = (int) oDocument.getDouble(sAttribute);
	        	writeLog(LOG_DEBUG, "Attribute " + sAttribute + " of package " + sPackage + " was set to " + iCurrentCompare);
	        	int iCompare = 0;
	        	if (sTypes.equals("String")) {
	        		int iResult; 
		        	if (bUnsensitive) {
		        		iResult = sCurrentValue1.compareToIgnoreCase(sCurrentValue2);
		        	} else {
		        		iResult = sCurrentValue1.compareTo(sCurrentValue2);
		        	}
		        	iCompare = ((iResult < 0) ? -1 : (iResult > 0) ? 1 : 0);
	        	} else if (sTypes.equals("Numeric")) {
		        	if (iCurrentValue1 < iCurrentValue2) {
		        		iCompare = -1;
	                } else if (iCurrentValue1 > iCurrentValue2) { 
	                	iCompare = 1;
	                } else { 
	                	iCompare = 0;
	                }
	        	} else if (sTypes.equals("Decimal")) {
		        	if (dCurrentValue1 < dCurrentValue2) {
		        		iCompare = -1;
	                } else if (dCurrentValue1 > dCurrentValue2) { 
	                	iCompare = 1;
	                } else { 
	                	iCompare = 0;
	                }
    			} else {
    				writeLog(LOG_WARN, "Attribute type " + sTypes + " is not handle yet, PLEASE ADD WORKING CODE TO THIS METHOD!");
    			}
	        	oDocument.setInt(sAttribute, iCompare);
	        	writeLog(LOG_INFO, "Attribute " + sAttribute + " of package " + sPackage + " now is set to " + iCompare);
                oDocument.save();
        	}

	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
}