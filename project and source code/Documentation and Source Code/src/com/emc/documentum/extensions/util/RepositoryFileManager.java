package com.emc.documentum.extensions.util;


import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.documentum.operations.*;
import com.documentum.com.*;

import java.io.*;
import java.util.*;

public class RepositoryFileManager implements IRepositoryFileManager {
	
	private RepositoryFileManager() {
		// Makes this constructor not accessible, this class is single-instance
	}
	
	public static IRepositoryFileManager getInstance() {
		if (_oRepositoryFileManager == null) _oRepositoryFileManager = new RepositoryFileManager();
		return _oRepositoryFileManager;
	}
	
	public String[] readPlainDocument(IDfDocument oDocument, boolean bSkipEmptyLines, boolean bSkipEmptyTrimmedLines, boolean bTrimLines) throws Exception {
		String aDocumentLines[] = null;
		System.out.println("Read " + oDocument.getObjectName() + " text content");
		DfLogger.trace(this, "PoC:: Read " + oDocument.getObjectName() + " text content", null, null);
        ByteArrayInputStream oContent = oDocument.getContent();
        BufferedReader oReader = new BufferedReader(new InputStreamReader(oContent));
        Vector<String> oLines = new Vector<String>();
        String sLine = oReader.readLine();
        while (sLine != null) {
        	String sTrim = sLine.trim();
        	if ((bSkipEmptyLines && sLine.length() > 0) ||
        		(!bSkipEmptyLines && sLine.length() == 0) || 
        		(bSkipEmptyTrimmedLines && sTrim.length() > 0)) {
	        		oLines.add(bTrimLines ? sTrim : sLine);
        	}
        	sLine = oReader.readLine();
        }
        aDocumentLines = StringUtils.toStringArray(oLines, false);
        System.out.println("Document " + oDocument.getObjectName() + " contains " + aDocumentLines.length + " text lines");		
        DfLogger.trace(this, "PoC:: Document " + oDocument.getObjectName() + " contains " + aDocumentLines.length + " text lines", null, null);
		return aDocumentLines;
	}

	public byte[] readBinaryDocument(IDfDocument oDocument) throws Exception {
		System.out.println("Read " + oDocument.getObjectName() + " text content");
		DfLogger.trace(this, "PoC:: Read " + oDocument.getObjectName() + " text content", null, null);
        ByteArrayInputStream oContent1 = oDocument.getContent();
        ByteArrayOutputStream oContent2 = new ByteArrayOutputStream();
        int iChar = oContent1.read();
        while (iChar != -1) {
        	oContent2.write(iChar);
        	iChar = oContent1.read();
        }
        byte[] aContent = oContent2.toByteArray();
        System.out.println("Document " + oDocument.getObjectName() + " contains " + aContent.length + " bytes");		
        DfLogger.trace(this, "PoC:: Document " + oDocument.getObjectName() + " contains " + aContent.length + " bytes", null, null);
		return aContent;
	}
	
	public IDfId createPropertiesFile(IDfSession oSession, String sFilepath, String sFilename, Object[] aProperties, Object[] aValues) throws Exception {
		String sDocument = sFilename + ".properties";
		IDfSysObject oDocument = (IDfSysObject) oSession.newObject("dm_document");
		oDocument.setObjectName(sDocument);
		oDocument.setContentType("crtext");
		oDocument.link(sFilepath);
		
		StringBuffer oContent = new StringBuffer();
		for (int i = 0; i < aProperties.length; i ++) {
			if (i > 0) oContent.append(System.getProperty("line.separator"));
			oContent.append(aProperties[i]);
			oContent.append("=");
			oContent.append(aValues[i]);
		}
	    byte aContent[] = oContent.toString().getBytes();
	    ByteArrayOutputStream oOutputStream = new ByteArrayOutputStream();
	    oOutputStream.write(aContent);
	    oDocument.setContent(oOutputStream);
	    oDocument.save();
	    System.out.println("Properties file '" + sFilepath + "/" + sDocument + "' created with content: " + System.getProperty("line.separator") + oContent.toString());
	    DfLogger.trace(this, "PoC:: Properties file '" + sFilepath + "/" + sDocument + "' created with content: " + System.getProperty("line.separator") + oContent.toString(), null, null);
	    return oDocument.getObjectId();
	}
	
	public IDfDocument createDocument(IDfSession oSession, String sDocumentPath, String sDocumentName, String sDocumentType, String sContentType, byte aContent[]) throws Exception {
		System.out.println("Create document '" + sDocumentName + "' in path '" + sDocumentPath + "' of type '" + sDocumentType + "'");
		DfLogger.trace(this, "PoC:: Create document '" + sDocumentName + "' in path '" + sDocumentPath + "' of type '" + sDocumentType + "'", null, null);
		IDfDocument oDocument = (IDfDocument) oSession.newObject(sDocumentType);
		oDocument.link(sDocumentPath);
		oDocument.setObjectName(sDocumentName);
		if (sContentType != null && !sContentType.equals("")) oDocument.setContentType(sContentType);
		if (aContent != null) {
		    System.out.println("Write content of document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType);
		    DfLogger.trace(this, "PoC:: Write content of document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType, null, null);			
			ByteArrayOutputStream oOutputStream = new ByteArrayOutputStream();
		    oOutputStream.write(aContent);
		    oDocument.setContent(oOutputStream);
		    System.out.println("Content of document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType + " written");
		    DfLogger.trace(this, "PoC:: Content of document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType + " written", null, null);			
		}
		oDocument.save();
		String sDocumentId = oDocument.getObjectId().toString();
	    System.out.println("Document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType + ((aContent == null) ? "' WITHOUT" : "' WITH") + " content of type '" + sContentType + "' and Id '" + sDocumentId + "' created");
	    DfLogger.info(this, "PoC:: Document '" + sDocumentPath + "/" + sDocumentName + "' of type '" + sDocumentType + ((aContent == null) ? "' WITHOUT" : "' WITH") + " content of type '" + sContentType + "' and Id '" + sDocumentId + "' created", null, null);
	    return oDocument;
	}
	
	public IDfFolder createFolder(IDfSession oSession, String sFolderPath, String sFolderName, String sFolderType) throws Exception {
		System.out.println("Create folder '" + sFolderName + "' in path '" + sFolderPath + "' of type '" + sFolderType + "'");
		DfLogger.trace(this, "PoC:: Create folder '" + sFolderName + "' in path '" + sFolderPath + "' of type '" + sFolderType + "'", null, null);
		IDfFolder oFolder = (IDfFolder) oSession.newObject(sFolderType);
    	oFolder.setObjectName(sFolderName);
    	oFolder.link(sFolderPath);
    	oFolder.save();
    	String sFolderId = oFolder.getObjectId().toString();
	    System.out.println("Folder '" + sFolderPath + "/" + sFolderName + "' of type '" + sFolderType + " of type '" + sFolderType + "' and Id '" + sFolderId + "' created");
	    DfLogger.info(this, "PoC:: Folder '" + sFolderPath + "/" + sFolderName + "' of type '" + sFolderType + " of type '" + sFolderType + "' and Id '" + sFolderId + "' created", null, null);
        return oFolder;
	}
	
	public int setObjectTypedAttributes(IDfSysObject oObject, Map<String, String> oAttributes, String sDatetimeFormat) throws Exception {
		int iAttributes = 0;
		if (oObject != null) {
			String sObjectName = oObject.getObjectName();
			String sObjectType = oObject.getTypeName();
			if (oAttributes == null || oAttributes.isEmpty()) {
			    System.out.println("Object '" + sObjectName + " of type '" + sObjectType + "' has not attributes");
			    DfLogger.trace(this, "PoC:: Object '" + sObjectName + " of type '" + sObjectType + "' has not attributes", null, null);			
			} else {
		        System.out.println("Object '" + sObjectName + " of type '" + sObjectType + "' should have " + oAttributes.size() + " attribute(s) to set");
		        DfLogger.trace(this, "PoC:: Object '" + sObjectName + " of type '" + sObjectType + "' should have " + oAttributes.size() + " attribute(s) to set", null, null);					
		        Iterator<String> oNames = oAttributes.keySet().iterator();
				while (oNames.hasNext()) {
					String sName = (String) oNames.next();
					String sValue = (String) oAttributes.get(sName);
					System.out.println("\tSearch for attribute '" + sName + "' to set value '" + sValue + "'");
					DfLogger.trace(this, "PoC:: \tSearch for attribute '" + sName + "' to set value '" + sValue + "'", null, null);
					boolean bFound = false;
					int iObjectAttributes = 0; 
					while (iObjectAttributes < oObject.getAttrCount() && !bFound) {
						IDfAttr oAttribute = oObject.getAttr(iObjectAttributes);
			        	if (oAttribute.getName().equals(sName)) {
			        		bFound = true;		        		
			        		int iType = oAttribute.getDataType();
			        		boolean bSet = false;
			        		try {
					    	    if (iType == IDfAttr.DM_STRING) {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as a string type");
						    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a string type'", null, null);
						    	    oObject.setString(sName, sValue);
						    	    oObject.save();
						    	    bSet = true;
					    	    } else if (iType == IDfAttr.DM_INTEGER) {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as a integer type");
						    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a integer type'", null, null);				    	    	
						    	    oObject.setInt(sName, Integer.parseInt(sValue));
						    	    oObject.save();
						    	    bSet = true;
					    	    } else if (iType == IDfAttr.DM_DOUBLE) {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as a decimal type");
						    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a decimal type'", null, null);				    	    	
						    	    oObject.setDouble(sName, Double.parseDouble(sValue));
						    	    oObject.save();
						    	    bSet = true;
					    	    } else if (iType == IDfAttr.DM_BOOLEAN) {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as a boolean type");
						    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a boolean type'", null, null);				    	    	
						    	    oObject.setBoolean(sName, Boolean.parseBoolean(sValue));
						    	    oObject.save();
						    	    bSet = true;
					    	    } else if (iType == IDfAttr.DM_TIME) {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as a date/time type");
						    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a date/time type'", null, null);
						    	    if (sDatetimeFormat == null || sDatetimeFormat.equals("")) sDatetimeFormat = StringUtils.DATETIME_FORMAT;
					        		DfTime oTime = new DfTime((Date) StringUtils.toDate(sValue, sDatetimeFormat)); 
					        		oObject.setTime(sName, oTime);
					        		oObject.save();
					        		bSet = true;
					        	} else {
						    	    System.out.println("\tAttribute '" + sName + "' is defined as an unknown '" + iType + "' type");
						    	    DfLogger.warn(this, "PoC:: \tAttribute '" + sName + "' is defined as a unknown '" + iType + "' type'", null, null);				        		
					        		// Attribute not set
					        	}
				        	} catch (Exception oException) {
				    	        System.out.println("\tCannot set attribute '" + sName + "' of type '" + iType + "', error is: " + oException.getMessage());
				                DfLogger.warn(this, "PoC:: \tCannot set attribute '" + sName + "' of type '" + iType + "', error is: " + oException.getMessage(), null, oException);    		
				        	} finally {
					    	    if (bSet) {
					        	    System.out.println("\tAttribute '" + sName + "' is set to '" + sValue + "'");
					        	    DfLogger.trace(this, "PoC:: \tAttribute '" + sName + "' is set to '" + sValue + "'", null, null);        		
					        	    iAttributes ++;
					        	}
				        	}
			        	}
			        	iObjectAttributes ++;
					}
					if (!bFound) {
			    	    System.out.println("\tAttribute '" + sName + "' not found");
			    	    DfLogger.warn(this, "PoC:: \tAttribute '" + sName + "' not found", null, null);				
					}
				}
		        System.out.println("Set " + iAttributes + " attribute(s) in folder '" + sObjectName + "'");
		        DfLogger.trace(this, "PoC:: Set " + iAttributes + " attribute(s) in folder '" + sObjectName + "'", null, null);		    	
	        }
		}
		return iAttributes;
	}

	public int setObjectFoundAttributes(IDfSysObject oObject, Map<String, Object> oAttributes) throws Exception {
		int iAttributes = 0;
		if (oObject != null) {
			String sObjectName = oObject.getObjectName();
			String sObjectType = oObject.getTypeName();
			if (oAttributes == null || oAttributes.isEmpty()) {
			    System.out.println("Object '" + sObjectName + " of type '" + sObjectType + "' has not attributes");
			    DfLogger.trace(this, "PoC:: Object '" + sObjectName + " of type '" + sObjectType + "' has not attributes", null, null);			
			} else {
		        System.out.println("Object '" + sObjectName + " of type '" + sObjectType + "' should have " + oAttributes.size() + " attribute(s) to set");
		        DfLogger.trace(this, "PoC:: Object '" + sObjectName + " of type '" + sObjectType + "' should have " + oAttributes.size() + " attribute(s) to set", null, null);					
		        Iterator<String> oAttributeNames = oAttributes.keySet().iterator();
		        while (oAttributeNames.hasNext()) {
		        	String sName = (String) oAttributeNames.next();
		        	Object oValue = oAttributes.get(sName);        	
		        	String sClass = oValue.getClass().toString();
		    	    System.out.println("\tAttribute '" + sName + "' is defined as a Java '" + sClass + "' type and its value is '" + oValue.toString() + "'");
		    	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a Java '" + sClass + "' type and its value is '" + oValue.toString() + "'", null, null);        		
		    	    boolean bSet = false;
		    	    try {
			    	    if (sClass.equals("class java.lang.String")) {
			    	    	oObject.setString(sName, (String) oValue);
			    	    	bSet = true;
			        	} else if (sClass.equals("class java.lang.Integer")) {
			        		oObject.setInt(sName, ((Integer) oValue).intValue());
			        		bSet = true;
			        	} else if (sClass.equals("class java.lang.Double")) {
			        		oObject.setDouble(sName, ((Double) oValue).doubleValue());
			        		bSet = true;
			        	} else if (sClass.equals("class java.lang.Boolean")) {
			        		oObject.setBoolean(sName, ((Boolean) oValue).booleanValue());
			        		bSet = true;
			        	} else if (sClass.equals("class java.util.Date")) {
			        		DfTime oTime = new DfTime((Date) oValue); 
			        		oObject.setTime(sName, oTime);
			        		bSet = true;
			        	} else {
			        	    System.out.println("\tAttribute '" + sName + "' is defined as an unmanaged Java type '" + sClass + "'");
			        	    DfLogger.warn(this, "PoC:: \tAttribute '" + sName + "' is defined as an unmanaged Java type '" + sClass + "'", null, null);        		
			        	    // Attribute not set
			        	}
		        	} catch (Exception oException) {
		    	        System.out.println("\tCannot set attribute '" + sName + "' of Java type '" + sClass + "', error is: " + oException.getMessage());
		                DfLogger.error(this, "PoC:: \tCannot set attribute '" + sName + "' of Java type ', error is: " + oException.getMessage() + sClass, null, null);    		
			        } finally {
			    	    if (bSet) {
			        	    System.out.println("\tAttribute '" + sName + "' is set to '" + oValue.toString() + "'");
			        	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is set to '" + oValue.toString() + "'", null, null);        		
			        	    iAttributes ++;
			        	}
			        }    	    
		        }
			}
		}
	    // 4. Save the document on the repository and return
		oObject.save();
	    return iAttributes;
	}
	
	public boolean isObjectSystemAttribute(String sAttribute) {
		boolean bSystem = false;
		int iSystem = 0;
		while (!bSystem && iSystem < SYSTEM_ATTRIBUTES.length) {
			bSystem = sAttribute.equals(SYSTEM_ATTRIBUTES[iSystem]);
			iSystem ++;
		}
		return bSystem;
	}
	
	public IDfDocument copyDocument(IDfSession oSession, String sSourcePath, String sSourceDocument, String sTargetPath, String sTargetDocument) throws Exception {
		IDfDocument oDocument = (IDfDocument) oSession.getObjectByPath(sSourcePath + "/" + sSourceDocument);
		IDfFolder oSourcePath = (IDfFolder) oSession.getObjectByPath(sSourcePath);
		IDfFolder oTargetPath = (IDfFolder) oSession.getObjectByPath(sTargetPath);
	    if (oSourcePath == null || oDocument == null) {
	    	System.out.println("PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath);
	    	DfLogger.warn(this, "PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath, null, null);
	    } else if (oTargetPath == null) {
            System.out.println("Target path " + sTargetPath + " not found");
	    	DfLogger.warn(this, "PoC:: Target path " + sTargetPath + " not found", null, null);
	    } else {
	    	System.out.println("PoC:: Copy document " + sSourceDocument + " in path " + sSourcePath + " to document " + sTargetDocument + " in path " + sTargetPath);
	    	DfLogger.trace(this, "PoC:: Copy document " + sSourceDocument + " in path " + sSourcePath + " to document " + sTargetDocument + " in path " + sTargetPath, null, null);
	    	oDocument.setObjectName(sTargetDocument);
	    	oDocument.unlink(sSourcePath);
	    	oDocument.link(sTargetPath);
	    	oDocument.saveAsNew(true);
	    	System.out.println("PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " copied to document " + sTargetDocument + " in path " + sTargetPath);
	    	DfLogger.info(this, "PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " copied to document " + sTargetDocument + " in path " + sTargetPath, null, null);
	    }
	    return oDocument;
	}

	public IDfDocument linkDocument(IDfSession oSession, String sSourcePath, String sSourceDocument, String sTargetPath) throws Exception {
		IDfDocument oDocument = (IDfDocument) oSession.getObjectByPath(sSourcePath + "/" + sSourceDocument);
		IDfFolder oSourcePath = (IDfFolder) oSession.getObjectByPath(sSourcePath);
		IDfFolder oTargetPath = (IDfFolder) oSession.getObjectByPath(sTargetPath);
	    if (oSourcePath == null || oDocument == null) {
	    	System.out.println("PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath);
	    	DfLogger.warn(this, "PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath, null, null);
	    } else if (oTargetPath == null) {
            System.out.println("Target path " + sTargetPath + " not found");
	    	DfLogger.warn(this, "PoC:: Target path " + sTargetPath + " not found", null, null);
	    } else {
	    	System.out.println("PoC:: Link document " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath);
	    	DfLogger.trace(this, "PoC:: Link document " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath, null, null);
	    	oDocument.link(sTargetPath);
	    	oDocument.save();
	    	System.out.println("PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " linked to path " + sTargetPath);
	    	DfLogger.info(this, "PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " linked to path " + sTargetPath, null, null);
	    }
	    return oDocument;
	}

	public IDfDocument moveDocument(IDfSession oSession, String sSourcePath, String sSourceDocument, String sTargetPath, String sTargetDocument) throws Exception {
		IDfDocument oDocument = (IDfDocument) oSession.getObjectByPath(sSourcePath + "/" + sSourceDocument);
		IDfFolder oSourcePath = (IDfFolder) oSession.getObjectByPath(sSourcePath);
		IDfFolder oTargetPath = (IDfFolder) oSession.getObjectByPath(sTargetPath);
	    if (oSourcePath == null || oDocument == null) {
	    	System.out.println("PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath);
	    	DfLogger.warn(this, "PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath, null, null);
	    } else if (oTargetPath == null) {
            System.out.println("Target path " + sSourcePath + " not found");
	    	DfLogger.warn(this, "PoC:: Target path " + sSourcePath + " not found", null, null);
	    } else {
	    	System.out.println("PoC:: Copy document " + sSourceDocument + " in path " + sSourcePath + " to document " + sTargetDocument + " in path " + sTargetPath);
	    	DfLogger.trace(this, "PoC:: Copy document " + sSourceDocument + " in path " + sSourcePath + " to document " + sTargetDocument + " in path " + sTargetPath, null, null);
	    	oDocument.setObjectName(sTargetDocument);
	    	oDocument.unlink(sSourcePath);
	    	oDocument.link(sTargetPath);
	    	oDocument.save();
	    	System.out.println("PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " copied to document " + sTargetDocument + " in path " + sTargetPath);
	    	DfLogger.info(this, "PoC:: Document " + sSourceDocument + " in path " + sSourcePath + " copied to document " + sTargetDocument + " in path " + sTargetPath, null, null);
	    }
	    return oDocument;
	}
	
	public IDfDocument moveOperation(IDfSession session, String sSourceDocument, String sSourcePath, String sTargetPath) throws Exception {
        IDfSession oSession = session;
        IDfDocument oDocument = (IDfDocument) oSession.getObjectByPath(sSourcePath + "/" + sSourceDocument);
        IDfFolder oSourceFolder = (IDfFolder) oSession.getObjectByPath(sSourcePath);
        IDfFolder oTargetFolder = (IDfFolder) oSession.getObjectByPath(sTargetPath);
        if (oSourceFolder == null || oDocument == null) {
	    	System.out.println("PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath);
	    	DfLogger.warn(this, "PoC:: Document " + sSourceDocument + " not found in path " + sSourcePath, null, null);
		} else if (oTargetFolder == null) {
            System.out.println("Target path " + sSourcePath + " not found");
	    	DfLogger.warn(this, "PoC:: Target path " + sSourcePath + " not found", null, null);
        } else {
        	System.out.println("PoC:: Move object " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath);
        	DfLogger.info(this, "PoC:: Move object " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath, null, null);
        	
        	g_oClientX = new DfClientX();
        	
        	IDfMoveOperation oMoveOperation = g_oClientX.getMoveOperation();
	        oMoveOperation.setSourceFolderId(oSourceFolder.getObjectId());
	    	oMoveOperation.setDestinationFolderId(oTargetFolder.getObjectId());
	    	oMoveOperation.add(oDocument);
	    	if (oMoveOperation.execute()) {
	    		System.out.println("PoC:: Object " + sSourceDocument + " in path " + sSourcePath + " moved to path " + sTargetPath);
	    		DfLogger.info(this, "PoC:: Object " + sSourceDocument + " in path " + sSourcePath + " moved to path " + sTargetPath, null, null);
	    	} else {
	    		IDfList oErrors = oMoveOperation.getErrors();
	            StringBuffer oMessages = new StringBuffer();
	            for(int iError = 0 ; iError < oErrors.getCount(); iError ++) {
	            	IDfOperationError oError = (IDfOperationError) oErrors.get(iError);
	            	oMessages.append(System.getProperty("line.separator"));
	            	oMessages.append(oError.getErrorCode());
	            	oMessages.append(":");
	                oMessages.append(oError.getMessage());
	                oMessages.append(System.getProperty("line.separator"));
		            oMessages.append(oError.getException().getMessage());
	            }
	            String sMessages = oMessages.toString();
				System.out.println("PoC:: Failed to move object " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath + ", Error due to " + sMessages);
				DfLogger.error(this, "PoC:: Failed to move object " + sSourceDocument + " in path " + sSourcePath + " to path " + sTargetPath + ", Error due to " + sMessages, null, null);
	    	}
        }
        return oDocument;
	}
	
    public Vector<String> importFileOrDirectory(IDfSession session, String sFileSystemPath, String sRepositoryPath) throws Exception {
    	if (sRepositoryPath == null || sRepositoryPath.equals("")) sRepositoryPath = DEFAULT_IMPORT_PATH;
    	System.out.println("Start import document " + sFileSystemPath + " from local file system to repository folder " + sRepositoryPath);
    	DfLogger.debug(this, "Start import document " + sFileSystemPath + " from local file system to repository folder " + sRepositoryPath, null, null);
    	
    	IDfClientX oClient = new DfClientX();
        IDfSession oSession = session;
        IDfImportOperation oOperation = oClient.getImportOperation();
        oOperation.setSession(session);
        
        boolean bContinue = true;
        IDfFile oSourceFile = oClient.getFile(sFileSystemPath);
        if (bContinue && !oSourceFile.exists()) {
            System.out.println("File or directory " + sFileSystemPath + " does not exist in the file system");
            DfLogger.warn(this, "PoC:: File or directory " + sFileSystemPath + " does not exist in the file system", null, null);
            bContinue = false;
        }
        IDfFolder oTargetFolder = oSession.getFolderByPath(sRepositoryPath);
        if (bContinue && oTargetFolder == null) {
            System.out.println("Folder or cabinet " + sRepositoryPath + " does not exist in the repository");
    	    DfLogger.warn(this, "PoC:: Folder or cabinet " + sRepositoryPath + " does not exist in the repository", null, null);
    	    bContinue = false;
        }
        Vector<String> oImportedIds = null;
        if (bContinue) {
	        oOperation.setDestinationFolderId(oTargetFolder.getObjectId());
	        oOperation.add(sFileSystemPath);
            System.out.println("Import file or directory " + sFileSystemPath + " into folder or cabinet " + sRepositoryPath);
    	    DfLogger.trace(this, "PoC:: Import file or directory " + sFileSystemPath + " into folder or cabinet " + sRepositoryPath, null, null);            
	        if (executeOperation(oOperation)) {	    	    
	            System.out.println("Operation for importing file or directory " + sFileSystemPath + " into folder or cabinet " + sRepositoryPath + " executed");
	    	    DfLogger.trace(this, "PoC:: Operation for importing file or directory " + sFileSystemPath + " into folder or cabinet " + sRepositoryPath +  " executed", null, null);	    	    
	            IDfList oOperationNodes = oOperation.getNodes();
	            int iOperationNodes  = oOperationNodes.getCount();
	            System.out.println(iOperationNodes + " imported objects into folder or cabinet " + sRepositoryPath);
	            DfLogger.trace(this, "PoC:: " + iOperationNodes + " imported objects into folder or cabinet " + sRepositoryPath, null, null);
	            for (int i = 0; i < iOperationNodes; i ++) {
	                IDfImportNode oNode = (IDfImportNode) oOperationNodes.get(i);
	                String sObjectId = oNode.getNewObjectId().toString();
	                if (oImportedIds == null) oImportedIds = new Vector<String>();	                
	                oImportedIds.add(sObjectId);
	                System.out.println("Imported object with Id " + sObjectId);
	                DfLogger.trace(this, "PoC:: Imported object with Id " + sObjectId, null, null);
	            }
	        }
        }
        if (oImportedIds == null) {
            System.out.println("File or directory " + sFileSystemPath + " not imported into folder or cabinet " + sRepositoryPath);
    	    DfLogger.error(this, "PoC:: File or directory " + sFileSystemPath + " not imported into folder or cabinet " + sRepositoryPath, null, null);
        } else {
            System.out.println("File or directory " + sFileSystemPath + " imported into folder or cabinet " + sRepositoryPath);
    	    DfLogger.trace(this, "PoC:: File or directory " + sFileSystemPath + " imported into folder or cabinet " + sRepositoryPath, null, null);
        }
        return oImportedIds;
    }

    private boolean executeOperation(IDfOperation oOperation) throws Exception {
    	boolean bExecute = oOperation.execute();
        if (!bExecute) {
            IDfList oErrors = oOperation.getErrors();
            StringBuffer oMessages = new StringBuffer();
            for(int iError = 0 ; iError < oErrors.getCount() ; iError ++) {
            	IDfOperationError oError = (IDfOperationError) oErrors.get(iError);
            	oMessages.append(System.getProperty("line.separator"));
            	oMessages.append(oError.getErrorCode());
            	oMessages.append(":");
                oMessages.append(oError.getMessage());
                oMessages.append(System.getProperty("line.separator"));
                oMessages.append(oError.getException().getMessage());
            }
            String sMessages = oMessages.toString();
            System.out.println("Error executing the operation, Error due to " + sMessages);
            DfLogger.error(this, "PoC:: Error executing the operation, , Error due to " + sMessages, null, null);
        }
    	return bExecute;
    }
    
    public void cloneFolderContents(IDfSession oSession, String sSourcePath, String sTargetPath, boolean bCopyOrLink) throws Exception {
    	IDfFolder oSourceFolder = oSession.getFolderByPath(sSourcePath);
    	IDfFolder oTargetFolder = oSession.getFolderByPath(sSourcePath);
	    if (oSourceFolder == null) {
	    	System.out.println("Path " + sSourcePath + " not found");
	    	DfLogger.warn(this, "PoC:: Path " + sSourcePath + " not found", null, null);
	    } else if (oTargetFolder == null) {
            System.out.println("Target path " + sTargetPath + " not found");
	    	DfLogger.warn(this, "PoC:: Target path " + sTargetPath + " not found", null, null);
	    } else {
            System.out.println("Scan path " + sSourcePath);
	    	DfLogger.debug(this, "PoC:: Scan source path " + sSourcePath, null, null);	    	
			IDfCollection oFolderSubs = oSourceFolder.getContents("object_name");
			int iFolders = 0;
			int iDocuments = 0;
			while (oFolderSubs.next()) {
				String sObjectName = oFolderSubs.getString("object_name");
				IDfSysObject oObject = (IDfSysObject) oSession.getObjectByPath(sSourcePath + "/" + sObjectName);
				String sObjectType = oObject.getTypeName();
				if (oObject.isInstanceOf("dm_folder")) {
					IDfFolder oNewFolder = oSession.getFolderByPath(sTargetPath + "/" + sObjectName);
					if (oNewFolder == null) {
						createFolder(oSession, sTargetPath, sObjectName, sObjectType);
					} else {
			            System.out.println("Folder " + sObjectName + " already exists in path " + sTargetPath);
				    	DfLogger.warn(this, "PoC:: Folder " + sObjectName + " already exists in path " + sTargetPath, null, null);	    	
					}
					cloneFolderContents(oSession, sSourcePath + "/" + sObjectName, sTargetPath + "/" + sObjectName, bCopyOrLink);
					iFolders ++;
				} else if (oObject.isInstanceOf("dm_document")) {
					IDfSysObject oNewDocument = (IDfSysObject) oSession.getObjectByPath(sTargetPath + "/" + sObjectName);
					if (oNewDocument == null) {
						if (bCopyOrLink) {
							copyDocument(oSession, sSourcePath, sObjectName, sTargetPath, sObjectName);
						} else {
							linkDocument(oSession, sSourcePath, sObjectName, sTargetPath);
						}
					} else {
			            System.out.println("Document " + sObjectName + " already exists in path " + sTargetPath + ", it will be unchanged");
				    	DfLogger.warn(this, "PoC:: Document " + sObjectName + " already exists in path " + sTargetPath + ", it will be unchanged", null, null);	    	
					}
					iDocuments ++;
				}
			}
            System.out.println("Cloned " + iFolders + " folder(s) and " + iDocuments + " document(s) from source path " + sSourcePath + " to target path " + sTargetPath);
	    	DfLogger.info(this, "PoC:: Cloned " + iFolders + " folder(s) and " + iDocuments + " document(s) from source path " + sSourcePath + " to target path " + sTargetPath, null, null);
	    }
    }
    
    public void applyFolderPermissionSet(IDfSession oSession, String sSourcePath, IDfId oPermissionSetId, boolean bRecursively) throws Exception {
    	IDfFolder oSourceFolder = oSession.getFolderByPath(sSourcePath);
		System.out.println("Getting permission set of Id " + oPermissionSetId.toString());
		DfLogger.trace(this, "PoC:: Getting permission set of Id " + oPermissionSetId.toString(), null, null);
		IDfACL oAcl = (IDfACL) oSession.getObject(oPermissionSetId);
	    if (oSourceFolder == null) {
	    	System.out.println("Path " + sSourcePath + " not found");
	    	DfLogger.warn(this, "PoC:: Path " + sSourcePath + " not found", null, null);
	    } else if (oAcl == null) {
	    	System.out.println("Permission set of Id " + oPermissionSetId.toString() + " not found");
	    	DfLogger.warn(this, "PoC:: Permission set of Id " + oPermissionSetId.toString() + " not found", null, null);
	    } else {
	    	String sPermissionSet = oAcl.getObjectName();
            System.out.println("Scan path " + sSourcePath + " to set permission " + sPermissionSet);
	    	DfLogger.debug(this, "PoC:: Scan source path " + sSourcePath + " to set permission " + sPermissionSet, null, null);	    	
			IDfCollection oFolderSubs = oSourceFolder.getContents("object_name");
			int iObjects = 0;
			while (oFolderSubs.next()) {
				String sObjectPath = sSourcePath + "/" + oFolderSubs.getString("object_name");
				IDfSysObject oObject = (IDfSysObject) oSession.getObjectByPath(sObjectPath);
                System.out.println("Apply permission set " + sPermissionSet + " to child object " + sObjectPath);
                DfLogger.trace(this, "PoC:: Apply permission set " + sPermissionSet + " to child object " + sObjectPath, null, null);
                oObject.setACL(oAcl);
                oObject.save();
                System.out.println("Permission set " + sPermissionSet + " applied to child object " + sObjectPath);
                DfLogger.trace(this, "PoC:: Permission set " + sPermissionSet + " applied to child object " + sObjectPath, null, null);
                if (bRecursively) applyFolderPermissionSet(oSession, sObjectPath, oPermissionSetId, bRecursively);
				iObjects ++;
			}
            System.out.println("Permission set " + sPermissionSet + " applied to " + iObjects + " object(s) in " + sSourcePath);
	    	DfLogger.info(this, "PoC:: Permission set " + sPermissionSet + " Applied to " + iObjects + " object(s) in " + sSourcePath, null, null);
	    }
    }

    /*public static void main(String[] args) throws Exception {
    	
		RepositoryConnection oDocumentumConnection = new RepositoryConnection();
		try {		
			oDocumentumConnection.beginTransaction();    
	    	IDfSession oSession = oDocumentumConnection.getSession();
	    	
	    	IRepositoryFileManager oRepositoryFileManager = new RepositoryFileManager();
	    	
	    	int iAction = 5;
	    	if (iAction == 1) {
	    		
	    		String sXmlDocumentPath = "/Test/Workflows/Test Processing Instructions Workflow";
	    		String sXmlDocumentName = "Simple Document";
	    	
        		StringBuffer oXmlDocument = new StringBuffer();
        		oXmlDocument.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        		oXmlDocument.append(System.getProperty("line.separator"));
        		oXmlDocument.append("<CSV>");
        		oXmlDocument.append(System.getProperty("line.separator"));
        		oXmlDocument.append("\t<Filename>" + sXmlDocumentName + "</Filename>");
        		oXmlDocument.append(System.getProperty("line.separator"));
        		oXmlDocument.append("\t<Filepath>" + sXmlDocumentPath + "</Filepath>");
        		oXmlDocument.append(System.getProperty("line.separator"));
        		oXmlDocument.append("\t<Content>");
        		oXmlDocument.append(System.getProperty("line.separator"));

				oXmlDocument.append("\t\t<Columns>");
				oXmlDocument.append(System.getProperty("line.separator"));
				
				oXmlDocument.append("\t\t</Columns>");
				oXmlDocument.append(System.getProperty("line.separator"));
				oXmlDocument.append("\t\t<Rows>");
				oXmlDocument.append(System.getProperty("line.separator"));
				
				oXmlDocument.append("\t\t</Rows>");
				oXmlDocument.append(System.getProperty("line.separator"));
				oXmlDocument.append("\t</Content>");
				oXmlDocument.append(System.getProperty("line.separator"));
				oXmlDocument.append("</CSV>");
				oXmlDocument.append(System.getProperty("line.separator"));
				String sXmlContent = oXmlDocument.toString();
				
				System.out.println("XML content of document " + sXmlDocumentName + " is:" + System.getProperty("line.separator") + sXmlContent);
				System.out.println("Save XML document " + sXmlDocumentName + " to path " + sXmlDocumentPath);
				
				oRepositoryFileManager.createDocument(oSession, 
	    									sXmlDocumentPath,
	    									sXmlDocumentName,
	    									"dm_document",
	    									"xml",
	    									sXmlContent.getBytes());
	    	} 

	    	if (iAction == 2) {
	    		
	    		IDfDocument oSourceDocument = (IDfDocument) oSession.getObjectByPath("/MT/_Common/Resources/Empty Document");
	    		byte[] aSourceContent = oRepositoryFileManager.readBinaryDocument(oSourceDocument);
	    		String sSourceContentType = oSourceDocument.getContentType();
	    		
	    		String sTargetPath = "/MT/Project/Job Documents";
	    		String sTargetDocument = "3291-CD-DX-3201_001";
	    		// dm_document msw8
	    		IDfDocument oDocument = oRepositoryFileManager.createDocument(oSession, 
																	sTargetPath,
																	sTargetDocument,
																	"job_document",
																	sSourceContentType,
																	aSourceContent);
	    		HashMap<String, String> oAttributes = new HashMap<String, String>();
	    		// oAttributes.put("document_number", "0000_0102");
	    		// oAttributes.put("planning_group", "FALSE");
	    		// oAttributes.put("issue_date", "01/01/2009 12:15:30");
	    		// oAttributes.put("acl_domain", "dm_452098d380000101"); If ACL value does not exists, this attribute setting generate an error
	    		
	    		oRepositoryFileManager.setObjectTypedAttributes(oDocument, oAttributes, null);
	    	}
	    	
	    	if (iAction == 3) {
		    	
	    		// String sFileSystemPath = "C://Barcodes//Barcodes-4d01dd4f80003905.doc";
	    		String sFileSystemPath = "C://_PRJ//Resources//Import//MT//Deliverables//3291-XH-MR-110-IS 05.pdf";
	    		IDfClient client = new DfClient()
		    	Vector<String> oImported = oRepositoryFileManager.importFileOrDirectory client, sFileSystemPath, DEFAULT_IMPORT_PATH);  	    	
		    	for (int iImported = 0; iImported < oImported.size(); iImported ++) {			
		    		System.out.println("Imported object with Id " + oImported.get(iImported));			
		    	}
	    	}
	    	
	    	if (iAction == 4) {
	    		
	    		String sSourcePath = "/MT/Project/01-WBS/Borouge 2 Project/Home Office Services/Engineering and Design/Electrical Eng./PE 3/Area General";
	    		String sTargetPath = "/MT/Project/02-PBS";
	    		oRepositoryFileManager.cloneFolderContents(oSession, sSourcePath, sTargetPath, true);
	    		
	    	}

	    	if (iAction == 5) {
	    		
	    		String sSourcePath = "/MT";
	    		IDfId oPermissionId = new DfId("452098d38001952f");
	    		oRepositoryFileManager.applyFolderPermissionSet(oSession, sSourcePath, oPermissionId, true);
	    	
	    	}
	    	
	    	oDocumentumConnection.commitTransaction();
	    } catch (Exception oException) {
	    	oDocumentumConnection.abortTransaction();
	    	System.out.println("Error due to " + oException.getMessage());
	    	oException.printStackTrace();
	    } finally {	    	
	    	oDocumentumConnection.release();
	    }
	}*/
        
    private static IRepositoryFileManager _oRepositoryFileManager = null;
    
    private static final String[] SYSTEM_ATTRIBUTES = { "acl_domain", "object_type"};
	public static String DEFAULT_IMPORT_PATH = "/dmadmin";
	private static DfClientX g_oClientX = null;
	
}
