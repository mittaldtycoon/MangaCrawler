package helper;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Helper {
	// Remove all the accent from a string
	public static String removeAccents(String text) {
		return text == null ? null : Normalizer.normalize(text, Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	// Format url (for e.g: " " => "%20")
	public static String formatUrl(String url) {
		if (url == null)
			return null;
		
		url = url.replaceAll(" ", "%20");
		
		return url;
	}
}
