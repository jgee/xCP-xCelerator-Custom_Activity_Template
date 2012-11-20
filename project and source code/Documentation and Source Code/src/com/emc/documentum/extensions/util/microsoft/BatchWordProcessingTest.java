package com.emc.documentum.extensions.util.microsoft;

public class BatchWordProcessingTest {

  public static void main(String[] args) throws Exception {
	
	BatchWordProcessing oWord = new BatchWordProcessing(BARCODES_PATH + "WordProcessing.txt", 
														BARCODES_PATH + "WordProcessing.log");

	System.out.println("Create new document");
	oWord.createNewDocumentFromTemplate(BARCODES_PATH + "MortgageBarcodes.doc");
	
	oWord.typeTextAtBookmark("NUMERO_PRATICA", "123");
	
	oWord.typeTextAtBookmark("INTESTATARI", "Michele Vaccaro");
	
	oWord.typeTextAtBookmark("MODULO_RICHIESTA_BARCODE", "*123-456-7890*");
	oWord.typeTextAtBookmark("MODULO_RICHIESTA_TEXT", "*123-456-7890*");
	
	oWord.typeTextAtBookmark("CARTA_IDENTITA_BARCODE", "*123-456-7890*");
	oWord.typeTextAtBookmark("CARTA_IDENTITA_TEXT", "*123-456-7890*");
	
	oWord.typeTextAtBookmark("CODICE_FISCALE_BARCODE", "*123-456-7890*");
	oWord.typeTextAtBookmark("CODICE_FISCALE_TEXT", "*123-456-7890*");
	
	oWord.typeTextAtBookmark("MODELLO_CUD_BARCODE", "*123-456-7890*");
	oWord.typeTextAtBookmark("MODELLO_CUD_TEXT", "*123-456-7890*");
	
	oWord.saveDocumentAsAndClose("C:\\_PRJ\\Resources\\Barcodes\\Documentum.doc");
	 
	System.out.println("Execute commands and exit");
	oWord.quitApplicationAfterWaiting(1000);
	oWord.exec();	
	System.out.println("Done");
  }

  public static String BARCODES_PATH = "C:\\_PRJ\\Resources\\SWord\\";
}