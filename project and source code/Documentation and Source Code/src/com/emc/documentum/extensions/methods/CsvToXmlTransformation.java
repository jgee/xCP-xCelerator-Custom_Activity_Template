package com.emc.documentum.extensions.methods;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;
import com.emc.documentum.extensions.util.*;

import java.io.*;

public class CsvToXmlTransformation extends _ExtensionWorkflowMethod {
	
	public int doTask(String workitemid, String CSV_PACKAGE_NAME, String XML_DOCUMENT_PATH, String XML_DOCUMENT_NAME, boolean SKIP_EMPTY_LINES, boolean FIRST_ROW_COLUMNS, String COLUMN_PREFIX, String KEY_COLUMN_POSITION, String SEPARATOR_CHAR, String XML_PACKAGE_NAME, PrintWriter oPrintWriter) throws Exception
	{
		if(workitemid == null)
		{
			writeLog(LOG_WARN, "Workitem Id is blank");
	  	    return -1;
		}
		if(CSV_PACKAGE_NAME == null)
		{
			CSV_PACKAGE_NAME = "";
		}
		if(XML_DOCUMENT_PATH == null)
		{
			XML_DOCUMENT_PATH = "";
		}
		if(XML_DOCUMENT_NAME == null)
		{
			XML_DOCUMENT_NAME = "";
		}
		if(COLUMN_PREFIX == null)
		{
			COLUMN_PREFIX = "";
		}
		if(KEY_COLUMN_POSITION == null)
		{
			KEY_COLUMN_POSITION = "";
		}
		if(SEPARATOR_CHAR == null)
		{
			SEPARATOR_CHAR = "";
		}
		if(XML_PACKAGE_NAME == null)
		{
			XML_PACKAGE_NAME = "";
		}
		
		IDfProperties oParameters = new DfProperties();
		IDfWorkitem oWorkitem = (IDfWorkitem)getSession().getObject(new DfId(workitemid));
		
		oParameters.putString("CSV_PACKAGE_NAME", CSV_PACKAGE_NAME);
		oParameters.putString("XML_DOCUMENT_PATH", XML_DOCUMENT_PATH);
		oParameters.putString("XML_DOCUMENT_NAME", XML_DOCUMENT_NAME);
		oParameters.putBoolean("SKIP_EMPTY_LINES", SKIP_EMPTY_LINES);
		oParameters.putBoolean("FIRST_ROW_COLUMNS", FIRST_ROW_COLUMNS);
		oParameters.putString("COLUMN_PREFIX", COLUMN_PREFIX);
		oParameters.putString("KEY_COLUMN_POSITION", KEY_COLUMN_POSITION);
		oParameters.putString("SEPARATOR_CHAR", SEPARATOR_CHAR);
		oParameters.putString("XML_PACKAGE_NAME", XML_PACKAGE_NAME);
		
		return doTask( oWorkitem,  oParameters,  oPrintWriter);
	}
	
	private int doTask(IDfWorkitem oWorkitem, IDfProperties oParameters, PrintWriter oPrintWriter) throws Exception {

		setClass("CsvToXmlTransformation");
		
		String sCsvPackageName = getWorkitemStringParameter(oParameters, "CSV_PACKAGE_NAME");
		String sXmlDocumentPath = getWorkitemStringParameter(oParameters, "XML_DOCUMENT_PATH");
		String sXmlDocumentName = getWorkitemStringParameter(oParameters, "XML_DOCUMENT_NAME");
		boolean bSkipEmptyLines = getWorkitemBooleanParameter(oParameters, "SKIP_EMPTY_LINES");
		boolean bFirstRowColumns = getWorkitemBooleanParameter(oParameters, "FIRST_ROW_COLUMNS");
		String sColumnPrefix = getWorkitemStringParameter(oParameters, "COLUMN_PREFIX");
		String sKeyColumnPosition = getWorkitemStringParameter(oParameters, "KEY_COLUMN_POSITION");
		String sSeparator = getWorkitemStringParameter(oParameters, "SEPARATOR_CHAR");
		String sXmlPackageName = getWorkitemStringParameter(oParameters, "XML_PACKAGE_NAME");
		
		if (isNull(sCsvPackageName) ||
			isNull(sXmlDocumentPath) || isNull(sXmlDocumentName) || 
			isNull(sKeyColumnPosition) || isNull(sSeparator)) {
			writeLog(LOG_WARN, STOP_METHOD_MESSAGE);
	  	    return -1;
		} 

		try {		
			IDfSession oSession = getSession();
			
			IDfPackage oCsvPackage = getWorkitemPackage(oWorkitem, sCsvPackageName);
        	IDfDocument oCsvDocument = getPackageDocument(oSession, oCsvPackage, 0, null);
        	if (foundObject(oCsvDocument)) {
        		String sCsvDocument = oCsvDocument.getObjectName();
        		writeLog(LOG_INFO, "Start transformation of CSV document " + sCsvDocument + " to XML");
        		StringBuffer oXmlContent = new StringBuffer();
        		oXmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        		oXmlContent.append(System.getProperty("line.separator"));
        		oXmlContent.append("<CSV>");
        		oXmlContent.append(System.getProperty("line.separator"));
        		oXmlContent.append("\t<Filename>" + sXmlDocumentName + "</Filename>");
        		oXmlContent.append(System.getProperty("line.separator"));
        		oXmlContent.append("\t<Filepath>" + sXmlDocumentPath+ "</Filepath>");
        		oXmlContent.append(System.getProperty("line.separator"));
        		oXmlContent.append("\t<Content>");
        		oXmlContent.append(System.getProperty("line.separator"));
        		IRepositoryFileManager oRepositoryFileManager = RepositoryFileManager.getInstance();
				String[] aDocumentLines = oRepositoryFileManager.readPlainDocument(oCsvDocument, bSkipEmptyLines, true, true);
				int iKeyColumn = StringUtils.toInt(sKeyColumnPosition);
				String sToken = null;
				oXmlContent.append("\t\t<Columns>");
				oXmlContent.append(System.getProperty("line.separator"));
				if (aDocumentLines.length > 1) {
					writeLog(LOG_DEBUG, "Tokenize first line [" + aDocumentLines[0] + "]");
					String[] aToken = StringUtils.splitString(aDocumentLines[0], sSeparator, false);
					if (aToken == null) {
						writeLog(LOG_WARN, "First line tokens are empty [" + aDocumentLines[0] + "]");
					} else {
						writeLog(LOG_DEBUG, "First line tokens availables [" + aDocumentLines[0] + "]");
						sToken = StringUtils.replacePattern(aToken[iKeyColumn], "&", "+", true);
						oXmlContent.append("\t\t\t<Column colpos=\"0\">" +  sToken + "</Column>");
						oXmlContent.append(System.getProperty("line.separator"));
						int iColpos = 1;
						for (int iColumn = 0; iColumn < aToken.length; iColumn ++ ) {
							if (iColumn != iKeyColumn) {
								if (bFirstRowColumns) {
									sToken = StringUtils.replacePattern(aToken[iColumn], "&", "+", true);
									oXmlContent.append("\t\t\t<Column colpos=\"" + iColpos + "\">" +  sToken + "</Column>");
									oXmlContent.append(System.getProperty("line.separator"));
								} else {
									oXmlContent.append("\t\t\t<Column colpos=\"" + iColpos + "\">" + sColumnPrefix + iColumn + "</Column>");
									oXmlContent.append(System.getProperty("line.separator"));
								}
								iColpos ++;
							}
						}
					}
				}
				oXmlContent.append("\t\t</Columns>");
				oXmlContent.append(System.getProperty("line.separator"));
				oXmlContent.append("\t\t<Rows>");
				oXmlContent.append(System.getProperty("line.separator"));
				int iRowpos = 0;
				for (int iRow = 1; iRow < aDocumentLines.length; iRow ++) {
					String[] aToken = StringUtils.splitString(aDocumentLines[iRow], sSeparator, false);
					if (aToken == null) {
						writeLog(LOG_WARN, "Line " + iRow + " tokens are empty [" + aDocumentLines[iRow] + "]");
					} else {
						writeLog(LOG_DEBUG, "Line " + iRow + " tokens availables [" + aDocumentLines[iRow] + "]");
						oXmlContent.append("\t\t\t<Row rowpos=\"" + iRowpos + "\">");
						oXmlContent.append(System.getProperty("line.separator"));
						sToken = StringUtils.replacePattern(aToken[iKeyColumn], "&", "+", true);
						oXmlContent.append("\t\t\t\t<Cell rowpos=\"" + iRowpos + "\" colpos=\"0\">" +  sToken + "</Cell>");
						oXmlContent.append(System.getProperty("line.separator"));
						int iColpos = 1;
						for (int iColumn = 0; iColumn < aToken.length; iColumn ++ ) {
							if (iColumn != iKeyColumn) {
								sToken = StringUtils.replacePattern(aToken[iColumn], "&", "+", true);
								oXmlContent.append("\t\t\t\t<Cell rowpos=\"" + iRowpos + "\" colpos=\"" + iColumn + "\">" +  sToken + "</Cell>");
								oXmlContent.append(System.getProperty("line.separator"));
							}
							iColpos ++;
						}
						oXmlContent.append("\t\t\t</Row>");
						oXmlContent.append(System.getProperty("line.separator"));
						iRowpos ++;
					}
				}
				oXmlContent.append("\t\t</Rows>");
				oXmlContent.append(System.getProperty("line.separator"));
				oXmlContent.append("\t</Content>");
				oXmlContent.append(System.getProperty("line.separator"));
				oXmlContent.append("</CSV>");
				oXmlContent.append(System.getProperty("line.separator"));
				String sXmlContent = oXmlContent.toString();
				writeLog(LOG_DEBUG, "XML content of transformed " + sCsvDocument + " document is:" + System.getProperty("line.separator") + sXmlContent);

				oRepositoryFileManager.createDocument(oSession, sXmlDocumentPath, sXmlDocumentName, "dm_document", "xml", sXmlContent.getBytes());
			}
        	
        	String sXmlDocument = sXmlDocumentPath + "/" + sXmlDocumentName;
        	if (!isNull(sXmlPackageName) && foundObject(oSession, sXmlDocument)) {
        		
            	IDfPackage oPackage = getWorkitemPackage(oWorkitem, sXmlPackageName);
            	addDocumentToPackage(getSession(), oWorkitem, oPackage, sXmlDocument);
        	}
        	
	    } catch (Exception oException) {
	    	writeLog(LOG_ERROR, "Error due to " + oException.getMessage(), oException);
	    } finally {				    	
	    }	    
		return 0;
	}

}