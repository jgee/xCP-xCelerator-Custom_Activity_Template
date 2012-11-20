package com.emc.documentum.extensions.methods;


import java.util.Date;
import java.util.Vector;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPackage;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfProperties;
import com.documentum.fc.common.IDfTime;
import com.emc.documentum.extensions.util.StringUtils;


public abstract class _ExtensionWorkflowMethod extends DfSingleDocbaseModule {

	protected boolean isNull(Object oObject) {
		return (oObject == null || oObject.toString().equals(""));
	}
	
	protected boolean equals(String sText, String sValue) {
		return (sText != null && sText.equals(sValue));
	}

	protected boolean equalsUnsensitive(String sText, String sValue) {
		return (sText != null && sText.equalsIgnoreCase(sValue));
	}

	protected boolean foundObject(IDfSysObject oObject) {
		boolean bNull = false;
		if (isNull(oObject)) {
			writeLog(LOG_WARN, "Object not found (it is null)");
		} else {
			try {
				String sObjectName = oObject.getObjectName();
				String sObjectType = oObject.getTypeName();
				writeLog(LOG_INFO, "Found object '" + sObjectName + "' of type '" + sObjectType + "'");
	    	    bNull = true;
			} catch (Exception oException) {
	    	    writeLog(LOG_ERROR, "Object not found, Error due to ", oException);        		
			}
		}
    	return bNull;
	}
	
	protected boolean foundPackage(IDfPackage oPackage) {
		boolean bNull = false;
		if (isNull(oPackage)) {
			writeLog(LOG_WARN, "Package not found (it is null)");
		} else {
			try {
				String sPackageName = oPackage.getPackageName();
				String sPackageType = oPackage.getPackageName();
				writeLog(LOG_INFO, "Found object '" + sPackageName + "' of type '" + sPackageType + "'");
	    	    bNull = true;
			} catch (Exception oException) {
	    	    writeLog(LOG_ERROR, "Object not found, Error due to ", oException);        		
			}
		}
    	return bNull;
	}
	
	protected boolean foundObject(IDfSession oSession, String sObjectPath) {
		boolean bFound = false;
		try {
			IDfSysObject oObject = (IDfSysObject) oSession.getObjectByPath(sObjectPath);
			if (isNull(oObject)) {
				writeLog(LOG_WARN, "Object not found in path '" + sObjectPath + "'");
			} else {
				String sObjectName = oObject.getObjectName();
				String sObjectType = oObject.getTypeName();
				writeLog(LOG_INFO, "Found object '" + sObjectName + "' of type '" + sObjectType + "' in path '" + sObjectPath + "'");
				bFound = true;
			}
		} catch (Exception oException) {
			writeLog(LOG_ERROR, "Object not found in path '" + sObjectPath + "', Error due to ", oException);
		}
    	return bFound;
	}
	
	protected String[] getLinkedPaths(IDfSession oSession, IDfSysObject oObject) {
		String[] aPaths = null;
		try {
			IDfFolder oFolder = (IDfFolder) oSession.getObject(oObject.getFolderId(0));
	    	int iPathCount = oFolder.getFolderPathCount();
	    	aPaths = new String[iPathCount];
	    	for (int iPath = 0; iPath < iPathCount; iPath ++) {
	    		aPaths[iPath] = oFolder.getFolderPath(iPath);
	    	}
	    	if (iPathCount == 1) {
	    		writeLog(LOG_DEBUG, "Document " + oObject.getObjectName() + " is stored to " + aPaths[0]);
		    } else {
	    		writeLog(LOG_WARN, "Found " + iPathCount + " paths where the document " + oObject.getObjectName() + " is stored to, the first one is " + aPaths[0]);
	    	}
		} catch (DfException oException) {
			writeLog(LOG_ERROR, "Paths not found for the specified object, Error due to ", oException);
		}
    	return aPaths;
	}

	protected String[] getVersionLabels(IDfSession oSession, IDfSysObject oObject) {
		String[] aVersionLabels = null;
		try {
        	IDfCollection oVersions = oObject.getVersions(null);
        	Vector<String> oVersionLabels = new Vector<String>();
        	while (oVersions.next()) {
        		String sVersionLabel = oVersions.getString("r_version_label");
        		oVersionLabels.add(sVersionLabel);
        		writeLog(LOG_INFO, "\tFound object " + oObject.getObjectName() + " version " + sVersionLabel);
        	}
        	aVersionLabels = StringUtils.toStringArray(oVersionLabels, false);
		} catch (DfException oException) {
			writeLog(LOG_ERROR, "Versions not found for the specified object, Error due to ", oException);
		}
    	return aVersionLabels;
	}

	protected String getWorkitemStringParameter(IDfProperties oParameters, String sParameter) throws Exception {
		String sValue = oParameters.getString(sParameter);
		if (sValue != null && !sValue.equals("")) {
			writeLog(LOG_DEBUG, "Activity parameter: " + sParameter + " = '" + sValue + "'");
		} else {
			writeLog(LOG_WARN, "Activity parameter " + sParameter + " is not specified");
		}
		return sValue;
	}

	protected double getWorkitemDecimalParameter(IDfProperties oParameters, String sParameter) throws Exception {
		double dValue = oParameters.getDouble(sParameter);
		writeLog(LOG_DEBUG, "Activity parameter " + sParameter + " = " + dValue);
		return dValue;
	}

	protected int getWorkitemNumericParameter(IDfProperties oParameters, String sParameter) throws Exception {
		int iValue = oParameters.getInt(sParameter);
		writeLog(LOG_DEBUG, "Activity parameter " + sParameter + " = " + iValue);
		return iValue;
	}

	protected boolean getWorkitemBooleanParameter(IDfProperties oParameters, String sParameter) throws Exception {
		boolean bValue = oParameters.getBoolean(sParameter);
		writeLog(LOG_DEBUG, "Activity parameter " + sParameter + " = " + bValue);
		return bValue;
	}

	protected IDfId getWorkitemIdParameter(IDfProperties oParameters, String sParameter) throws Exception {
		IDfId oValue = oParameters.getId(sParameter);
		writeLog(LOG_DEBUG, "Activity parameter " + sParameter + " = " + oValue);
		return oValue;
	}
	
	protected IDfList getWorkitemListParameter(IDfProperties oParameters, String sParameter) throws Exception {
		IDfList oPackages = oParameters.getList(sParameter);
		if (oPackages != null && oPackages.getCount() > 0) {
			writeLog(LOG_DEBUG, "At least one package specified");
		} else {
			writeLog(LOG_WARN, "Activity parameter " + sParameter + " does not contain packages");
		}
		return oPackages;
	}
	
	protected IDfPackage getWorkitemPackage(IDfWorkitem oWorkitem, String sPackage) throws Exception {
		writeLog(LOG_DEBUG, "Search " + sPackage + " workflow package");
        IDfPackage oPackage = null;
        IDfCollection oPackages = oWorkitem.getAllPackages(null);
        try {
        	while (oPackages.next()) {
            	String sWorkitemPackage = oPackages.getString("r_package_name");
            	writeLog(LOG_DEBUG, "\tCheck workflow package " + sWorkitemPackage);
		        if (sWorkitemPackage.equalsIgnoreCase(sPackage)) {
                	IDfId oPackageId = oPackages.getId("r_object_id");
	                oPackage = oWorkitem.getPackage(oPackageId);
	                writeLog(LOG_INFO, "\tFound package " + sPackage + " with Id " + oPackageId.toString());
		        }
            }
            if (oPackage == null) {
            	writeLog(LOG_DEBUG, "Package " + sPackage + " not found");
            }
        } finally {
            if (oPackages != null) oPackages.close();
        }
        return oPackage;
	}
	
	protected IDfDocument getPackageDocument(IDfSession oSession, IDfPackage oPackage, int iIndex, String sCheckType) throws Exception {
		return (IDfDocument) getPackageObject(oSession, oPackage, iIndex, sCheckType);
	}
	
	protected IDfSysObject getPackageObject(IDfSession oSession, IDfPackage oPackage, int iIndex, String sCheckType) throws Exception {
		IDfSysObject oDocument = null;
		if (!isNull(oPackage)) {
			IDfId oDocumentId = null;
			try {
				oDocumentId = oPackage.getComponentId(iIndex);
		    } catch (Exception oException) {
		    	writeLog(LOG_ERROR, "Cannot retrieve package component, Error due to " + oException.getMessage());
		    }
			if (oDocumentId == null || oDocumentId.isNull()) {
				writeLog(LOG_WARN, "Package document not found at position " + iIndex);
        	} else {
        		oDocument = (IDfSysObject) oSession.getObject(oDocumentId);
        		String sType =  oDocument.getTypeName(); 
        		writeLog(LOG_DEBUG, "Package document " + oDocument.getObjectName() + " of type " + sType + " found at position " + iIndex);
        		if (sCheckType != null) {
        			if (sType.equals(sCheckType)) {        		
        				writeLog(LOG_INFO, "Found package document " + oDocument.getObjectName() + " of type " + sType + " of the correct type");
	        		} else {
	        			writeLog(LOG_WARN, "Found package document " + oDocument.getObjectName() + " of type " + sType + ", it is not of the correct type " + sCheckType);
	    		        oDocument = null;
	        		}
        		}
        	}
		}
		return oDocument;
	}
	
	protected void addDocumentToPackage(IDfSession session, IDfWorkitem oWorkitem, IDfPackage oPackage, String sDocumentPath) throws Exception {
		Vector<String> oDocumentPaths = new Vector<String>();
		oDocumentPaths.add(sDocumentPath);
		addDocumentsToPackage(session, oWorkitem, oPackage, oDocumentPaths);
	}
	
	protected void addDocumentsToPackage(IDfSession session, IDfWorkitem oWorkitem, IDfPackage oPackage, Vector<String> oDocumentPaths) throws Exception {
        if (isNull(oPackage) || isNull(oDocumentPaths) || oDocumentPaths.size() == 0) {
        	writeLog(LOG_WARN, "Cannot add documents to package, because the package is not found or document paths are missing");
        } else {
        	String sPackage = oPackage.getPackageName();
        	try {
        		IDfSession oSession = session;
    			IDfClientX oClientx = new DfClientX();
            	IDfList oDocumentIds = oClientx.getList();
		    	oDocumentIds.setElementType(IDfList.DF_ID);
            	for (String sDocumentPath : oDocumentPaths) {
            		writeLog(LOG_DEBUG, "\tPrepare document " + sDocumentPath + " to be added to package " + sPackage + " of type " + oPackage.getPackageType());
            		IDfSysObject oDocument = (IDfSysObject) oSession.getObjectByPath(sDocumentPath);
            		oDocumentIds.append(oDocument.getObjectId());
            	}
	    		writeLog(LOG_INFO, "\tAdd " + oDocumentIds.getCount() + " documents to package " + sPackage + " of type " + oPackage.getPackageType());
                oWorkitem.addPackage(sPackage, oPackage.getPackageType(), oDocumentIds);
		    } catch (Exception oException) {
		    	writeLog(LOG_ERROR, "Cannot add documents to workflow package " + sPackage + ", Error due to " + oException.getMessage(), oException);
	        }
        }
	}

	protected void appendDocumentAttribute(IDfDocument oDocument, String sAttribute, String sValue, boolean bRemoveAll, boolean bSave) throws Exception {
    	if (oDocument.isAttrRepeating(sAttribute)) {
    		writeLog(LOG_DEBUG, "Attribute " + sAttribute + " is repeating");
            if (bRemoveAll) {
            	writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute + " is clean");
            	oDocument.removeAll(sAttribute);
            }
            writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute + " filling");
        	oDocument.appendString(sAttribute, sValue);
        	writeLog(LOG_INFO, "\tAttribute " + sAttribute + " appended with value [" + sValue + "]");
            if (bSave) oDocument.save();
		} else {
			writeLog(LOG_WARN, "Attribute " + sAttribute + " is not repeating, it cannot be set");
		}
	}

	protected int setDocumentAttribute(IDfDocument oDocument, String sAttribute, String aValues[], boolean bRemoveAll, boolean bSave) throws Exception {
    	int iCount = 0;
    	if (oDocument.isAttrRepeating(sAttribute)) {
    		writeLog(LOG_DEBUG, "Attribute " + sAttribute + " is repeating");
            if (bRemoveAll) {
            	writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute + " is clean");
            	oDocument.removeAll(sAttribute);
            }
            writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute + " filling");
            while (aValues != null && iCount < aValues.length) {
            	oDocument.appendString(sAttribute, aValues[iCount]);
            	String sCurrentValue = oDocument.getRepeatingString(sAttribute, iCount);
            	writeLog(LOG_INFO, "\tAttribute " + sAttribute + " now is set to [" + sCurrentValue + "] at position " + iCount);
                iCount ++;
            }
            writeLog(LOG_DEBUG, "Repeating attribute " + sAttribute + " filled with " + iCount + " values");
            if (bSave) oDocument.save();
		} else {
			writeLog(LOG_WARN, "Attribute " + sAttribute + " is not repeating, it cannot be set");
		}
        return iCount;
	}

	protected void setDocumentAttribute(IDfDocument oDocument, String sAttribute, String sValue, boolean bSave) throws Exception {
    	String sCurrentValue = oDocument.getString(sAttribute);
    	writeLog(LOG_DEBUG, "Attribute " + sAttribute + " was set to [" + sCurrentValue + "]");
        oDocument.setString(sAttribute, sValue);
    	sCurrentValue = oDocument.getString(sAttribute);
    	writeLog(LOG_INFO, "Attribute " + sAttribute + " now is set to [" + sCurrentValue + "]");
        if (bSave) oDocument.save();
	}
	
	protected void setDocumentAttribute(IDfDocument oDocument, String sAttribute, int iValue, boolean bSave) throws Exception {
    	int iCurrentValue = oDocument.getInt(sAttribute);
    	writeLog(LOG_DEBUG, "Attribute " + sAttribute + " was set to " + iCurrentValue);
        oDocument.setInt(sAttribute, iValue);
    	iCurrentValue = oDocument.getInt(sAttribute);
    	writeLog(LOG_INFO, "Attribute " + sAttribute + " now is set to " + iCurrentValue);
        DfLogger.trace(this, "PoC:: Attribute " + sAttribute + " now is set to " + iCurrentValue, null, null);
        if (bSave) oDocument.save();
	}

	protected void setDocumentAttribute(IDfDocument oDocument, String sAttribute, double dValue, boolean bSave) throws Exception {
		double dCurrentValue = oDocument.getInt(sAttribute);
    	writeLog(LOG_DEBUG, "Attribute " + sAttribute + " was set to " + dCurrentValue);
        oDocument.setDouble(sAttribute, dValue);
    	dCurrentValue = oDocument.getDouble(sAttribute);
    	writeLog(LOG_INFO, "Attribute " + sAttribute + " now is set to " + dCurrentValue);
        if (bSave) oDocument.save();
	}
	
	protected void setDocumentAttribute(IDfDocument oDocument, String sAttribute, Date oValue, boolean bSave) throws Exception {
    	IDfTime oCurrentValue = oDocument.getTime(sAttribute);
    	writeLog(LOG_DEBUG, "Attribute " + sAttribute + " was set to " + oCurrentValue.toString());
        oDocument.setTime(sAttribute, new DfTime((Date) oValue));
        oCurrentValue = oDocument.getTime(sAttribute);
    	writeLog(LOG_INFO, "Attribute " + sAttribute + " now is set to " + oCurrentValue.toString());
        if (bSave) oDocument.save();
	}
	
	protected void setClass(String sClass) {
		_sClass = sClass;
		writeLog(LOG_DEBUG, START_METHOD_MESSAGE);
	}
	
	protected void writeLog(String sLevel, String sMessage) {
		writeLog(sLevel, sMessage, null);
	}
	
	protected void writeLog(String sLevel, String sMessage, Throwable oException) {
		String sPrefix = "PoC:: "; // + isNull(_sClass) ? "" : _sClass + " - ";
		System.out.println(sPrefix + sMessage);
		if (sLevel.equals(LOG_ERROR)) {
			DfLogger.error(this, sPrefix + sMessage, null, oException);
		} else if (sLevel.equals(LOG_WARN)) {
			DfLogger.warn(this, sPrefix + sMessage, null, oException);
		} else if (sLevel.equals(LOG_INFO)) {
			DfLogger.trace(this, sPrefix + sMessage, null, oException);
		} else if (sLevel.equals(LOG_DEBUG)) {
			DfLogger.trace(this, sPrefix + sMessage, null, oException);
		} else {
			DfLogger.trace(this, sPrefix + sMessage, null, oException);
		}
	}
	
	protected String _sClass = "ExtensionWorkflowMethod";
	
	protected static final String LOG_DEBUG = "DEBUG";
	protected static final String LOG_INFO = "INFO";
	protected static final String LOG_WARN = "WARN";
	protected static final String LOG_ERROR = "ERROR";
	protected static final String START_METHOD_MESSAGE = "--- START BPM METHOD";
	protected static final String STOP_METHOD_MESSAGE = "Method cannot continue, not enough parameters";
	protected static final String END_METHOD_MESSAGE = "---- END BPM METHOD";
	protected static final String METHOD_NOT_IMPLEMENTED_MESSAGE = "PLEASE ADD YOUR CODE TO THIS METHOD!";
}
