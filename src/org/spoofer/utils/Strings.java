package org.spoofer.utils;

public class Strings {

	private Strings() {
		super();
	}

	/**
	 * Checks if the given String is not null and has more than white space in it.
	 * Specifically s must not be null and have at least a single, non white space, character in it
	 * to return true
	 * 
	 * @param s the String to test
	 * @return true is teh string contains text.
	 */
	public static boolean hasText(String s) {
		return null != s && !s.trim().isEmpty();
	}

	/**
	 * returns the given string with the first letter a capital letter.
	 * If the first letter is already a capital or the given string empty or null,
	 *  the returned string will be the given string.
	 * 
	 * @param s the string to capilaise
	 * @return the given string with its first character in title case, or if given string is null or empty, returns given string.
	 */
	public static String getCapitalised(String s) {
		if (!hasText(s))
			return s;

		char firstCh = s.charAt(0);
		return Character.isLowerCase(firstCh) ? 
				new StringBuilder().append(Character.toTitleCase(firstCh)).append(s.substring(1)).toString() : s;
	}

	

	/**
	 * Inserts the given parameters into the place holders in teh given string.
	 * Place holders are '?' questian marks
	 * @param s
	 * @param parameters
	 * @return
	 */
	public static String insertParameters(String s, String[] parameters) {

		if (null != parameters && parameters.length > 0) {

			for (String parameter : parameters)
				s = s.replaceFirst("\\?", parameter);
		}
		return s;
	}

	private static final String DEFAULT_ARRAY_DELIMITER = ", ";

	/**
	 * returns the given array of Strings as a single, delimited string.
	 * Adds each element of the array to the end of the last to form a single string.
	 * Strings use the default delimiter of a comma ', ', followed by a single space.
	 * 
	 * @param array  The array of strings to add
	 * @return a single string, starting with the first Element of the array, followed by the delimiter and the next element and so on.
	 * Delimiters are placed after every element, except the last element.
	 */
	public static String arrayToString(String[] array) {
		return arrayToString(array, DEFAULT_ARRAY_DELIMITER);
	}

	/**
	 * returns the given array of Strings as a single, delimited string.
	 * Adds each element of the array to the end of the last to form a single string.
	 * 
	 * @param array  The array of strings to add
	 * @param delimiter The delimiter to place between each element
	 * @return a single string, starting with the first Element of the array, followed by the delimiter and the next element and so on.
	 * Delimiters are placed after every element, except the last element.
	 */
	public static String arrayToString(String[] array, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (String s : array) {
			if (sb.length() > 0)
				sb.append(delimiter);
			sb.append(s);
		}

		return sb.toString();
	}

	public static String[] stringToArray(String s) {
		return stringToArray(s, DEFAULT_ARRAY_DELIMITER);
	}
	public static String[] stringToArray(String s, String delimiter) {
		return s.split(delimiter);
	}
}
