package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;
import com.emc.documentum.extensions.util.fop.*;

import java.io.*;
import java.util.*;

public class FopXmlToPdfTransformation extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String XML_PACKAGE_NAME, String PDF_DOCUMENT_PATH, String PDF_DOCUMENT_NAME, String PDF_PACKAGE_NAME, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(XML_PACKAGE_NAME == null)
		{
			XML_PACKAGE_NAME = "";
		}
		if(PDF_DOCUMENT_PATH == null)
		{
			PDF_DOCUMENT_PATH = "";
		}
		if(PDF_DOCUMENT_NAME == null)
		{
			PDF_DOCUMENT_NAME = "";
		}
		if(PDF_PACKAGE_NAME == null)
		{
			PDF_PACKAGE_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("XML_PACKAGE_NAME", XML_PACKAGE_NAME);
		oParameters.putString("PDF_DOCUMENT_PATH", PDF_DOCUMENT_PATH);
		oParameters.putString("PDF_DOCUMENT_NAME", PDF_DOCUMENT_NAME);
		oParameters.putString("PDF_PACKAGE_NAME", PDF_PACKAGE_NAME);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("FopPdfGeneration");
		
		String sXmlPackageName = getWorkitemStringParameter(oParameters, "XML_PACKAGE_NAME");
		String sPdfDocumentPath = getWorkitemStringParameter(oParameters, "PDF_DOCUMENT_PATH");
		String sPdfDocumentName = getWorkitemStringParameter(oParameters, "PDF_DOCUMENT_NAME");
		String sPdfPackageName = getWorkitemStringParameter(oParameters, "PDF_PACKAGE_NAME");
		
		if (isNull(sXmlPackageName) || isNull(sPdfDocumentPath) || isNull(sPdfDocumentName)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}

		try {		
			IDfSession oSession = getSession();

			String sPdfFileSystemPath = FopGenerator.FILESYSTEM_IMPORT_PATH + sPdfDocumentName + ".pdf";
			IDfPackage oXmlPackage = getWorkitemPackage(oWorkitem, sXmlPackageName);
        	IDfDocument oXmlDocument = getPackageDocument(oSession, oXmlPackage, 0, null);
        	IDfFolder oPdfFolder = (IDfFolder) oSession.getObjectByPath(sPdfDocumentPath);
        	if (foundObject(oXmlDocument) && foundObject(oPdfFolder)) {
        		IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
    			byte[] aXmlDocument = oRepositoryFileManager.readBinaryDocument(oXmlDocument);
        		FopGenerator oFopGenerator = new FopGenerator();
        		oFopGenerator.generatePdf(aXmlDocument, sPdfFileSystemPath);
        	}
        	
        	Thread.sleep(IRepositoryFileManager.FILESYSTEM_IMPORT_WAIT);

        	IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
        	Vector<String> oImported = oRepositoryFileManager.importFileOrDirectory(getSession(), sPdfFileSystemPath, sPdfDocumentPath);
        	
        	String sPdfDocument = sPdfDocumentPath + "/" + sPdfDocumentName;
        	if (!isNull(sPdfPackageName) && !isNull(oImported)) {
        		        		
            	IDfPackage oPackage = getWorkitemPackage(oWorkitem, sPdfPackageName);
            	addDocumentToPackage(getSession(), oWorkitem, oPackage, sPdfDocument);
        	}

	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}
	
}