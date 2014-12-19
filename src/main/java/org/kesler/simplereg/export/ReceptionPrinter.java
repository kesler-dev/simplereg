package org.kesler.simplereg.export;

import org.apache.log4j.Logger;
import org.kesler.simplereg.logic.Reception;
import org.kesler.simplereg.util.OptionsUtil;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ReceptionPrinter {
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	protected Reception reception;

	ReceptionPrinter(Reception reception) {
		this.reception = reception;
	}

	public abstract void printReception() throws Exception;

	protected String getRequestTemplatePath() {
		String jarPath = OptionsUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String dirSeparator = System.getProperty("file.separator");

		String requestTemplateFileName = OptionsUtil.getOption("print.request");

		String templateDir = new File(jarPath).getParent() + dirSeparator + "templates" + dirSeparator;

		String requestTemplatePath = templateDir + requestTemplateFileName;

//		File requestTemplateFile = new File(requestTemplatePath);
//
//		log.info("Try to open file: " + requestTemplatePath);
//
//		if (!requestTemplateFile.exists()) {
//			log.error("Cannot open file: " + requestTemplatePath);
//			return "";
//		}
		return requestTemplatePath;

	}
	protected InputStream getRequestInputStream() throws Exception{
		File file = new File(getRequestTemplatePath());
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			log.error("Cannot open input stream");
			throw new Exception(ex);
		}
		return inputStream;

	}
	protected String getRequestSavePath() {
		String jarPath = OptionsUtil.getCurrentDir();
		String dirSeparator = OptionsUtil.getDirSeparator();


		String outDir = new File(jarPath).getParent() + dirSeparator + "out" + dirSeparator;

		File outDirFile = new File(outDir);
		if (!outDirFile.exists()) outDirFile.mkdir();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh-mm-ss");


		String requestPath = outDir + "request " + dateFormat.format(new Date()) +".docx";

		log.info("Prepare request path: " + requestPath);

		return requestPath;
	}

	protected void openFile(String filePath) {
		Desktop desktop = null;
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
		}

		//Открытие файла:

		try {
			desktop.open(new File(filePath));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

}