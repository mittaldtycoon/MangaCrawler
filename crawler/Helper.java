package crawler;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Helper {
	// Remove all the accent from a string
	public static String removeAccents(String text) {
		return text == null ? null : Normalizer.normalize(text, Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}
