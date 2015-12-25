package net.filippov.uniastrum.blackbase_handler.writers;

import static net.filippov.uniastrum.blackbase_handler.main.StringUtils.buildString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.filippov.uniastrum.blackbase_handler.exceptions.LogWriterException;

/**
 * Simple log-writer
 * 
 * @author Oleg Filippov
 *
 */
public class LogWriter {
	
	private PrintWriter logWriter;
	private static final String CHARSET = "UTF-8";
	
	/**
	 * Constructor
	 * 
	 * @param inputFilePath			inputfile full path
	 * @throws LogWriterException	if an output exception occurred
	 */
	public LogWriter(String inputFilePath) throws LogWriterException {
		init(inputFilePath);
	}
	
	/**
	 * Initializes the Writer
	 * 
	 * @param inputFilePath	inputPath to construct output path
	 */
	private void init(String inputFilePath) {
		try {
			logWriter = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(getLogFilePath(inputFilePath)), CHARSET)));
		} catch (FileNotFoundException e) {
			throw new LogWriterException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new LogWriterException(buildString("Incorrect encoding: ", e.getMessage()), e);
		}
	}
	
	/**
	 * Constructs an output filename in the inputFilePath
	 * 
	 * @return	the output path
	 */
	private String getLogFilePath(String inputFilePath) {
		String parentPath = new File(inputFilePath).getParent();
		return buildString(
				parentPath,
				"log_",
				new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()),
				".txt"
		);
	}
	
	/**
	 * Writes the specified string
	 * 
	 * @param str	String to write
	 */
	public void write(String str) {
		logWriter.write(str);
	}
	
	/**
	 * Writes the log-result of a specific record
	 * 
	 * @param countCurrentRecord	the number of a specific record
	 * @param countOutputRecords	the number of records obtained from a specific record
	 */
	public void writeCurrentRecordLog(int countCurrentRecord, int countOutputRecords) {
		logWriter.println(buildString(
				"Input record number: ",
				String.valueOf(countCurrentRecord),
				".\tThe number of output records: ",
				String.valueOf(countOutputRecords)
				));
	}
	
	/**
	 * Writes the total result
	 * 
	 * @param countInput		total number of records
	 * @param countOutputTer	the number of records of type BlackBaseType#TERRORIST
	 * @param countOutputCom	the number of records of type BlackBaseType#COMPANY
	 * @param countException	the of records processed with errors
	 */
	public void writeResult(int countInput, int countOutputTer, int countOutputCom, int countException) {
		logWriter.println(buildString(
				"===================================================================================\r\n",
				"TOTAL records processed:\t",
				String.valueOf(countInput),
				"\r\nThe number of errors:\t",
				String.valueOf(countException),
				"\r\nTOTAL number of output records:",
				"\r\n\tTerrorists\t",
				String.valueOf(countOutputTer),
				"\r\n\tCompanies\t",
				String.valueOf(countOutputCom)));
	}
	
	/**
	 * Prints the stack trace of an exception occurred
	 * 
	 * @param t Throwable instance
	 */
	public void printStackTrace(Throwable t) {
		t.printStackTrace(logWriter);
	}
	
	/**
	 * Closes 
	 */
	public void close() {
		logWriter.close();
	}
}
