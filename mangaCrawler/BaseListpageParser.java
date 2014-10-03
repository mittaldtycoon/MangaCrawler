package mangaCrawler;

import helper.CrawlerType;
import helper.NetworkingFunctions;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

public abstract class BaseListpageParser {
	protected final int numRetryDownloadPage = 2;
	protected String linkPrefix = null;

	protected Document doc = null;
	protected String mangaName = null;
	protected String listLink = null;
	protected Map<String, String> nameToLinkMap = null;

	protected BaseListpageParser(String listLink, String linkPrefix) {
		this.listLink = listLink;
		this.linkPrefix = linkPrefix;

		if (this.listLink == null || this.linkPrefix == null)
			return;

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

	// Parse all the information. This function will call parseMangaName and
	// parseLink
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
	protected abstract boolean parseMangaName();

	// Parse the chapter links
	protected abstract boolean parseLink();
	
	// Return the type of the crawler
	public abstract CrawlerType.CRAWLER_TYPE getType();
}
