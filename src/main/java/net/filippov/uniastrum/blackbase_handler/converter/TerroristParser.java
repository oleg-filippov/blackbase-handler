package net.filippov.uniastrum.blackbase_handler.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class parses the "NAME"-field and constructs the result as List<Record>
 * Uses {@code Record} class
 * 
 * @author Oleg Filippov
 *
 */
public class TerroristParser {

	private String name;
	private String dateOfBirth;
	private String pasportSeries;
	private String pasportNumber;
	
	/**
	 * Constructor
	 * 
	 * @param name
	 * @param dateOfBirth
	 * @param pasportSeries
	 * @param pasportNumber
	 */
	public TerroristParser(String name, String dateOfBirth,
			String pasportSeries, String pasportNumber) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.pasportSeries = pasportSeries;
		this.pasportNumber = pasportNumber;
	}

	/**
	 * Analyzes the record fields values specified in constructor
	 * 
	 * @return the result as a List<Record>
	 */
	public List<Record> parseTerrorists() {
		List<String[]> fullNamesList = new ArrayList<String[]>();
		List<Record> result = new ArrayList<Record>();
		name = name.replaceAll("\\s*-\\s*", "-"); // remove spaces before and after hyphens
		
		int bracketIndex = name.indexOf("(");
		if (bracketIndex == -1) {
			fullNamesList.addAll(getFullNameElements(name));
		} else {
			String mainName = name.substring(0, bracketIndex).trim();
			fullNamesList.addAll(getFullNameElements(mainName));
			
			String otherNamesString = name.substring(bracketIndex+1, name.lastIndexOf(")")).trim();
			String[] otherNames = otherNamesString.split("\\s*;\\s*");
			for (String otherName : otherNames) {
				fullNamesList.addAll(getFullNameElements(otherName));
			}
		}
		
		if (pasportSeries.isEmpty() && !pasportNumber.isEmpty()) {
			pasportNumber = "";
		} else if (pasportNumber.isEmpty() && !pasportSeries.isEmpty()) {
			pasportSeries = "";
		} else if (!pasportSeries.matches("\\d{4}") || !pasportNumber.matches("\\d{6}")) {
			pasportSeries = "";
			pasportNumber = "";
		}
		
		for (String[] fullNameElements : fullNamesList) {
			if (fullNameElements.length == 3) {
				result.add(new Record(
						fullNameElements, dateOfBirth, pasportSeries, pasportNumber));
			}
		}
		
		return result;
	}
	
	private static List<String[]> getFullNameElements(String name) {
		String[] nameElements = name.split("\\s+");
		
		if (nameElements.length < 2)
			return Collections.emptyList();
		
		List<String[]> result = new ArrayList<String[]>();
		
		String lastName = nameElements[0];
		String firstName = nameElements[1];
		String middleName;
		
		boolean additionalFirstNameExists = nameElements.length > 2
				&& nameElements[2].startsWith("(")
				&& nameElements[2].endsWith(")");
		int middleNameStartIndex = additionalFirstNameExists ? 3 : 2;
		
		StringBuilder middleNameBuilder = new StringBuilder();
		for (int i = middleNameStartIndex; i < nameElements.length; i++) {
			middleNameBuilder.append(nameElements[i]).append(" ");
		}
		middleName = middleNameBuilder.length() > 0
				? middleNameBuilder.substring(0, middleNameBuilder.length()-1).toString()
				: "";
		
		result.add(new String[]{lastName, firstName, middleName});
		if (additionalFirstNameExists) {
			String additionalFirstName = nameElements[2].substring(1, nameElements[2].length()-1).trim();
			result.add(new String[]{lastName, additionalFirstName, middleName});
		}
		return result;
	}
}

