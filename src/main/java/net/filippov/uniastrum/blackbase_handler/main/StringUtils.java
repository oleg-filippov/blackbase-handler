package net.filippov.uniastrum.blackbase_handler.main;

/**
 * String util-methods
 * 
 * @author Oleg Filippov
 *
 */
public class StringUtils {

	/**
	 * Concatenates string from string-args using StringBuilder
	 * 
	 * @param strings to concatenate
	 * @return result string
	 */
	public static String buildString(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	/**
	 * Joins strings with the specified delimiter and wraps it with double quotes if needed
	 * 
	 * @param delimiter
	 * @param addDoubleQuotes
	 * @param strings to concatenate
	 * @return result string
	 */
	public static String buildString(char delimiter, boolean addDoubleQuotes, String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if (addDoubleQuotes)
				sb.append("\"");
			if (s != null && s.indexOf("\"") > -1)
				s = s.replaceAll("\"", "'");
			sb.append(s);
			if (addDoubleQuotes)
				sb.append("\"");
			sb.append(delimiter);
		}
		return sb.substring(0, sb.length()-1).toString();
	}
}
