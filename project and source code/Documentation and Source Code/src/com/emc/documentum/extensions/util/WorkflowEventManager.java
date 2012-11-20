package com.emc.documentum.extensions.util;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

import java.util.*;

public class WorkflowEventManager {
	
	public String startRunnableWorkflow(RepositoryConnection oDocumentumConnection, String sWorkflowName) throws Exception {        
        return startRunnableWorkflow(oDocumentumConnection, sWorkflowName, null, null, null, null, null, null);
	}

	public String startRunnableWorkflow(RepositoryConnection oDocumentumConnection, String sWorkflowName, String sPackageName, Map<String, Object> oAttributes, String sDocumentName, String sDocumentType, String sContentType, byte aContent[]) throws Exception {
		IDfSession oSession = oDocumentumConnection.getSession();		

		IDfId oProcessId = null;		
		if (sWorkflowName != null && !sWorkflowName.equals("")) {
            System.out.println("Search for running workflow");
            DfLogger.trace(this, "PoC:: Search for running workflow scanning", null, null);
			IDfCollection oProcesses = oSession.getRunnableProcesses(null);    
	        int iWorkflows = 0;
	        try {	        	
	        	while (oProcesses.next()) {
	            	String sWorkflow = oProcesses.getString("object_name");
	            	System.out.println("\tFound runnable workflow '" + sWorkflow + "'");
	            	DfLogger.trace(this, "PoC:: \tFound runnable workflow '" + sWorkflow + "'", null, null);
	                if (sWorkflow.equals(sWorkflowName)) oProcessId = oProcesses.getId("r_object_id");
	                iWorkflows ++;
	            }
	        } finally {
	            if (oProcesses != null) oProcesses.close();
	        }
	        System.out.println(iWorkflows + " runnable workflows found");
	        DfLogger.trace(this, "PoC:: " + iWorkflows + " runnable workflows found", null, null);
		}
        if (oProcessId == null) {
            System.out.println("Workflow '" + sWorkflowName + "' not found");
            DfLogger.warn(this, "PoC:: Workflow '" + sWorkflowName + "' not found", null, null);
            return null;
        }  	

        // PROCESS FOUND!
        
        System.out.println("Workflow '" + sWorkflowName + "' is valid and runnable, process Id is " + oProcessId);
        DfLogger.trace(this, "PoC:: Workflow '" + sWorkflowName + "' is valid and runnable, process Id is " + oProcessId, null, null);		                        	
        
        if (sPackageName == null || sPackageName.equals("")) {
	        System.out.println("Packages information not specified, cannot start '" + sWorkflowName + "' workflow");
	        DfLogger.warn(this, "PoC:: Packages information not specified, cannot start '" + sWorkflowName + "' workflow", null, null);        	
        	return null;
        }
        
        // PACKAGE INFO SPECIFIED!
        
    	System.out.println("Search workflow '" + sWorkflowName + "' start activities");
        DfLogger.trace(this, "PoC:: Search workflow '" + sWorkflowName + "' start activities", null, null);
    	IDfProcess oProcess = (IDfProcess) oSession.getObject(oProcessId);
    	IDfList oStartActivities = oProcess.getStartActivities(null);
    	int iActivity = oStartActivities.getCount();
        System.out.println(iActivity + " start activity(ies) found for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: " + iActivity + " start activity(ies) found for workflow '" + sWorkflowName + "'", null, null);	        
        if (iActivity > 1) {
	        System.out.println("Many start activities found, cannot start '" + sWorkflowName + "' workflow");
	        DfLogger.warn(this, "PoC:: Many start activities found, cannot start '" + sWorkflowName + "' workflow", null, null);
	        return null;
        } 
        
        // ONLY ONE START ACTIVITY!

        String sStartActivity = oStartActivities.getString(0);
    	IDfId oStartActivityId = oProcess.getActivityIdByName(sStartActivity);
    	IDfActivity oStartActivity = (IDfActivity) oSession.getObject(oStartActivityId);		        	
        System.out.println("Use first start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Use first start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);
    	
        // START ACTIVITY FOUND!

        String sPackageType = null;
        IDfList oPackageIds = null;
        HashMap<String, String> oOtherPackages = null;

        String sPackagePort = DEFAULT_PACKAGE_INPUT_PORT;
        System.out.println("Search packages on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Search packages on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);
        int iFound = 0;
    	for (int iPackage = 0; iPackage < oStartActivity.getPackageCount(); iPackage ++) {
    		if (oStartActivity.getPortType(iPackage).equals(IDfActivity.PORT_TYPE_INPUT) && oStartActivity.getPortName(iPackage).equals(sPackagePort)) {
    			String sPackage = oStartActivity.getPackageName(iPackage);
    			String sPackageFlag = ACTIVITY_FLAG[oStartActivity.getPackageFlag(iPackage)];
		        System.out.println("\tFound package '" + sPackage + "' " + sPackageFlag + " on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
		        DfLogger.trace(this, "PoC:: \tFound package '" + sPackage + "' " + sPackageFlag + " on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);
	        	if (sPackage.equalsIgnoreCase(sPackageName)) {
	        		sPackageType = oStartActivity.getPackageType(iPackage);
	        	} else {
	        		if (oOtherPackages == null) oOtherPackages = new HashMap<String, String>();
	        		oOtherPackages.put(sPackage, oStartActivity.getPackageType(iPackage));
	        	}
	        	iFound ++;
    		}
    	}
        System.out.println(iFound + " valid packages found on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: " + iFound + " valid packages found on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);		    	
	
    	if (sPackageType == null || sPackageType.equals("")) {
	        System.out.println("Specified package '" + sPackageName + "' of start activity '" + sStartActivity + "' not found, cannot start '" + sWorkflowName + "' workflow with it");
	        DfLogger.warn(this, "PoC:: Specified package '" + sPackageName + "' of start activity '" + sStartActivity + "' not found, cannot start '" + sWorkflowName + "' workflow with it", null, null);
	        return null;
        }
    	
    	// PACKAGE FOUND, OTHER PACKAGES RETRIEVED!
    	
    	System.out.println("Found specified package '" + sPackageName + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity+ "' for workflow '" + sWorkflowName + "'");
    	DfLogger.trace(this, "PoC:: Found specified package '" + sPackageName + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);

    	IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
    	System.out.println("Create document of type '" + sDocumentType + "' for package '" + sPackageName + "' of type '" + sPackageType + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Create document of type '" + sDocumentType + "' for package '" + sPackageName + "' of type '" + sPackageType + "' for workflow '" + sWorkflowName + "'", null, null);
        // IDfId oObejctId 
        IDfDocument oDocument = oRepositoryFileManager.createDocument(oSession, DEFAULT_PACKAGES_PATH, sDocumentName, sDocumentType, sContentType, aContent);
        oRepositoryFileManager.setObjectFoundAttributes(oDocument , oAttributes);
        oPackageIds = oDocumentumConnection.getClient().getList();
        oPackageIds.setElementType(IDfList.DF_ID);
        oPackageIds.appendId(oDocument.getObjectId());
        
        // PACKAGE DOCUMENT CREATED!
        
    	System.out.println("Start runnable workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Start runnable workflow '" + sWorkflowName + "'", null, null);
		IDfWorkflowBuilder oWorkflowBuilder = oSession.newWorkflowBuilder(oProcessId);
    	oWorkflowBuilder.initWorkflow();
    	IDfId oWorkflowId = oWorkflowBuilder.runWorkflow();
    	String sWorkflowId = oWorkflowId.toString(); 
        System.out.println("New workflow '" + sWorkflowName + "' started with Id " + sWorkflowId);
        DfLogger.trace(this, "PoC:: New workflow '" + sWorkflowName + "' started with Id " + sWorkflowId, null, null);
        
        // WORKFLOW STARTED!
        
        System.out.println("Attach document '" + sPackageType + "' to package '" + sPackageName + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Attach document '" + sPackageType + "' to package '" + sPackageName + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);				                				
        oWorkflowBuilder.addPackage(sStartActivity, sPackagePort, sPackageName, sPackageType, null, false, oPackageIds);	            
		
        // PACKAGE DOCUMENT ATTACHED!
        
        System.out.println("Prepare " + oOtherPackages.size() + " other package(s) on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Prepare " + oOtherPackages.size() + " other package(s) on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);					
		Iterator<String> oPackageNames = oOtherPackages.keySet().iterator();
		while (oPackageNames.hasNext()) {
			String sOtherPackageName = (String) oPackageNames.next();
			String sOtherPackageType = (String) oOtherPackages.get(sOtherPackageName);
            System.out.println("\tPrepare other package '" + sOtherPackageName + "' of type '" + sOtherPackageType + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'");
            DfLogger.trace(this, "PoC:: \tPrepare other package '" + sOtherPackageName + "' of type '" + sOtherPackageType + "' on port '" + sPackagePort + "' of start activity '" + sStartActivity + "' for workflow '" + sWorkflowName + "'", null, null);					
			oWorkflowBuilder.addPackage(sStartActivity, sPackagePort, sOtherPackageName, sOtherPackageType, null, false, null);
		}	
		
		// OTHER PACKAGES PREPARED!
		
		System.out.println("New workflow '" + sWorkflowName + "' with Id " + oWorkflowId + ((oWorkflowBuilder.getStartStatus() == 0) ? " successfully started" : " started with errors"));
		DfLogger.trace(this, "PoC:: New workflow '" + sWorkflowName + "' with Id " + oWorkflowId + ((oWorkflowBuilder.getStartStatus() == 0) ? " successfully started" : " started with errors"), null, null);
		IDfWorkflow oWorkflow = oWorkflowBuilder.getWorkflow();
        System.out.println("Check next running activities for new workflow '" + sWorkflowName + "'");
        DfLogger.trace(this, "PoC:: Check next running activities for new workflow '" + sWorkflowName + "'", null, null);
		for (int iNext = 0; iNext < oWorkflow.getActivityCount(); iNext ++) {
			String sActivityState = ACTIVITY_STATE[oWorkflow.getActState(iNext)];
	        System.out.println("\tFound next activity '" + oWorkflow.getActName(iNext) + "' in state '" + sActivityState + "' with " + oWorkflow.getTriggerInput(iNext) + " triggered input port(s) on " + oWorkflow.getTriggerThresh(iNext));
	        DfLogger.trace(this, "PoC:: \tFound next activity '" + oWorkflow.getActName(iNext) + "' in state '" + sActivityState + "' with " + oWorkflow.getTriggerInput(iNext) + " triggered input port(s) on " + oWorkflow.getTriggerThresh(iNext), null, null);
	    }
		
		// WORKFLOW RUNNING INFO LOGGED!
		
        return sWorkflowId;
	}

	public HashMap<String, String> getWorkflowHumanTasks(RepositoryConnection oDocumentumConnection, String sWorkflowId, String sUsername, String sPackageName, Map<String, Object> oAttributes) throws Exception {
		IDfSession oSession = oDocumentumConnection.getSession();
		
        System.out.println("Search tasks for user '" + sUsername + "'");
        DfLogger.trace(this, "PoC:: Search tasks for user '" + sUsername + "'", null, null);
        HashMap<String, String> oValidTasks = null;
		IDfCollection oTasks = oSession.getTasks(sUsername, IDfSession.DF_TASKS, null, "date_sent");
		try { 	
            while (oTasks.next()) {
            	IDfId oItemId = oTasks.getId("item_id");
            	IDfWorkitem oWorkitem = (IDfWorkitem) oSession.getObject(oItemId);
            	IDfActivity oActivity = oWorkitem.getActivity();
            	
            	IDfId oTaskWorkflowId = oWorkitem.getWorkflowId();            	
            	if ((sWorkflowId == null || sWorkflowId.equalsIgnoreCase("")) || (sWorkflowId != null && sWorkflowId.equalsIgnoreCase(oTaskWorkflowId.toString()))) {            		
            		String sActivity = oActivity.getObjectName();
            		System.out.println("Found task named '" + sActivity + "' for user '" + sUsername + "'");
	    			DfLogger.trace(this, "PoC:: Found task named '" + sActivity + "' for user '" + sUsername + "'", null, null);
	    			boolean bValid = true;
	    			
	    			IDfDocument oDocument = null;
	    	        if (sPackageName != null && !sPackageName.equals("")) {
	    	            System.out.println("Search packages for task '" + sActivity + "'");
	    	            DfLogger.trace(this, "PoC:: Search packages for task '" + sActivity + "'", null, null);
	    		    	IDfCollection oPackages = oWorkitem.getPackages(null);
	    		    	int iPackages = 0;
		    	        try {        	
		    	            while (oPackages.next()) {		    	            	
		    	            	String sPackage = oPackages.getString("r_package_name");
	    				        System.out.println("\tFound package '" + sPackage + "' for task '" + sActivity + "'");
	    				        DfLogger.trace(this, "PoC:: \tFound package '" + sPackage + "' for task '" + sActivity + "'", null, null);
		    				    if (sPackage.equalsIgnoreCase(sPackageName)) {
		    				    	IDfId oPackageId = oPackages.getId("r_object_id");
				    	        	IDfPackage oPackage = (IDfPackage) oSession.getObject(oPackageId);
				    	        	IDfId oDocumentId = oPackage.getComponentId(0);
				    	        	oDocument = (IDfDocument) oSession.getObject(oDocumentId);
		    				    }
		    				    iPackages ++;
		    		    	}
		    		        System.out.println("End search: " + iPackages + " package(s) found for task '" + sActivity + "'");
		    		        DfLogger.trace(this, "PoC:: End search: " + iPackages + " package(s) found for task '" + sActivity + "'", null, null);		    	
	    	            } finally {
	    	                if (oPackages != null) oPackages.close();
	    	            }
    	        
		    	        if (oDocument == null) {
		    		        System.out.println("Specified package '" + sPackageName + "' not found or empty for task '" + sActivity + "'");
		    		        DfLogger.warn(this, "PoC:: Specified package '" + sPackageName + "' not found or empty for task '" + sActivity + "'", null, null);
		    		        bValid = false;
		    	        } else {
		    	        	System.out.println("Found specified package '" + sPackageName + "' for task '" + sActivity + "'");
		    	        	DfLogger.trace(this, "PoC:: Found specified package '" + sPackageName + "' for task '" + sActivity + "'", null, null);
		    	        	
		    	        	if (oAttributes != null) {
			    	            System.out.println("Search package '" + sPackageName + "' attributes for task '" + sActivity + "'");
			    	            DfLogger.trace(this, "PoC:: Search package '" + sPackageName + "' attributes for task '" + sActivity + "'", null, null);		    	        	

			    	        	int iAttributes = 0;
			    	            Iterator<String> oAttributeNames = oAttributes.keySet().iterator();
			    	            while (oAttributeNames.hasNext()) {
			    	            	String sName = (String) oAttributeNames.next();
			    	            	Object oValue = oAttributes.get(sName);        	
			    	            	String sClass = oValue.getClass().toString();
			    	        	    System.out.println("\tAttribute '" + sName + "' is defined as a Java '" + sClass + "' type and its value is '" + oValue.toString() + "'");
			    	        	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' is defined as a Java '" + sClass + "' type and its value is '" + oValue.toString() + "'", null, null);        				    	            	
			    	        	    try {
			    	            		boolean bEqual = false;
			    	    	    	    if (sClass.equals("class java.lang.String")) {
			    	    	    	    	bEqual = oDocument.getString(sName).equals((String) oValue);
			    	    	        	} else if (sClass.equals("class java.lang.Integer")) {
			    	    	        		bEqual = oDocument.getInt(sName) == ((Integer) oValue).intValue();
			    	    	        	} else if (sClass.equals("class java.lang.Double")) {
			    	    	        		bEqual = oDocument.getDouble(sName) == ((Double) oValue).doubleValue();
			    	    	        	} else if (sClass.equals("class java.lang.Boolean")) {
			    	    	        		bEqual = oDocument.getBoolean(sName) == ((Boolean) oValue).booleanValue();
			    	    	        	} else if (sClass.equals("class java.util.Date")) {
			    	    	        		DfTime oTime = new DfTime((Date) oValue); 
			    	    	        		bEqual = oDocument.getTime(sName).equals(oTime);
			    	    	        	} else {
			    	    	        	    System.out.println("\tAttribute '" + sName + "' is defined as an unmanaged Java type '" + sClass + "'");
			    	    	        	    DfLogger.warn(this, "PoC:: \tAttribute '" + sName + "' is defined as an unmanaged Java type '" + sClass + "'", null, null);        		
			    	    	        	}
			    	    	    	    if (bEqual) {
			    	    	        	    System.out.println("\tAttribute '" + sName + "' of package '" + sPackageName + "' matches '" + oValue.toString() + "'");
			    	    	        	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' of package '" + sPackageName + "' matches '" + oValue.toString() + "'", null, null);
			    	    	        	    iAttributes ++;
			    	    	    	    } else {
			    	    	        	    System.out.println("\tAttribute '" + sName + "' of package '" + sPackageName + "' does not matches '" + oValue.toString() + "'");
			    	    	        	    DfLogger.debug(this, "PoC:: \tAttribute '" + sName + "' of package '" + sPackageName + "' does not matches '" + oValue.toString() + "'", null, null);
			    	    	        	}
			    	            	} catch (Exception oException) {
			    	        	        System.out.println("\tCannot get attribute '" + sName + "' from package '" + sPackageName + "', error is: " + oException.getMessage());
			    	                    DfLogger.error(this, "PoC:: \tCannot get attribute '" + sName + "' from package '" + sPackageName + "', error is: " + oException.getMessage() + sClass, null, null);    		
			    	    	        }    	    
			    	            }
			    		        System.out.println("End search: " + iAttributes + " attribute(s) found in package '" + sPackageName + "' for task '" + sActivity + "'");
			    		        DfLogger.trace(this, "PoC:: End search: " + iAttributes + " attribute(s) found in package '" + sPackageName + "' for task '" + sActivity + "'", null, null);		    	
			    	            
			    	            if (iAttributes < oAttributes.size()) {
				    	        	System.out.println("Not all specified attributes of package '" + sPackageName + "' found for task '" + sActivity + "'");
				    	        	DfLogger.warn(this, "PoC:: Not all specified attributes of package '" + sPackageName + "' found for task '" + sActivity + "'", null, null);
				    	        	bValid = false;
			    	            } else {
				    		        System.out.println("All specified attributes of package '" + sPackageName + "' found for task '" + sActivity + "'");
				    		        DfLogger.trace(this, "PoC:: All specified attributes of package '" + sPackageName + "' found for task '" + sActivity + "'", null, null);
				    	        }
		    	        	}
		    	        }		    	        
	    	        }
	            	if (bValid) {        		
	            		System.out.println("Task named '" + sActivity + "' for user '" + sUsername + "' is a valid task");
		    			DfLogger.trace(this, "PoC:: Task named '" + sActivity + "' for user '" + sUsername + "' is a valid task", null, null);
		    			if (oValidTasks == null) oValidTasks = new HashMap<String, String>();
		    			oValidTasks.put(oWorkitem.getObjectId().toString(), sActivity);
	            	}
            	}
            }
        } finally {
            if (oTasks != null) oTasks.close();
        }
		System.out.println("End search: " + ((oValidTasks == null) ? 0 : oValidTasks.size()) + " valid task(s) found for user '" + sUsername + "'");
		DfLogger.trace(this, "PoC:: End search: " + ((oValidTasks == null) ? 0 : oValidTasks.size()) + " valid task(s) found for user '" + sUsername + "'", null, null);
		
		return oValidTasks;
	}

	public void completeWorkflowHumanTask(RepositoryConnection oDocumentumConnection, String sWorkitemId) throws Exception {
		IDfSession oSession = oDocumentumConnection.getSession();
		
        IDfWorkitem oWorkitem = (IDfWorkitem) oSession.getObject(new DfId(sWorkitemId));
        IDfActivity oActivity = oWorkitem.getActivity();        
		String sActivity = oActivity.getObjectName();
		int iRuntimeState = oWorkitem.getRuntimeState();
		System.out.println("Get task '" + sActivity + "' in '" + ACTIVITY_STATE[iRuntimeState] + "' state");
		DfLogger.trace(this, "PoC:: Get task '" + sActivity + "' in '" + ACTIVITY_STATE[iRuntimeState] + "' state", null, null);
        
        if (iRuntimeState == IDfWorkitem.DF_WI_STATE_DORMANT) {
			System.out.println("Acquire task '" + sActivity + "'");
			DfLogger.trace(this, "PoC:: Acquire task '" + sActivity + "'", null, null);
	        oWorkitem.acquire();
    		System.out.println("Complete task '" + sActivity + "'");
			DfLogger.trace(this, "PoC:: Complete task '" + sActivity + "'", null, null);
			oWorkitem.complete();
        } else {
    		System.out.println("Cannot complete task '" + sActivity + "' because it is in '" + ACTIVITY_STATE[iRuntimeState] + "' state");
			DfLogger.warn(this, "PoC:: Cannot complete task '" + sActivity + "' because it is in '" + ACTIVITY_STATE[iRuntimeState] + "' state", null, null);        	
        }
    }
	
	public void sendEvent(IDfSession oSession, String sWorkflowId, String sEvent) throws Exception {		
		try {	
			IDfId oWorkflowId = new DfId(sWorkflowId);
			IDfWorkflow oWorkflow = (IDfWorkflow) oSession.getObject(oWorkflowId);
	
			String sObjectName = oWorkflow.getObjectName();
			System.out.println("Workflow name is '" + sObjectName + "'");
			DfLogger.trace(this, "PoC:: Workflow name is '" + sObjectName + "'", null, null);
			
			if (oWorkflow.getRuntimeState() == IDfWorkflow.DF_WF_STATE_RUNNING) {
				System.out.println("Workflow with Id '" + sWorkflowId + "' is running");
				DfLogger.warn(this, "PoC:: Workflow with Id '" + sWorkflowId + "' is running", null, null);							
			
				for (int iActivity = 0; iActivity < oWorkflow.getActivityCount(); iActivity ++) {
					String sActivityName = oWorkflow.getActName(iActivity);
					int iActivityState = oWorkflow.getActState(iActivity); 
					System.out.println("Activity '" + sActivityName + "' is in '" + ACTIVITY_STATE[iActivityState] + "' state");
					DfLogger.trace(this, "PoC:: Activity '" + sActivityName + "' is in '" + ACTIVITY_STATE[iActivityState] + "' state", null, null);
				}
					  
				IDfId oEventId = oWorkflow.queue(null, sEvent, 1, false, null, "Sent application event '" + sEvent + "'");
				System.out.println("Sent event " + oEventId.toString());
				DfLogger.trace(this, "PoC:: Sent event " + oEventId.toString(), null, null);
				
				for (int iActivity = 0; iActivity < oWorkflow.getActivityCount(); iActivity++) {
					String sActivityName = oWorkflow.getActName(iActivity);
					int iActivityState = oWorkflow.getActState(iActivity); 
					System.out.println("Activity '" + sActivityName + "' is in '" + ACTIVITY_STATE[iActivityState] + "' state");
					DfLogger.trace(this, "PoC:: Activity '" + sActivityName + "' is in '" + ACTIVITY_STATE[iActivityState] + "' state", null, null);
				}
			} else {
				System.out.println("Event did not send to workflow with Id '" + sWorkflowId + "' because it is not running");
				DfLogger.warn(this, "PoC:: Event did not send to workflow with Id '" + sWorkflowId + "' because it is not running", null, null);							
			}
		} catch (DfException oException) {
			System.out.println("Event did not send to workflow with Id '" + sWorkflowId + "', message is: " + oException.getMessage());
			DfLogger.warn(this, "PoC:: Event did not send to workflow with Id '" + sWorkflowId + "', message is: " + oException.getMessage(), null, null);			
		}
	}

	public void scheduleDealdineEvent(IDfSession oSession, String sWorkflowId, String sEventName, Date oDeadline, long lDays, long lHours, long lMinutes, long lSeconds, String sSchedulerPath) throws Exception {
	    if (sSchedulerPath == null || sSchedulerPath.equals("")) {
	    	sSchedulerPath = SCHEDULER_PATH;
	    }
		System.out.println("Search deadlines in " + sSchedulerPath);
		DfLogger.trace(this, "PoC:: Search deadlines in " + sSchedulerPath, null, null);
		
		String sDeadline = null;
		if (oDeadline != null) {
			sDeadline = StringUtils.formatDate(oDeadline, StringUtils.DATETIME_EXTENDED_FILE_FORMAT);
		} else {	
			String sNow = StringUtils.formatCurrentDate(StringUtils.DATETIME_EXTENDED_FILE_FORMAT);			
			sDeadline = StringUtils.dateAddition(sNow, lDays, lHours, lMinutes, lSeconds, StringUtils.DATETIME_EXTENDED_FILE_FORMAT);
			System.out.println("Now is " + sNow + ", deadline will be " + sDeadline);
			DfLogger.trace(this, "PoC:: Now is " + sNow + ", deadline will be " + sDeadline, null, null);
		}
							
		Vector<String> oProperties = new Vector<String>();
		oProperties.add("WorkflowId");
		oProperties.add("EventName");			
		
		Vector<String> oValues = new Vector<String>();
		oValues.add(sWorkflowId);
		oValues.add(sEventName);
		
		IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
		oRepositoryFileManager.createPropertiesFile(oSession, sSchedulerPath, sDeadline, oProperties.toArray(), oValues.toArray());			
	
	}	

	public void triggerDeadlineEvent(IDfSession oSession, String sSchedulerPath) throws Exception {	
	    if (sSchedulerPath == null || sSchedulerPath.equals("")) {
	    	sSchedulerPath = SCHEDULER_PATH;
	    }
	    System.out.println("Search deadlines in " + sSchedulerPath);
		DfLogger.trace(this, "PoC:: Search deadlines in " + sSchedulerPath, null, null);			

		IDfFolder oSchedulerFolder = oSession.getFolderByPath(sSchedulerPath);
	    if (oSchedulerFolder == null) {
	        System.out.println("Scheduling folder " + sSchedulerPath + " does not exist");
	        DfLogger.warn(this, "PoC:: Scheduling folder " + sSchedulerPath + " does not exist", null, null);
	    } else {
	        IDfCollection oDocuments = oSchedulerFolder.getContents("object_name");
	        try {
	            while (oDocuments.next()) {
	            	String sDocument = oDocuments.getString("object_name");
	                String sExtension = StringUtils.splitString(sDocument, ".", false)[1];
	                if (sExtension != null && sExtension.equalsIgnoreCase("properties")) {
		                System.out.println("Reading scheduled deadline object " + sDocument);
		                DfLogger.trace(this, "PoC:: Reading scheduled deadline object " + sDocument, null, null);	                
	                	String sDeadline = StringUtils.splitString(sDocument, ".", false)[0];
		                // Conversion to Date for checking date syntax validity
		                Date oDeadline = StringUtils.toDate(sDeadline, StringUtils.DATETIME_EXTENDED_FILE_FORMAT);
		                Date oNow = new Date();
		                System.out.println("Deadline is " + StringUtils.formatDate(oDeadline, StringUtils.DATETIME_FORMAT) + ", now is " + StringUtils.formatDate(oNow, StringUtils.DATETIME_FORMAT));
		                DfLogger.trace(this, "PoC:: Deadline is " + StringUtils.formatDate(oDeadline, StringUtils.DATETIME_FORMAT) + ", now is " + StringUtils.formatDate(oNow, StringUtils.DATETIME_FORMAT), null, null);
		                if (oNow.after(oDeadline)) {
		                	System.out.println("Deadline is passed");
		                	DfLogger.trace(this, "PoC:: Deadline is passed", null, null);
			                IDfSysObject oDocument = (IDfSysObject) oSession.getObjectByPath(sSchedulerPath + "/" + sDocument);
			                PropertyResourceBundle oProperties = new PropertyResourceBundle(oDocument.getContent());			                
			                String sWorkflowId = oProperties.getString("WorkflowId");
			                String sEventName = oProperties.getString("EventName");
			                System.out.println("Send event " + sEventName + " to workflow with Id " + sWorkflowId);
			                DfLogger.trace(this, "PoC:: Send event " + sEventName + " to workflow with Id " + sWorkflowId, null, null);
			                WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
			                oWorkflowEventManager.sendEvent(oSession, sWorkflowId, sEventName);
			                System.out.println("Deadline event " + sEventName + " for workflow with Id " + sWorkflowId + " managed");
			                DfLogger.trace(this, "PoC:: Deadline event " + sEventName + " for workflow with Id " + sWorkflowId + " managed", null, null);
			                oDocument.checkout();
			                oDocument.setObjectName(sDeadline + ".delete");
			                oDocument.save();
		                } else {
		                	System.out.println("Deadline is not passed");
		                	DfLogger.trace(this, "PoC:: Deadline is not passed", null, null);
		                }
	                } else {
		                System.out.println(sDocument + " is not a valid deadline object file");
		                DfLogger.warn(this, "PoC:: " + sDocument + " is not a valid deadline object file", null, null);	                
	                }
	            }
	        } finally {
	            if (oDocuments != null) oDocuments.close();
	        }
	    }
	}

	public void saveLifecycleDetails(IDfSession oSession, String sLifecyclePath, String sWorkflowId, String sDocumentId, String sLifecycleId, String sLifecycleWorkflowId, String sLifecycleName) throws Exception {
	    if (sLifecyclePath == null || sLifecyclePath.equals("")) {
	    	sLifecyclePath = LIFECYCLE_PATH;
	    }
		System.out.println("Save lifecycle information in " + sLifecyclePath);
		DfLogger.trace(this, "PoC:: Save lifecycle information in " + sLifecyclePath, null, null);

		Vector<String> oProperties = new Vector<String>();
		oProperties.add("WorkflowId");
		oProperties.add("DocumentId");			
		oProperties.add("LifecycleId");
		oProperties.add("LifecycleWorkflowId");
		oProperties.add("LifecycleName");
		
		Vector<String> oValues = new Vector<String>();
		oValues.add(sWorkflowId);
		oValues.add(sDocumentId);
		oValues.add(sLifecycleId);
		oValues.add(sLifecycleWorkflowId);
		oValues.add(sLifecycleName);
		
		IRepositoryFileManager oRepositoryFilesManager = RepositoryFileManager.getInstance();
		oRepositoryFilesManager.createPropertiesFile(oSession, sLifecyclePath, sWorkflowId + "_" + sDocumentId, oProperties.toArray(), oValues.toArray());		
	}
	
	public void triggerLifecycleEvent(IDfSession oSession, String sLifecyclePath, String sWorkflowId, String sDocumentId, String sEvent) throws Exception {	
	    if (sLifecyclePath == null || sLifecyclePath.equals("")) {
	    	sLifecyclePath = LIFECYCLE_PATH;
	    }
		System.out.println("Read lifecycle information in " + sLifecyclePath);
		DfLogger.trace(this, "PoC:: Read lifecycle information in " + sLifecyclePath, null, null);

        String sFilename = sWorkflowId + "_" + sDocumentId + ".properties";   
        try {
        	IDfSysObject oFile = (IDfSysObject) oSession.getObjectByPath(sLifecyclePath + "/" + sFilename);
	        PropertyResourceBundle oProperties = new PropertyResourceBundle(oFile.getContent());			                
	        String sLifecycleWorkflowId = oProperties.getString("LifecycleWorkflowId");
	        System.out.println("Send event " + sEvent + " to lifecycle workflow with Id " + sLifecycleWorkflowId);
	        DfLogger.trace(this, "PoC:: Send event " + sEvent + " to lifecycle workflow with Id " + sLifecycleWorkflowId, null, null);
	        WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
	        oWorkflowEventManager.sendEvent(oSession, sLifecycleWorkflowId, sEvent);
	        System.out.println("Lifecycle event " + sEvent + " for lifecycle workflow with Id " + sLifecycleWorkflowId + " managed");
	        DfLogger.trace(this, "PoC:: Lifecycle event " + sEvent + " for lifecycle workflow with Id " + sLifecycleWorkflowId + " managed", null, null);
        } catch (Exception oException) {
	    	System.out.println("Failed to read file " + sFilename + ", Error due to " + oException.getMessage());
	    	DfLogger.error(this, "PoC:: Failed to read file " + sFilename + ", Error due to " + oException.getMessage(), null, oException);        	
        }
	}	
	
	private static String DEFAULT_PACKAGE_INPUT_PORT = "Input:0";
	private static String[] ACTIVITY_FLAG = { "not visible BUT mandatory", "visible and mandatory", "not visible and not mandatory", "visible and not mandatory", "with unknown visibility" };
	private static String[] ACTIVITY_STATE = { "Dormant", "Active", "Finished", "Halted", "Failed" };
	private static String SCHEDULER_PATH = "/System/Applications/PoC Resources/Workflow Deadlines";
	private static String LIFECYCLE_PATH = "/System/Applications/PoC Resources/Workflow Lifecycles";
	private static String DEFAULT_PACKAGES_PATH = "/dmadmin/Resources";

}