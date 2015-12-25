package net.filippov.uniastrum.blackbase_handler.converter;

import static net.filippov.uniastrum.blackbase_handler.main.StringUtils.buildString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.filippov.uniastrum.blackbase_handler.main.BlackBaseType;

/**
 * Class-converter
 * Uses {@code TerroristParser} class
 * 
 * @author Oleg Filippov
 *
 */
public class RecordConverter {

	private String[] headerFieldsInput;
	private FieldNameWrapper wrapper;
	private Set<Record> uniqueRecords = new HashSet<Record>();
	private static final Map<BlackBaseType, List<String[]>> EMPTY_RESULT = Collections.emptyMap();

	/**
	 * Constructor
	 * 
	 * @param headerFields 	an array of header fields names
	 * @param mappingConfig {@code Properties} with a mapping configuration
	 */
	public RecordConverter(String[] headerFields, Properties mappingConfig) {
		this.headerFieldsInput = headerFields;
		this.wrapper = new FieldNameWrapper(mappingConfig);
	}
	
	/**
	 * Converts an input array of record's field values to a result map
	 * 
	 * @param recordFields
	 * @return the result as a Map of {@code BlackBaseType} to an array of fields values
	 */
	public Map<BlackBaseType, List<String[]>> getResultRecords(Object[] recordFields) {
		
		Map<String, String> recordMap = getCurrentRecordMap(recordFields);
		wrapper.setCurrentRecordMap(recordMap);
		Map<BlackBaseType, List<String[]>> resultMap = new HashMap<BlackBaseType, List<String[]>>();
		List<String[]> resultRecordFieldsList = new ArrayList<String[]>();
		
		String name = getFieldValue("name");
		if (name.isEmpty() || name.matches(".*[a-zA-Z]+.*")) {
			return EMPTY_RESULT;
		}
		
		String dateOfBirth = getFieldValue("dateOfBirth");
		String pasportSeries = getFieldValue("pasportSeries").replaceAll("\\s+", "");
		String pasportNumber = getFieldValue("pasportNumber").replaceAll("\\s+", "");
		String inn = getFieldValue("inn").replaceAll("\\s+", "");
		
		BlackBaseType blackBaseType;
		String recordType = getFieldValue("TU");
		if ("1".equals(recordType)) {
			blackBaseType = BlackBaseType.COMPANY;
		} else if ("3".equals(recordType)) {
			blackBaseType = BlackBaseType.TERRORIST;
		} else {
			return EMPTY_RESULT;
		}
		
		
		switch (blackBaseType) {
		case TERRORIST:
			TerroristParser terroristParser = new TerroristParser(
					name, dateOfBirth, pasportSeries, pasportNumber);
			List<Record> terrorists = terroristParser.parseTerrorists();
			Map<String, String> terroristFieldsMap = new HashMap<String, String>(recordMap);
			
			for (Record terrorist : terrorists) {
				boolean isUniqueRecord = uniqueRecords.add(terrorist);
				if (isUniqueRecord) {
					terroristFieldsMap.put(getFieldName("lastName"), terrorist.getLastName());
					terroristFieldsMap.put(getFieldName("firstName"), terrorist.getFirstName());
					terroristFieldsMap.put(getFieldName("middleName"), terrorist.getMiddleName());
					terroristFieldsMap.put(getFieldName("firstMiddleName"), buildString(
							terrorist.getFirstName(),
							" ",
							terrorist.getMiddleName()));
					terroristFieldsMap.put(getFieldName("pasportSeries"), terrorist.getPasportSeries());
					terroristFieldsMap.put(getFieldName("pasportNumber"), terrorist.getPasportNumber());
					terroristFieldsMap.put(getFieldName("clientType"), "Террорист");
					
					String[] terroristFields = new String[wrapper.headerFieldsTerroristLength];
					for (int i = 0; i < wrapper.headerFieldsTerroristLength; i++) {
						terroristFields[i] = terroristFieldsMap.get(getHeaderFieldsTerrorist()[i]);
					}
					resultRecordFieldsList.add(terroristFields);
				}
			}
			break;
		case COMPANY:
			if (!inn.matches("\\d{10,}") || !pasportNumber.matches("\\d{7,}"))
				return EMPTY_RESULT;
			
			recordMap.put(getFieldName("inn"), inn);
			recordMap.put(getFieldName("clientType"), "Стоп-лист");
			
			String[] companyFields = new String[wrapper.headerFieldsCompanyLength];
			for (int i = 0; i < companyFields.length; i++) {
				companyFields[i] = recordMap.get(getHeaderFieldsCompany()[i]);
			}
			resultRecordFieldsList.add(companyFields);
		}

		resultMap.put(blackBaseType, resultRecordFieldsList);
		return resultMap;
	}
	
	private Map<String, String> getCurrentRecordMap(Object[] recordFields) {
		if (headerFieldsInput.length != recordFields.length) {
			// exception
		}
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < headerFieldsInput.length; i++) {
			result.put(headerFieldsInput[i], convertToString(recordFields[i]));
		}
		return result;
	}
	
	private String convertToString(Object obj) {
		if (obj == null) {
			return "";
		} else if (obj instanceof Date) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
			String dateFormatted = dateFormatter.format((Date)obj);
			return dateFormatted;
		}
		// the other types without changes
		return String.valueOf(obj).trim();
	}
	
	public String[] getHeaderFieldsTerrorist() {
		return wrapper.headerFieldsTerroristOrder;
	}
	
	public String[] getHeaderFieldsCompany() {
		return wrapper.headerFieldsCompanyOrder;
	}
	
	private String getFieldName(String key) {
		return wrapper.getFieldName(key);
	}
	
	private String getFieldValue(String key) {
		return wrapper.getFieldValue(key);
	}
	
	/**
	 * The class-wrapper for the field names
	 * 
	 * Uses config/mapping.properties to get output field names
	 * by the names the program works with
	 * 
	 * @author Oleg Filippov
	 *
	 */
	private static class FieldNameWrapper {
		private Map<String, String> mappingFieldsMap = new HashMap<String, String>();
		private String[] headerFieldsTerroristOrder;
		private String[] headerFieldsCompanyOrder;
		private int headerFieldsTerroristLength;
		private int headerFieldsCompanyLength;
		
		private Map<String, String> currentRecordMap;
		
		private FieldNameWrapper(Properties mappingConfig) {
			for (String key : mappingConfig.stringPropertyNames()) {
				mappingFieldsMap.put(key, mappingConfig.getProperty(key));
			}
			
			String[] mappingTerroristOrder = mappingFieldsMap.get("terrorist_order").split("\\s*,\\s*");
			headerFieldsTerroristLength = mappingTerroristOrder.length;
			headerFieldsTerroristOrder = new String[headerFieldsTerroristLength];
			for (int i = 0; i < headerFieldsTerroristLength; i++) {
				headerFieldsTerroristOrder[i] = mappingFieldsMap.get(mappingTerroristOrder[i]);
			}
			
			String[] mappingCompanyOrder = mappingFieldsMap.get("company_order").split("\\s*,\\s*");
			headerFieldsCompanyLength = mappingCompanyOrder.length;
			headerFieldsCompanyOrder = new String[headerFieldsCompanyLength];
			for (int i = 0; i < headerFieldsCompanyLength; i++) {
				headerFieldsCompanyOrder[i] = mappingFieldsMap.get(mappingCompanyOrder[i]);
			}
		}
		
		private void setCurrentRecordMap(Map<String, String> recordMap) {
			this.currentRecordMap = recordMap;
		}
		
		/**
		 * Returns the output field value by the name the program works with
		 * 
		 * @param key field name the program works with
		 * @return the output field value
		 */
		private String getFieldValue(String key) {
			return currentRecordMap.get(mappingFieldsMap.get(key));
		}
		
		/**
		 * Returns the output field name by the name the program works with
		 * 
		 * @param key field name the program works with
		 * @return the output field name
		 */
		private String getFieldName(String key) {
			return mappingFieldsMap.get(key);
		}
	}
}
