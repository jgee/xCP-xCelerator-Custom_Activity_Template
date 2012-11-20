package com.emc.documentum.extensions.util;

import java.util.*;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

public interface IRepositoryFileManager {

	public String[] readPlainDocument(IDfDocument oDocument, boolean bSkipEmptyLines, boolean bSkipEmptyTrimmedLines, boolean bTrimLines) throws Exception;
	public byte[] readBinaryDocument(IDfDocument oDocument) throws Exception;
	public IDfId createPropertiesFile(IDfSession oSession, String sFilepath, String sFilename, Object[] aProperties, Object[] aValues) throws Exception;
	public IDfDocument createDocument(IDfSession oSession, String sDocumentPath, String sDocumentName, String sDocumentType, String sContentType, byte aContent[]) throws Exception;
	public IDfFolder createFolder(IDfSession oSession, String sFolderPath, String sFolderName, String sFolderType) throws Exception;
	public int setObjectTypedAttributes(IDfSysObject oObject, Map<String, String> oAttributes, String sDatetimeFormat) throws Exception;
	public int setObjectFoundAttributes(IDfSysObject oObject, Map<String, Object> oAttributes) throws Exception;
	public boolean isObjectSystemAttribute(String sAttribute);
	public IDfDocument copyDocument(IDfSession oSession, String sSourcePath, String sSourceDocument, String sTargetPath, String sTargetDocument) throws Exception;
	public IDfDocument moveDocument(IDfSession oSession, String sSourcePath, String sSourceDocument, String sTargetPath, String sTargetDocument) throws Exception;
	public IDfDocument moveOperation(IDfSession oSession, String sSourceDocument, String sSourcePath, String sTargetPath) throws Exception;
	public Vector<String> importFileOrDirectory(IDfSession session, String sFileSystemPath, String sRepositoryPath) throws Exception;
	public void cloneFolderContents(IDfSession oSession, String sSourcePath, String sTargetPath, boolean bCopyOrLink) throws Exception;
	public void applyFolderPermissionSet(IDfSession oSession, String sSourcePath, IDfId oPermissionSetId, boolean bRecursively) throws Exception;
	    
	public static final int FILESYSTEM_IMPORT_WAIT = 20000;
	
}
