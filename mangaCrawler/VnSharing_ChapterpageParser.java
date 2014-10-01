package mangaCrawler;

import helper.NetworkingFunctions;

import helper.Helper;

public class VnSharing_ChapterpageParser extends BaseChapterpageParser {
	public VnSharing_ChapterpageParser(String chapterLink) {
		super(chapterLink, "http://truyen.vnsharing.net/");
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
			imageLink = Helper.formatUrl(imageLink);
			
			System.out.println(i + " : " + imageLink);
			this.imageOrderToLinkMap.put(i, imageLink);
			
			i++;
		}

		return true;
	}

	public static void main(String[] args) {
//		VnSharing_ChapterpageParser crawler = new VnSharing_ChapterpageParser(
//				"http://truyen.vnsharing.net/Truyen/midori-no-hibi-tiep-/-VNS_davidtran94--Midori-no-Hibi---v01-c001?id=8368");
//
//		if (crawler.parseImageLinks()) {
//
//		}
	}
}
