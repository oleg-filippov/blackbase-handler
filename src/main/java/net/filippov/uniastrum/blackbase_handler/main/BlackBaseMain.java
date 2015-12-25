package net.filippov.uniastrum.blackbase_handler.main;

import static net.filippov.uniastrum.blackbase_handler.main.StringUtils.buildString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;

import net.filippov.uniastrum.blackbase_handler.converter.RecordConverter;
import net.filippov.uniastrum.blackbase_handler.exceptions.HandlerException;
import net.filippov.uniastrum.blackbase_handler.writers.CsvWriter;
import net.filippov.uniastrum.blackbase_handler.writers.LogWriter;

/**
 * The class reads an input *.dbf file with terrorists, handles the records (parsing, renaming, sorting)
 * and writes the result into *.csv files
 * 
 * @author Oleg Filippov
 *
 */
public class BlackBaseMain {

	private DBFReader dbfReader;
	private CsvWriter fileWriterTerrorist;
	private CsvWriter fileWriterCompany;
	private static LogWriter logWriter;
	private String inputCharset;
	private String[] headerFields;
	
	private RecordConverter converter;
	private Properties mappingConfig;
	
	private static final String CONFIG_PATH = "./config/config.properties";
	private static final String MAPPING_PATH = "./config/mapping.properties";
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * Constructor
	 * 
	 * @param inputFilePath
	 * @throws HandlerException
	 */
	public BlackBaseMain(String inputFilePath)
			throws HandlerException {
		init(inputFilePath);
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		try {
			// args[0]: inputFilePath
			BlackBaseMain handler = new BlackBaseMain(args[0]);
			logWriter = new LogWriter(args[0]);
			handler.processAll();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			logWriter.printStackTrace(e);
		} finally {
			if (logWriter != null) {
				logWriter.close();
			}
		}
	}
	
	/**
	 * Reads config properties from the specified config-file and passes them to streams' initializer
	 * 
	 * @param inputFilePath config-file path
	 */
	private void init(String inputFilePath) {
		Properties config = new Properties();
		mappingConfig = new Properties();
		try {
			config.load(new InputStreamReader(new FileInputStream(CONFIG_PATH), DEFAULT_CHARSET));
			mappingConfig.load(new InputStreamReader(new FileInputStream(MAPPING_PATH), DEFAULT_CHARSET));
			this.inputCharset = config.getProperty("input_encoding");
			
			initStreams(
					inputFilePath,
					config.getProperty("output_encoding"),
					config.getProperty("output_delimiter").charAt(0));
		} catch (FileNotFoundException e) {
			throw new HandlerException("Config-file not found!", e);
		} catch (IOException e) {
			throw new HandlerException("Error reading config-file!", e);
		}
	}
	
	/**
	 * Initializes all the streams
	 * 
	 * @throws JDBFException
	 */
	private void initStreams(String inputFilePath, String outputCharset, char outputDelimiter) {
		
		try {
			dbfReader = new DBFReader(inputFilePath);
		} catch (JDBFException e) {
			throw new HandlerException(
					buildString("Error while initializing an input file!\r\n", e.getMessage()), e);
		}
		fileWriterTerrorist = new CsvWriter(inputFilePath, outputCharset, "Terrorists", outputDelimiter);
		fileWriterCompany = new CsvWriter(inputFilePath, outputCharset, "Companies", outputDelimiter);
	}
	
	/**
	 * Main handler method
	 * 
	 * 1. Reads dbf-file
	 * 2. Handles each record
	 * 3. Writes result into csv-files
	 * 
	 * @throws JDBFException
	 */
	public void processAll() throws HandlerException {
		
		try {
			headerFields = getHeaderFields();
			converter = new RecordConverter(headerFields, mappingConfig);
			
			fileWriterTerrorist.writeRecord(converter.getHeaderFieldsTerrorist());
			fileWriterCompany.writeRecord(converter.getHeaderFieldsCompany());
			
			int countInputAll = 0;
			int countOutputTerAll = 0;
			int countOutputComAll = 0;
			int countException = 0;
			while (dbfReader.hasNextRecord()) {
				Object[] recordFields = dbfReader.nextRecord(Charset.forName(inputCharset));
				countInputAll++;
				try {
					Map<BlackBaseType, List<String[]>> resultMap = converter.getResultRecords(recordFields);
					int countOutputRecords = 0;
					for (Map.Entry<BlackBaseType, List<String[]>> pair : resultMap.entrySet()) {
						switch (pair.getKey()) {
						case TERRORIST:
							for (String[] resultRecordFields : pair.getValue()) {
								try {
									fileWriterTerrorist.writeRecord(resultRecordFields);
								} catch (Exception e) {
									e.printStackTrace();
								}
								countOutputRecords++;
								countOutputTerAll++;
							}
							break;
						case COMPANY:
							for (String[] resultRecordFields : pair.getValue()) {
								fileWriterCompany.writeRecord(resultRecordFields);
								countOutputComAll++;
							}
							break;
						}
					}
					logWriter.writeCurrentRecordLog(countInputAll, countOutputRecords);
				} catch (Exception e) {
					countException++;
					logWriter.printStackTrace(e);
				}
			}
			logWriter.writeResult(countInputAll, countOutputTerAll, countOutputComAll, countException);
		} catch (JDBFException e) {
			throw new HandlerException(buildString("Error while reading *.dbf-file!\r\n", e.getMessage()), e);
		} catch (IOException ioe) {
			throw new HandlerException("Error while reading *.dbf-file!\r\n", ioe);
		} finally {
			if (fileWriterCompany != null) {
				fileWriterCompany.close();
			}
			if (fileWriterTerrorist != null) {
				fileWriterTerrorist.close();
			}
			if (dbfReader != null) {
				try {
					dbfReader.close();
				} catch (JDBFException e) {
					throw new HandlerException(buildString("Error closing *.dbf-file!\r\n", e.getMessage()));
				}
			}
		}
	}
	
	/**
	 * Returns an array of header fields
	 * 
	 * @return result array
	 * @throws IOException
	 */
	private String[] getHeaderFields() throws IOException {
		int fieldCount = dbfReader.getFieldCount();
		String[] headerFields = new String[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			headerFields[i] = dbfReader.getField(i).getName();
		}
		return headerFields;
	}
}
