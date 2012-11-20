package com.emc.documentum.extensions.util;

import com.documentum.com.*;
import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

public class RepositoryConnection {

	public RepositoryConnection() throws Exception {
		this(DOCUMENTUM_REPOSITORY, DOCUMENTUM_ADMINISTRATOR, DOCUMENTUM_PASSWORD);
	}
	
	public RepositoryConnection(String sDocbase, String sUsername, String sPassword) throws Exception {
		if (g_oSessionManager == null) {
			System.out.println("Connect to '" + sDocbase + "' Process Engine server as '" + sUsername + "' user");
			DfLogger.trace(this, "PoC:: Connect to '" + sDocbase + "' Process Engine server as '" + sUsername + "' user", null, null);
			g_oClientX = new DfClientX();
			IDfLoginInfo oLoginInfo = g_oClientX.getLoginInfo();
			oLoginInfo.setUser(sUsername);
			oLoginInfo.setPassword(sPassword);
			IDfClient oClient = g_oClientX.getLocalClient();
			g_oSessionManager = oClient.newSessionManager();
			g_oSessionManager.setIdentity(sDocbase, oLoginInfo);
		} else {
			System.out.println("Connection to '" + sDocbase + "' Process Engine server as '" + sUsername + "' user already created");
			DfLogger.trace(this, "PoC:: Connection to '" + sDocbase + "' Process Engine server as '" + sUsername + "' user already created", null, null);
		}
		System.out.println("Create a session");
		DfLogger.trace(this, "PoC:: Create a session", null, null);
		_oSession = g_oSessionManager.newSession(sDocbase);
	}

	public DfClientX getClient() {
		return g_oClientX;
	}
	
	public IDfSession getSession() {
		return _oSession;
	}
	
	public void beginTransaction() throws Exception {
		System.out.println("Begin session transaction");
		DfLogger.trace(this, "PoC:: Begin session transaction", null, null);
		_oSession.beginTrans();
	}

	public void commitTransaction() throws Exception {
		if (_oSession != null && _oSession.isTransactionActive()) {
    		System.out.println("Commit session transaction");
    		DfLogger.trace(this, "PoC:: Commit session transaction", null, null);
    		_oSession.commitTrans();
		}
	}

	public void abortTransaction() throws Exception {
		if (_oSession != null && _oSession.isTransactionActive()) {
    	  System.out.println("Abort session transaction");
    	  DfLogger.trace(this, "PoC:: Abort session transaction", null, null);
    	  _oSession.abortTrans();
		}
	}
	
	public void release() throws Exception {
   		if (_oSession != null) {
	    	System.out.println("Disconnect from '" + DOCUMENTUM_REPOSITORY + "' Process Engine server");
	    	DfLogger.trace(this, "PoC:: Disconnect from '" + DOCUMENTUM_REPOSITORY + "' Process Engine server", null, null);
	    	g_oSessionManager.release(_oSession);
	    }
	}

	
	public static void main(String[] args) throws Exception {
		RepositoryConnection oDocumentumConnection = new RepositoryConnection();
		try {		
			oDocumentumConnection.beginTransaction();     
			
	        System.out.println("Do something");
	        
			oDocumentumConnection.commitTransaction();
	    } catch (Exception oException) {
	    	oDocumentumConnection.abortTransaction();
	    	System.out.println("Error due to " + oException.getMessage());
	    } finally {				    	
	    	oDocumentumConnection.release();
	    }
	}
	
	
	private static IDfSessionManager g_oSessionManager = null;
	private static DfClientX g_oClientX = null;
	private IDfSession _oSession = null; 
	   	
	public static String DOCUMENTUM_REPOSITORY = "express";
	public static String DOCUMENTUM_ADMINISTRATOR = "dmadmin";
	public static String DOCUMENTUM_PASSWORD = "dmadmin";

}
