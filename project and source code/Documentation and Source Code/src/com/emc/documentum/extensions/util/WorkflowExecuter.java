package com.emc.documentum.extensions.util;

import java.util.*;

public class WorkflowExecuter {
		
	public static void main(String[] args) throws Exception 
	{
		RepositoryConnection oDocumentumConnection = new RepositoryConnection();
		try {		
			oDocumentumConnection.beginTransaction();    

			WorkflowEventManager oWorkflowEventManager = new WorkflowEventManager();
			
			int iAction = 2;
			if (iAction == 0) {
				
				Map<String, Object> oAttributes = new HashMap<String, Object>();
				oAttributes.put("subject", "Documento di test");
							
				oWorkflowEventManager.startRunnableWorkflow(
						oDocumentumConnection, 
						"Test Add Package Workflow",
						"Document1Package",
						oAttributes,
						"Primo Documento di Testo",
						"dm_document",
						"crtext",
						"EXAPLE TEST".getBytes());
			}

			if (iAction == 1) {

				Map<String, Object> oAttributes = new HashMap<String, Object>();
				oAttributes.put("subject", "Documento di test");
				
				HashMap<String, String> oTasks = oWorkflowEventManager.getWorkflowHumanTasks(oDocumentumConnection, null, "Administrator", "Document1Package", oAttributes);
				if (oTasks == null) {
					System.out.println("No tasks found");
				} else {
					System.out.println(oTasks.size() + " tasks found");
					Iterator<String> oAttributeNames = oTasks.keySet().iterator();
					while (oAttributeNames.hasNext()) {
							String sTaskId = (String) oAttributeNames.next();
							System.out.println("Processing task number " + sTaskId);
							oWorkflowEventManager.completeWorkflowHumanTask(oDocumentumConnection, sTaskId);
					}
	            }
			}
			
			if (iAction == 2) {
				
				Map<String, Object> oAttributes1 = new HashMap<String, Object>();
				oAttributes1.put("subject", "Pratica Elettronica di Mutuo");
				oAttributes1.put("soggetto1_codice", "123456");
				oAttributes1.put("soggetto1_nome", "Andrea");
				oAttributes1.put("soggetto1_cognome", "Fazioni");
				oAttributes1.put("soggetto1_indirizzo", "Via Caldera 21");
				oAttributes1.put("soggetto1_comune", "Milano");
				oAttributes1.put("soggetto1_provincia", "MI");
				oAttributes1.put("soggetto1_cap", "20153");
				oAttributes1.put("soggetto1_telefono", "0240908653");
				oAttributes1.put("soggetto1_email", "afazioni@bpmdemo.com");
				oAttributes1.put("soggetto1_imposta_sost", "Prima casa");
				oAttributes1.put("soggetto1_intestazione", "Il soggetto si intesterà l'immobile");
				oAttributes1.put("immobile_indirizzo", "Via Montenapoleone 1");
				oAttributes1.put("immobile_comune", "Milano");
				oAttributes1.put("immobile_provincia", "MI");
				oAttributes1.put("immobile_cap", "20100");
				oAttributes1.put("immobile_nazione", "Italia");
				oAttributes1.put("soluzione_durata_mutuo", new Integer(25));
				oAttributes1.put("soluzione_importo_richiesto", "215000");
				oAttributes1.put("soluzione_limite_tasso", "No");
				oAttributes1.put("soluzione_mix_tasso", "50% Fisso + 50% Variabile");
				oAttributes1.put("soluzione_piano_rimborso", "Francese");
				// oAttributes1.put("soggetto1_data_nascita", StringUtils.toDate("28/03/1973", StringUtils.DATE_FORMAT));
				// oAttributes1.put("soggetto1_codice", new Integer(35));
				// oAttributes1.put("customer_male", new Boolean(true));
				// oAttributes1.put("customer_amount", new Double(12000.45));
				
				oWorkflowEventManager.startRunnableWorkflow(
						oDocumentumConnection, 
						"Nuova Pratica Elettronica di Mutuo Workflow",
						"PraticaPackage",
						oAttributes1,
						"Nuova Pratica di Mutuo",
						"pratica_mutuo",
						null,
						null);
			}

			if (iAction == 3) {
				
				String sLifecycleName = "Implementation Proposal Lifecycle";
				
				String sLifecycleWorkflowId = oWorkflowEventManager.startRunnableWorkflow(oDocumentumConnection, sLifecycleName);
				// String sLifecycleWorkflowId = "4d0030398000b90c";

				String sWorkflowId = "4d0030398000a900";
				String sDocumentId = "090030398001b540";
				String sLifecycleId = "460030398000a55a";
				
				oWorkflowEventManager.saveLifecycleDetails(oDocumentumConnection.getSession(), null, sWorkflowId, sDocumentId, sLifecycleId, sLifecycleWorkflowId, sLifecycleName);
				// oWorkflowEventManager.sendEvent(oDocumentumConnection.getSession(), sLifecycleWorkflowId, "FORMALIZED");
				// oWorkflowEventManager.sendEvent(oDocumentumConnection.getSession(), sLifecycleWorkflowId, "CONSOLIDATED");
				// oWorkflowEventManager.sendEvent(oDocumentumConnection.getSession(), sLifecycleWorkflowId, "CONTRIBUTED");
				// oWorkflowEventManager.sendEvent(oDocumentumConnection.getSession(), sLifecycleWorkflowId, "REFINED");
				// oWorkflowEventManager.sendEvent(oDocumentumConnection.getSession(), sLifecycleWorkflowId, "CLOSED");
			}
			
			oDocumentumConnection.commitTransaction();
	    } catch (Exception oException) {
	    	oDocumentumConnection.abortTransaction();
	    	System.out.println("Error due to " + oException.getMessage());
	    	oException.printStackTrace();
	    } finally {				    	
	    	oDocumentumConnection.release();
	    }
	}
}