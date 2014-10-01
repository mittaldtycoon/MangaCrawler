package mangaCrawler;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

public abstract class BaseChapterpageParser {
	protected final int numRetryDownloadPage = 2;
	protected String linkPrefix = null;

	protected Document doc = null;
	protected String chapterLink = null;
	protected Map<Integer, String> imageOrderToLinkMap = null;

	protected BaseChapterpageParser(String chapterLink, String linkPrefix) {
		this.chapterLink = chapterLink;
		this.linkPrefix = linkPrefix;
		
		if (this.linkPrefix == null || this.chapterLink == null)
			return;

		if (!this.chapterLink.contains(this.linkPrefix))
			return;

		this.imageOrderToLinkMap = new HashMap<Integer, String>();
	}
	
	// Map between the image order in the chapter and its link
	protected Map<Integer, String> getImageOrderToLinkMap() {
		return this.imageOrderToLinkMap;
	}

	protected abstract boolean parseImageLinks();
}
