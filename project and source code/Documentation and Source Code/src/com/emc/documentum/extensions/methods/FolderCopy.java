package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class FolderCopy extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String SOURCE_PATH, String TARGET_PATH, boolean LINK, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(SOURCE_PATH == null)
		{
			SOURCE_PATH = "";
		}
		if(TARGET_PATH == null)
		{
			TARGET_PATH = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("SOURCE_PATH", SOURCE_PATH);
		oParameters.putString("TARGET_PATH", TARGET_PATH);
		oParameters.putBoolean("LINK", LINK);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {
		
		setClass("FolderCopy");
		
		String sSourcePath = getWorkitemStringParameter(oParameters, "SOURCE_PATH");
		String sTargetPath = getWorkitemStringParameter(oParameters, "TARGET_PATH");
		boolean bCopyOrLink = getWorkitemBooleanParameter(oParameters, "COPY_OR_LINK");
		
		if (isNull(sSourcePath) || isNull(sTargetPath)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		}
		
		try {		
			IDfSession oSession = getSession();				
			IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
	        oRepositoryFileManager.cloneFolderContents(oSession, sSourcePath, sTargetPath, bCopyOrLink);

	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}
	
}