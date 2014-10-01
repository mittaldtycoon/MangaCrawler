package mangaCrawler;

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
	protected abstract boolean parseInfo();

	// Parse the manga name
	protected abstract boolean parseMangaName();

	// Parse the chapter links
	protected abstract boolean parseLink();
}
