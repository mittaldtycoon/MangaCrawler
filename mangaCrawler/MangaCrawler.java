package mangaCrawler;

import helper.CrawlerType;
import helper.Helper;

import java.io.File;
import java.util.Map;

public class MangaCrawler {
	private static final String rootDir = "C:\\Skydrive\\Books\\";
	private static final char[] fileNameInvalidChar = { '\\', '/', ':', '*',
			'?', '<', '>', '|' };
	private static int waitTimeShort = 500;
	private static int waitTimeLong = 4000;

	private boolean downloadImageInParser = false;
	private String mangaLink = null;
	private BaseListpageParser listPageParser = null;
	private BaseChapterpageParser chapterPageParser = null;
	private CrawlerType.CRAWLER_TYPE type = CrawlerType.CRAWLER_TYPE.EMPTYTYPE;

	public MangaCrawler(String mangaLink) {
		this.mangaLink = mangaLink;

		if (this.mangaLink == null)
			return;

		// Decide the type of the crawler
		if (!this.populateType(this.mangaLink))
			return;
		
		// Instantiate the crawler object with the correponding type
		if (!this.populateCrawlerObject(this.type))
			return;
	}

	// Decide what type of crawler needed
	protected boolean populateType(String link) {
		if (link == null)
			return false;

		if (mangaLink.contains("http://www.mangareader.net/")) {
			this.type = CrawlerType.CRAWLER_TYPE.MANGAREADER;
		} else if (mangaLink.contains("http://truyen.vnsharing.net/")) {
			this.type = CrawlerType.CRAWLER_TYPE.VNSHARING;
		} else if (mangaLink.contains("http://www.mangahere.co/")) {
			this.type = CrawlerType.CRAWLER_TYPE.MANGAHERE;
		}

		return this.type != CrawlerType.CRAWLER_TYPE.EMPTYTYPE;
	}
	
	// Instantiate the crawler object with the correponding type
	protected boolean populateCrawlerObject(CrawlerType.CRAWLER_TYPE type) {
		if (type == CrawlerType.CRAWLER_TYPE.EMPTYTYPE)
			return false;

		switch (type) {
		case MANGAREADER:
			this.listPageParser = new MangaReader_ListpageParser(this.mangaLink);
			this.chapterPageParser = new MangaReader_ChapterpageParser(
					"http://www.mangareader.net/");
			this.downloadImageInParser = true;
			break;
		case VNSHARING:
			this.listPageParser = new VnSharing_ListpageParser(this.mangaLink);
			this.chapterPageParser = new VnSharing_ChapterpageParser(
					"http://truyen.vnsharing.net/");
			this.downloadImageInParser = false;
			break;
		case MANGAHERE:
			this.listPageParser = new MangaHere_ListpageParser(this.mangaLink);
			this.chapterPageParser = new MangaHere_ChapterpageParser(
					"http://www.mangahere.co/");
			this.downloadImageInParser = true;
			break;
		default:
			break;
		}
		
		return true;
	}

	// Sanitize the filename
	public static String cleanFileName(String fileName) {
		if (fileName == null)
			return null;

		char emptyChar = ' ';
		for (int i = 0; i < MangaCrawler.fileNameInvalidChar.length; i++) {
			fileName.replace(MangaCrawler.fileNameInvalidChar[i], emptyChar);
		}

		return fileName;
	}

	public boolean crawl() {
		if (this.mangaLink == null || this.listPageParser == null)
			return false;

		if (!listPageParser.parseInfo())
			return false;

		String mangaName = listPageParser.getMangaName();
		Map<String, String> chapterNameToLinkMap = listPageParser
				.getNameToLinkMap();

		if (chapterNameToLinkMap.size() == 0)
			return false;

		mangaName = MangaCrawler.cleanFileName(mangaName);
		String mangaDirectory = MangaCrawler.rootDir + mangaName;
		mangaDirectory = Helper.removeAccents(mangaDirectory);

		// Create the manga parent folder
		File mangaDir = new File(mangaDirectory);
		if (!mangaDir.exists() && mangaDir.mkdir()) {
			System.out.println("Directory: " + mangaDirectory + " created");
		} else if (!mangaDir.exists()) {
			return false;
		} else if (mangaDir.exists()) {
			System.out.println("Directory: " + mangaDirectory
					+ " already existed");
		}

		Helper.threadWait(MangaCrawler.waitTimeLong);

		// Iterate through all the chapter, create a direcotry for each and
		// download all the images to that directory
		for (Map.Entry<String, String> entry : chapterNameToLinkMap.entrySet()) {
			String chapterName = entry.getKey();
			String chapterLink = entry.getValue();

			chapterName = MangaCrawler.cleanFileName(chapterName);
			String chapterDirectory = mangaDirectory + "\\" + chapterName;
			chapterDirectory = Helper.removeAccents(chapterDirectory);

			// Create the chapter subfolder
			File chapterDir = new File(chapterDirectory);
			if (!chapterDir.exists() && chapterDir.mkdir()) {
				System.out.println("Subdirectory: " + chapterDirectory
						+ " created");

				if (!this.chapterPageParser.setChapterLink(chapterLink))
					continue;

				if (!this.chapterPageParser.parseImageLinks(
						this.downloadImageInParser, chapterDirectory))
					continue;

				if (!this.downloadImageInParser) {
					// Iterate through all the images and download them to the
					// subdirectory
					Map<Integer, String> imageOrderToLinkMap = this.chapterPageParser
							.getImageOrderToLinkMap();
					for (Map.Entry<Integer, String> imageEntry : imageOrderToLinkMap
							.entrySet()) {
						Integer imageOrder = imageEntry.getKey();
						String imageLink = imageEntry.getValue();

						if (!Helper.downloadAndStoreImage(imageOrder,
								imageLink, chapterDirectory))
							continue;

						Helper.threadWait(MangaCrawler.waitTimeShort);
					}
				}

				Helper.threadWait(MangaCrawler.waitTimeLong);
			} else {
				System.out.println("Subdirectory: " + chapterDirectory
						+ " already exists");
				continue;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		MangaCrawler crawler = new MangaCrawler(
				"http://www.mangareader.net/230/ichigo-100.html");

		if (crawler.crawl()) {

		}
	}
}
