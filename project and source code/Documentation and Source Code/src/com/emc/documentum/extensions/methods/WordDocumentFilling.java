package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.microsoft.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;
import java.util.*;

public class WordDocumentFilling extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String DOCUMENT_PATH, String DOCUMENT_NAME, String PACKAGE_NAME, String BOOKMARK_NAME_1, String BOOKMARK_NAME_2, String BOOKMARK_NAME_3, String BOOKMARK_NAME_4, String BOOKMARK_NAME_5, String BOOKMARK_VALUE_1, String BOOKMARK_VALUE_2, String BOOKMARK_VALUE_3, String BOOKMARK_VALUE_4, String BOOKMARK_VALUE_5, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(DOCUMENT_PATH == null)
		{
			DOCUMENT_PATH = "";
		}
		if(DOCUMENT_NAME == null)
		{
			DOCUMENT_NAME = "";
		}
		if(BOOKMARK_NAME_1 == null)
		{
			BOOKMARK_NAME_1 = "";
		}
		if(BOOKMARK_NAME_2 == null)
		{
			BOOKMARK_NAME_2 = "";
		}
		if(BOOKMARK_NAME_3 == null)
		{
			BOOKMARK_NAME_3 = "";
		}
		if(BOOKMARK_NAME_4 == null)
		{
			BOOKMARK_NAME_4 = "";
		}
		if(BOOKMARK_NAME_5 == null)
		{
			BOOKMARK_NAME_5 = "";
		}
		if(BOOKMARK_VALUE_1 == null)
		{
			BOOKMARK_VALUE_1 = "";
		}
		if(BOOKMARK_VALUE_2 == null)
		{
			BOOKMARK_VALUE_2 = "";
		}
		if(BOOKMARK_VALUE_3 == null)
		{
			BOOKMARK_VALUE_3 = "";
		}
		if(BOOKMARK_VALUE_4 == null)
		{
			BOOKMARK_VALUE_4 = "";
		}
		if(BOOKMARK_VALUE_5 == null)
		{
			BOOKMARK_VALUE_5 = "";
		}
		if(PACKAGE_NAME == null)
		{
			PACKAGE_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("TEMPLATE_DOCUMENT_NAME", DOCUMENT_PATH);
		oParameters.putString("GENERATED_DOCUMENT_NAME", DOCUMENT_NAME);
		oParameters.putString("BOOKMARK_NAME_1", BOOKMARK_NAME_1);
		oParameters.putString("BOOKMARK_NAME_2", BOOKMARK_NAME_2);
		oParameters.putString("BOOKMARK_NAME_3", BOOKMARK_NAME_3);
		oParameters.putString("BOOKMARK_NAME_4", BOOKMARK_NAME_4);
		oParameters.putString("BOOKMARK_NAME_5", BOOKMARK_NAME_5);
		oParameters.putString("BOOKMARK_VALUE_1", BOOKMARK_VALUE_1);
		oParameters.putString("BOOKMARK_VALUE_2", BOOKMARK_VALUE_2);
		oParameters.putString("BOOKMARK_VALUE_3", BOOKMARK_VALUE_3);
		oParameters.putString("BOOKMARK_VALUE_4", BOOKMARK_VALUE_4);
		oParameters.putString("BOOKMARK_VALUE_5", BOOKMARK_VALUE_5);
		oParameters.putString("PACKAGE_NAME", PACKAGE_NAME);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {
		
		setClass("WordDocumentFilling");
		
		String sTemplateFilename = getWorkitemStringParameter(oParameters, "TEMPLATE_DOCUMENT_NAME"); // Ex: "MortgageBarcodes.doc"
		String sGeneratedPrefix = getWorkitemStringParameter(oParameters, "GENERATED_DOCUMENT_NAME"); // Ex: "Barcodes-"
		String sBoomarkName1 = getWorkitemStringParameter(oParameters, "BOOKMARK_NAME_1");
		String sBoomarkName2 = getWorkitemStringParameter(oParameters, "BOOKMARK_NAME_2");
		String sBoomarkName3 = getWorkitemStringParameter(oParameters, "BOOKMARK_NAME_3");
		String sBoomarkName4 = getWorkitemStringParameter(oParameters, "BOOKMARK_NAME_4");
		String sBoomarkName5 = getWorkitemStringParameter(oParameters, "BOOKMARK_NAME_5");
		String sBoomarkValue1 = getWorkitemStringParameter(oParameters, "BOOKMARK_VALUE_1");
		String sBoomarkValue2 = getWorkitemStringParameter(oParameters, "BOOKMARK_VALUE_2");
		String sBoomarkValue3 = getWorkitemStringParameter(oParameters, "BOOKMARK_VALUE_3");
		String sBoomarkValue4 = getWorkitemStringParameter(oParameters, "BOOKMARK_VALUE_4");
		String sBoomarkValue5 = getWorkitemStringParameter(oParameters, "BOOKMARK_VALUE_5");
		String sPackageName = getWorkitemStringParameter(oParameters, "PACKAGE_NAME");
		
		if (isNull(sTemplateFilename) || isNull(sGeneratedPrefix)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			BatchWordProcessing oWord = new BatchWordProcessing(FILESYSTEM_IMPORT_PATH + "WordProcessing.txt", FILESYSTEM_IMPORT_PATH + "WordProcessing.log");

        	String sTemplateFilepath = FILESYSTEM_IMPORT_PATH + sTemplateFilename;
        	String sGeneratedFilepath = FILESYSTEM_IMPORT_PATH + sGeneratedPrefix + oWorkitem.getWorkflowId().toString() + ".doc";
        	
        	writeLog(LOG_DEBUG, "Create new Word document " + sGeneratedFilepath + " from Word template " + sTemplateFilepath + " on local file system");
        	oWord.createNewDocumentFromTemplate(sTemplateFilepath);
        	
        	writeLog(LOG_DEBUG, "Set bookmark " + sBoomarkName1 + " with value " + sBoomarkValue1 + " into Word document " + sTemplateFilepath + " on local file system");
        	oWord.typeTextAtBookmark(sBoomarkName1, sBoomarkValue1);			            	
        	writeLog(LOG_DEBUG, "Set bookmark " + sBoomarkName2 + " with value " + sBoomarkValue2 + " into Word document " + sTemplateFilepath + " on local file system");
        	oWord.typeTextAtBookmark(sBoomarkName2, sBoomarkValue2);			            	
        	writeLog(LOG_DEBUG, "Set bookmark " + sBoomarkName3 + " with value " + sBoomarkValue3 + " into Word document " + sTemplateFilepath + " on local file system");
        	oWord.typeTextAtBookmark(sBoomarkName3, sBoomarkValue3);			            	
        	writeLog(LOG_DEBUG, "Set bookmark " + sBoomarkName4 + " with value " + sBoomarkValue4 + " into Word document " + sTemplateFilepath + " on local file system");
        	oWord.typeTextAtBookmark(sBoomarkName4, sBoomarkValue4);			            	
        	writeLog(LOG_DEBUG, "Set bookmark " + sBoomarkName5 + " with value " + sBoomarkValue5 + " into Word document " + sTemplateFilepath + " on local file system");
        	oWord.typeTextAtBookmark(sBoomarkName5, sBoomarkValue5);			            	
        	 	        	
        	oWord.saveDocumentAsAndClose(sGeneratedFilepath);
        	writeLog(LOG_INFO, "Save new Word document " + sGeneratedFilepath + " on local file system");
        	oWord.quitApplicationAfterWaiting(IRepositoryFileManager.FILESYSTEM_IMPORT_WAIT);
        	oWord.exec();
        	
        	Thread.sleep(IRepositoryFileManager.FILESYSTEM_IMPORT_WAIT);

        	IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
        	Vector<String> oImported = oRepositoryFileManager.importFileOrDirectory(getSession(), sGeneratedFilepath, null);
        	            	
        	if (!isNull(sPackageName) && !isNull(oImported)) {
        		
        		
            	IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPackageName);
            	String sImportedPath = RepositoryFileManager.DEFAULT_IMPORT_PATH + "/" + sGeneratedPrefix + oWorkitem.getWorkflowId().toString();
            	addDocumentToPackage(getSession(), oWorkitem, oPackage, sImportedPath);
        	}
			
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}	
	
	private static final String FILESYSTEM_IMPORT_PATH = "C://_PRJ//Resources//SWord//";

}