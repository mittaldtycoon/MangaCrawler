package mangaCrawler;

import helper.NetworkingFunctions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VnSharing_ListpageParser {
	private final int numRetryDownloadPage = 2;
	private final String linkPrefix = "http://truyen.vnsharing.net/";

	private Document doc = null;
	private String mangaName = null;
	private String listLink = null;
	private Map<String, String> nameToLinkMap = null;

	public VnSharing_ListpageParser(String listLink) {
		if (listLink == null)
			return;

		this.listLink = listLink;

		if (!this.listLink.contains(this.linkPrefix))
			return;
		
		this.nameToLinkMap = new HashMap<String, String>();
	}

	public Map<String, String> getNameToLinkMap() {
		return this.nameToLinkMap;
	}
	
	public String getMangaName() {
		return this.mangaName;
	}

	public boolean parseInfo() {
		if (this.listLink == null)
			return false;

		// Download the listing page html content
		this.doc = NetworkingFunctions.downloadHtmlContent(this.listLink,
				this.numRetryDownloadPage);

		if (this.doc == null)
			return false;

		// Parse the manga name
		if (!this.parseMangaName())
			return false;

		// Parse the chapter links
		if (!this.parseLink())
			return false;

		return true;
	}

	// Parse the manga name
	private boolean parseMangaName() {
		if (this.listLink == null)
			return false;

		if (this.doc == null)
			return false;

		Elements mangaNameElems = this.doc.select("a[class=bigchar]");

		if (mangaNameElems.size() != 1)
			return false;

		this.mangaName = mangaNameElems.get(0).text();
		System.out.println("Manga name : " + this.mangaName);

		return true;
	}

	// Parse the chapter links
	private boolean parseLink() {
		if (this.listLink == null)
			return false;

		if (this.doc == null)
			return false;

		Elements listingElems = this.doc.select("table[class=listing]");

		// Return if the page has no listing
		if (listingElems.size() != 1) {
			System.out.println("No listing element");
			return false;
		}

		// US elem is the first one
		Element listingElem = listingElems.get(0);

		Elements chapterElems = listingElem.select("tr");

		if (chapterElems.size() == 0)
			return false;

		int numChapters = chapterElems.size();

		// Iterate through each state listed in craiglist
		for (int i = 0; i < numChapters; i++) {
			Elements chapterLinkNameElements = chapterElems.get(i).select("a");

			if (chapterLinkNameElements.size() <= 0)
				continue;

			String chapterLink = chapterLinkNameElements.get(0).attr("href")
					.toString();

			try {
				URL chapterUrl = new URL(this.linkPrefix);
				URL absoluteChapterUrl = new URL(chapterUrl, chapterLink);
				String chapterName = chapterLinkNameElements.get(0).text();

				this.nameToLinkMap.put(chapterName, absoluteChapterUrl.toString());
				System.out.println(chapterName + " : " + absoluteChapterUrl);
			} catch (MalformedURLException e) {
				continue;
			}
		}

		return true;
	}

	public static void main(String[] args) {
//		VnSharing_ListpageParser crawler = new VnSharing_ListpageParser(
//				"http://truyen.vnsharing.net/Truyen/Ai-Kora");
//
//		if (crawler.parseInfo()) {
//
//		}
	}
}
