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

	// Put the current thread for wait for xxx milisec
	public static void threadWait(long milisec) {
		try {
			Thread.sleep(milisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String extractSubString(String htmlContent, String prefix,
			String surfix) {
		if (htmlContent == null)
			return null;

		int nextPageImageIndex = htmlContent.indexOf(prefix);
		String imageLink = null;

		if (nextPageImageIndex != -1) {
			imageLink = htmlContent.substring(nextPageImageIndex
					+ prefix.length());

			nextPageImageIndex = imageLink.indexOf(surfix);
			if (nextPageImageIndex == -1)
				return null;

			imageLink = imageLink.substring(0, nextPageImageIndex);
		} else
			return null;

		imageLink = imageLink.trim();
		return imageLink;
	}

	// Download image and store it in folder
	public static boolean downloadAndStoreImage(Integer imageOrder,
			String imageLink, String chapterDirectory) {
		if (imageOrder == null || imageLink == null || chapterDirectory == null)
			return false;

		String imageLocation = chapterDirectory + "\\" + imageOrder + ".jpg";
		if (!NetworkingFunctions.downloadImage(imageLink, imageLocation))
			return false;

		System.out.println("Download " + imageLink + " to " + imageLocation);

		return true;
	}
}
