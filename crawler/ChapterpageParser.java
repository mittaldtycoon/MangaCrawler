package crawler;

import java.util.HashMap;
import java.util.Map;
import org.jsoup.nodes.Document;

public class ChapterpageParser {
	private final int numRetryDownloadPage = 2;
	private final String linkPrefix = "http://truyen.vnsharing.net/";

	private Document doc = null;
	private String chapterLink = null;
	private Map<Integer, String> imageOrderToLinkMap = null;

	public ChapterpageParser(String chapterLink) {
		if (chapterLink == null)
			return;

		this.chapterLink = chapterLink;

		if (!this.chapterLink.contains(this.linkPrefix))
			return;

		this.imageOrderToLinkMap = new HashMap<Integer, String>();
	}

	public Map<Integer, String> getImageOrderToLinkMap() {
		return this.imageOrderToLinkMap;
	}

	public boolean parseImageLinks() {
		if (this.chapterLink == null)
			return false;

		// Download the listing page html content
		this.doc = NetworkingFunctions.downloadHtmlContent(this.chapterLink,
				this.numRetryDownloadPage);

		if (this.doc == null)
			return false;

		String htmlContent = this.doc.outerHtml();
		String imagePrefix = "lstImages.push(\"";
		
		int i = 1;
		while (true) {
			int startIndex = htmlContent.indexOf(imagePrefix);
			if (startIndex == -1)
				break;
			
			htmlContent = htmlContent.substring(startIndex+imagePrefix.length());
			
			int endIndex = htmlContent.indexOf("\"");
			if (endIndex == -1)
				break;
			
			String imageLink = htmlContent.substring(0, endIndex);
			
			System.out.println(i + " : " + imageLink);
			this.imageOrderToLinkMap.put(i, imageLink);
			
			i++;
		}

		return true;
	}

	public static void main(String[] args) {
		ChapterpageParser crawler = new ChapterpageParser(
				"http://truyen.vnsharing.net/Truyen/Ai-Kora/Parts-118?id=17609");

		if (crawler.parseImageLinks()) {

		}
	}
}
