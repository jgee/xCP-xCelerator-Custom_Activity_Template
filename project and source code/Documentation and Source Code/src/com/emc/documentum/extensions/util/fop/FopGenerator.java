package com.emc.documentum.extensions.util.fop;

import java.io.*;

import org.apache.fop.apps.*;

import com.documentum.fc.common.DfLogger;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

public class FopGenerator {

	public void generatePdf(String sFopXmlFileSystemPath, String sPdfFileSystemPath) throws Exception {
		System.out.println("Create FOP engine to transform the provided FOP XML file " + sFopXmlFileSystemPath + " on local file system");
		DfLogger.trace(this, "PoC:: Create FOP engineto transform the provided FOP XML file " + sFopXmlFileSystemPath + " on local file system", null, null);
		FopFactory oFopFactory = FopFactory.newInstance();
		OutputStream oOutputStream = new BufferedOutputStream(new FileOutputStream(new File(sPdfFileSystemPath)));
		try {
			Fop oFop = oFopFactory.newFop(MimeConstants.MIME_PDF, oOutputStream);
			TransformerFactory oTransformerFactory = TransformerFactory.newInstance();
			Transformer oTransformer = oTransformerFactory.newTransformer();
			Source oSource = new StreamSource(new File(sFopXmlFileSystemPath));
			Result oResult = new SAXResult(oFop.getDefaultHandler());
			System.out.println("Trasnform FOP XML to PDF on local file system path " + sPdfFileSystemPath);
			DfLogger.trace(this, "PoC:: Trasnform FOP XML to PDF on local file system path " + sPdfFileSystemPath, null, null);
			oTransformer.transform(oSource, oResult);
			System.out.println("FOP PDF file " + sPdfFileSystemPath + " created on local file system");
			DfLogger.info(this, "PoC:: FOP PDF file " + sPdfFileSystemPath + " created on local file system", null, null);
		} catch (FOPException oFopException) {
			System.out.println("PoC:: FOP PDF file " + sPdfFileSystemPath + " not created on local file system, Error due to " + oFopException.getMessage());
			DfLogger.error(this, "PoC:: FOP PDF file " + sPdfFileSystemPath + " not created on local file system, Error due to ", null, oFopException);
		} finally {
			oOutputStream.close();
		}
	}
	
	public void generatePdf(byte[] aFopXmlContent, String sPdfFileSystemPath) throws Exception {

		int iContentSize = aFopXmlContent.length / 1024;
		System.out.println("Create FOP engine to transform the provided FOP XML content of " + iContentSize + "KB");
		DfLogger.trace(this, "PoC:: Create FOP engine to transform the provided FOP XML content of " + iContentSize + "KB", null, null);
		FopFactory oFopFactory = FopFactory.newInstance();
		OutputStream oOutputStream = new BufferedOutputStream(new FileOutputStream(new File(sPdfFileSystemPath)));
		try {
			Fop oFop = oFopFactory.newFop(MimeConstants.MIME_PDF, oOutputStream);
			TransformerFactory oTransformerFactory = TransformerFactory.newInstance();
			Transformer oTransformer = oTransformerFactory.newTransformer();
			ByteArrayInputStream oFopXmlContent = new ByteArrayInputStream(aFopXmlContent);
			Source oSource = new StreamSource(new BufferedReader(new InputStreamReader(oFopXmlContent)));
			Result oResult = new SAXResult(oFop.getDefaultHandler());
			System.out.println("Trasnform FOP XML to PDF on local file system path " + sPdfFileSystemPath);
			DfLogger.trace(this, "PoC:: Trasnform FOP XML to PDF on local file system path " + sPdfFileSystemPath, null, null);
			oTransformer.transform(oSource, oResult);
			System.out.println("FOP PDF file " + sPdfFileSystemPath + " created on local file system");
			DfLogger.info(this, "PoC:: FOP PDF file " + sPdfFileSystemPath + " created on local file system", null, null);
		} catch (FOPException oFopException) {
			System.out.println("PoC:: FOP PDF file " + sPdfFileSystemPath + " not created on local file system, Error due to " + oFopException.getMessage());
			DfLogger.error(this, "PoC:: FOP PDF file " + sPdfFileSystemPath + " not created on local file system, Error due to ", null, oFopException);
		} finally {
			oOutputStream.close();
		}
	}
	
	public static final String FILESYSTEM_IMPORT_PATH = "C://_PRJ//Resources//FOP//";

	public static void main(String[] args) throws Exception {
		
		System.out.println("Start PDF generation");
		FopGenerator oFopGenerator = new FopGenerator();
		oFopGenerator.generatePdf("C:/_PRJ/XML/Transformations/FOP/Test/Test.fo.xml",
							   "C:/_PRJ/XML/Transformations/FOP/Test/Output.pdf");
		System.out.println("End PDF generation");
	}

}
