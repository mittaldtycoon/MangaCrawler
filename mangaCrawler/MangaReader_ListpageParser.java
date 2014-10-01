package mangaCrawler;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MangaReader_ListpageParser extends BaseListpageParser {
	public MangaReader_ListpageParser(String listLink) {
		super(listLink, "http://www.mangareader.net/");
	}

	// Parse the manga name
	protected boolean parseMangaName() {
		if (this.listLink == null || this.doc == null)
			return false;

		Elements mangaNameElems = this.doc.select("h1");

		if (mangaNameElems.size() != 1)
			return false;

		this.mangaName = mangaNameElems.get(0).text();
		System.out.println("Manga name : " + this.mangaName);

		return true;
	}

	// Parse the chapter links
	protected boolean parseLink() {
		if (this.listLink == null || this.doc == null)
			return false;

		Elements listingElems = this.doc.select("table[id=listing]");

		// Return if the page has no listing
		if (listingElems.size() != 1) {
			System.out.println("No listing element");
			return false;
		}

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

				this.nameToLinkMap.put(chapterName,
						absoluteChapterUrl.toString());
				System.out.println(chapterName + " : " + absoluteChapterUrl);
			} catch (MalformedURLException e) {
				continue;
			}
		}

		return true;
	}

	public static void main(String[] args) {
//		MangaReader_ListpageParser crawler = new MangaReader_ListpageParser(
//				"http://www.mangareader.net/711/unbalance-x-unbalance.html");
//
//		if (crawler.parseInfo()) {
//
//		}
	}
}
