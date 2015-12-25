package net.filippov.uniastrum.blackbase_handler.writers;

import static net.filippov.uniastrum.blackbase_handler.main.StringUtils.buildString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import net.filippov.uniastrum.blackbase_handler.exceptions.CsvWriterException;

/**
 * Writer in *.csv format
 * 
 * @author Oleg Filippov
 *
 */
public class CsvWriter {
	
	private PrintWriter csvWriter;
	private final char OUTPUT_DELIMITER;
	
	/**
	 * Constructor
	 * 
	 * @param inputFilePath			inputfile full path
	 * @param charset				specified charset
	 * @param blackBaseType			the type of a record BlackBaseType
	 * @param output_delimiter		delimiter used between the record field's values
	 * @throws CsvWriterException	if an output exception occurred
	 */
	public CsvWriter(String inputFilePath, String charset, String blackBaseType, char output_delimiter)
			throws CsvWriterException {
		
		init(inputFilePath, charset, blackBaseType);
		OUTPUT_DELIMITER = output_delimiter;
	}
	
	/**
	 * Initializes the Writer
	 * 
	 * @param inputPath		inputPath to construct output path
	 * @param charset		specified charset
	 * @param blackBaseType	the type of a record BlackBaseType
	 */
	private void init(String inputPath, String charset, String blackBaseType) {
		try {
			csvWriter = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(getOutputFilePath(inputPath, blackBaseType)), charset)));
		} catch (FileNotFoundException e) {
			throw new CsvWriterException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new CsvWriterException(buildString("Incorrect encoding: ", e.getMessage()), e);
		}
	}
	
	/**
	 * Constructs an output filename in the inputFilePath
	 * 
	 * @param prefix	String representation of record type BlackBaseType
	 * @return			the output path
	 */
	private String getOutputFilePath(String inputFilePath, String prefix) {
		File inputFile = new File(inputFilePath);
		return buildString(
				inputFile.getParent(),
				prefix,
				" ",
				inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".")+1),
				"csv"
		);
	}
	
	/**
	 * Writes the current record field values
	 * 
	 * @param recordFields	an array of record fields
	 */
	public void writeRecord(String[] recordFields) {
		String outputLine = buildString(
				OUTPUT_DELIMITER,
				true,
				recordFields);
		if (outputLine.indexOf("\r\n") > -1) {
			outputLine = outputLine.replaceAll("\r\n", " ");
		}
		csvWriter.println(outputLine);
	}
	
	/**
	 * Closes writer
	 */
	public void close() {
		csvWriter.close();
	}

}
