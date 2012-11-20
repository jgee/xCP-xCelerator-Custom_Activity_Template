package com.emc.documentum.extensions.methods;

import java.io.PrintWriter;
import java.util.Date;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfProperties;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfProperties;
import com.documentum.fc.common.IDfTime;
import com.emc.documentum.extensions.util.StringUtils;
import com.emc.documentum.extensions.util.WorkflowEventManager;

public class WorkflowDeadlineScheduling extends DfSingleDocbaseModule {

	public int doTask(String workitemid, String WF_ID, String EVENT_NM, String DATE, int DAYS, int HOURS, int MINUTES, int SECONDS, IDfId PATH, PrintWriter oPrintWriter) throws Exception
	{
		IDfTime scheduleDate = null;
		
		if(workitemid == null)
		{
            DfLogger.error(this, "Workitem Id is blank", null, null);
	  	    return -1;
		}
		if(WF_ID == null)
		{
			WF_ID = "";
		}
		if(EVENT_NM == null)
		{
			EVENT_NM = "";
		}
		if(DATE == null)
		{
			scheduleDate = new DfTime(DATE);
		}
		if(PATH == null)
		{
			PATH = new DfId("0000000000000000");
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString(WORKFLOW_ID, WF_ID);
		oParameters.putString(EVENT_NAME, EVENT_NM);
		oParameters.putTime(DEADLINE_DATE, scheduleDate);
		oParameters.putInt(DEADLINE_DAYS, DAYS);
		oParameters.putInt(DEADLINE_HOURS, HOURS);
		oParameters.putInt(DEADLINE_MINUTES, MINUTES);
		oParameters.putInt(DEADLINE_SECONDS, SECONDS);
		oParameters.putId(SCHEDULER_PATH, PATH);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private  int doTask(IDfWorkitem oWorkitem, IDfProperties oActivityParameters, PrintWriter oPrintWriter) throws Exception {
		boolean bStop = false;
		String sWorkflowId = oActivityParameters.getString(WORKFLOW_ID);
		if (sWorkflowId != null && !sWorkflowId.equals("")) {
			System.out.println("Workflow Id: " + sWorkflowId);
	  	    DfLogger.trace(this, "PoC:: Workflow Id: " + sWorkflowId, null, null);			
		} else {
			System.out.println("Workflow Id not specified");
	  	    DfLogger.trace(this, "PoC:: Workflow Id not specified", null, null);
	  	    bStop = true;
		}
		String sEventName = oActivityParameters.getString(EVENT_NAME);
		if (sEventName != null && !sEventName.equals("")) {
			System.out.println("Event name: " + sEventName);
	  	    DfLogger.trace(this, "PoC:: Event name: " + sEventName, null, null);			
		} else {
			System.out.println("Event name not specified");
	  	    DfLogger.trace(this, "PoC:: Event name not specified", null, null);
	  	    bStop = true;
		}
		IDfTime oTime = oActivityParameters.getTime(DEADLINE_DATE);
		Date oDeadline;
		if (oTime != null) {
			oDeadline = oTime.getDate();
	        System.out.println("Deadline: " + StringUtils.formatDate(oDeadline, StringUtils.DATETIME_EXTENDED_FORMAT));
			DfLogger.trace(this, "PoC:: Deadline: " + StringUtils.formatDate(oDeadline, StringUtils.DATETIME_EXTENDED_FORMAT), null, null);
		} else {
			oDeadline = null;
			System.out.println("Deadline not specified");
	  	    DfLogger.trace(this, "PoC:: Deadline not specified", null, null);
		}
		long lDays = (long) oActivityParameters.getInt(DEADLINE_DAYS);
		System.out.println("Days: " + lDays);
  	    DfLogger.trace(this, "PoC:: Days: " + lDays, null, null);
		long lHours = (long) oActivityParameters.getInt(DEADLINE_HOURS);
		System.out.println("Hours: " + lHours);
  	    DfLogger.trace(this, "PoC:: Hours: " + lHours, null, null);
		long lMinutes = (long) oActivityParameters.getInt(DEADLINE_MINUTES);
		System.out.println("Minutes: " + lMinutes);
  	    DfLogger.trace(this, "PoC:: Minutes: " + lMinutes, null, null);
		long lSeconds = (long) oActivityParameters.getInt(DEADLINE_SECONDS);
		System.out.println("Seconds: " + lSeconds);
  	    DfLogger.trace(this, "PoC:: Seconds: " + lSeconds, null, null);		
		IDfId oSchedulerPathId = oActivityParameters.getId(SCHEDULER_PATH);		
		if (oSchedulerPathId != null) {
			System.out.println("Scheduler path Id: " + oSchedulerPathId.toString());
	  	    DfLogger.trace(this, "PoC:: Scheduler path Id: " + oSchedulerPathId.toString(), null, null);			
		} else {
			System.out.println("Scheduler path Id not specified");
	  	    DfLogger.trace(this, "PoC:: Scheduler path Id not specified", null, null);
		}
		if (bStop) {
			System.out.println("Method cannot continue, not enough parameters");
	  	    DfLogger.trace(this, "PoC:: Method cannot continue, not enough parameters", null, null);
		} else {
			try {		
				IDfSession oSession = getSession();
				
				String sSchedulerPath = null;
				try {
					IDfFolder oFolder = (IDfFolder) oSession.getObject(oSchedulerPathId);
					sSchedulerPath = oFolder.getFolderPath(0);
				} catch (DfException oException) { 
					System.out.println("Specified Scheduler path Id '" + oSchedulerPathId.toString() + " does not refer to a valid path");
				}
				
				WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
		  	    oWorkflowEventManager.scheduleDealdineEvent(oSession, sWorkflowId, sEventName, oDeadline, lDays, lHours, lMinutes, lSeconds, sSchedulerPath);

		    } catch (Exception oException) {
		    	System.out.println("Error due to " + oException.getMessage());
		    	DfLogger.error(this, "PoC:: Error due to " + oException.getMessage(), null, oException);
		    } finally {				    	
		    }	    
		}
		return 0;	
	}	
	
	private static final String WORKFLOW_ID = "WORKFLOW_ID";
	private static final String EVENT_NAME = "EVENT_NAME";
	private static final String SCHEDULER_PATH = "SCHEDULER_PATH";
	private static final String DEADLINE_DATE = "DEADLINE_DATE";
	private static final String DEADLINE_DAYS = "DEADLINE_DAYS";
	private static final String DEADLINE_HOURS = "DEADLINE_HOURS";
	private static final String DEADLINE_MINUTES = "DEADLINE_MINUTES";
	private static final String DEADLINE_SECONDS = "DEADLINE_SECONDS";
	
}