package net.filippov.uniastrum.blackbase_handler.converter;

import java.util.Arrays;

/**
 * The class represents the record entity an is used as a result of {@code TerroristParser}
 * 
 * @author Oleg Filippov
 *
 */
public class Record {

	private String[] fullName;
	private String dateOfBirth;
	private String pasportSeries;
	private String pasportNumber;
	
	public Record(String[] fullName, String dateOfBirth,
			String pasportSeries, String pasportNumber) {
		this.fullName = fullName;
		this.dateOfBirth = dateOfBirth;
		this.pasportSeries = pasportSeries;
		this.pasportNumber = pasportNumber;
	}

	public String getLastName() {
		return fullName[0];
	}
	
	public String getFirstName() {
		return fullName[1];
	}
	
	public String getMiddleName() {
		return fullName[2];
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getPasportSeries() {
		return pasportSeries;
	}

	public String getPasportNumber() {
		return pasportNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + Arrays.hashCode(fullName);
		result = prime * result
				+ ((pasportNumber == null) ? 0 : pasportNumber.hashCode());
		result = prime * result
				+ ((pasportSeries == null) ? 0 : pasportSeries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (!Arrays.equals(fullName, other.fullName))
			return false;
		if (pasportNumber == null) {
			if (other.pasportNumber != null)
				return false;
		} else if (!pasportNumber.equals(other.pasportNumber))
			return false;
		if (pasportSeries == null) {
			if (other.pasportSeries != null)
				return false;
		} else if (!pasportSeries.equals(other.pasportSeries))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(fullName)
				+ ", " + dateOfBirth + ", "
				+ pasportSeries + ", " + pasportNumber;
	}


}
