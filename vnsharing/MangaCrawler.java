package vnsharing;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class MangaCrawler {
	private static final String linkPrefix = "http://truyen.vnsharing.net/";
	private static final String rootDir = "C:\\Skydrive\\Books\\";
	private static final char[] fileNameInvalidChar = { '\\', '/', ':', '*',
			'?', '<', '>', '|' };
	private static int waitTimeShort = 500;
	private static int waitTimeLong = 4000;

	private String mangaLink = null;

	public MangaCrawler(String mangaLink) {
		if (mangaLink == null)
			return;

		if (!mangaLink.contains(MangaCrawler.linkPrefix))
			return;

		this.mangaLink = mangaLink;
	}

	public static String cleanFileName(String fileName) {
		if (fileName == null)
			return null;

		char emptyChar = ' ';
		for (int i = 0; i < MangaCrawler.fileNameInvalidChar.length; i++) {
			fileName.replace(MangaCrawler.fileNameInvalidChar[i], emptyChar);
		}

		return fileName;
	}

	public void threadWait (long milisec) {
		try {
			Thread.sleep(milisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean crawl() {
		if (this.mangaLink == null)
			return false;

		ListpageParser listPageParser = new ListpageParser(this.mangaLink);

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
			System.out.println("Directory: " + mangaDirectory + " already existed");
		}
		
		this.threadWait(MangaCrawler.waitTimeLong);
		
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
				System.out.println("Subdirectory: " + chapterDirectory + " created");
				
				ChapterpageParser chapterParser = new ChapterpageParser(
						chapterLink);

				if (!chapterParser.parseImageLinks())
					continue;

				// Iterate through all the images and download them to the
				// subdirectory
				Map<Integer, String> imageOrderToLinkMap = chapterParser
						.getImageOrderToLinkMap();
				for (Map.Entry<Integer, String> imageEntry : imageOrderToLinkMap
						.entrySet()) {
					Integer imageOrder = imageEntry.getKey();
					String imageLink = imageEntry.getValue();

					if (imageOrder == null || imageLink == null)
						continue;
					
					String imageLocation = chapterDirectory + "\\" + imageOrder + ".jpg";
					if (!NetworkingFunctions.downloadImage(imageLink, imageLocation))
						continue;
					
					this.threadWait(MangaCrawler.waitTimeShort);
					System.out.println("Download "+imageLink+" to "+imageLocation);
				}
				this.threadWait(MangaCrawler.waitTimeLong);
			} else {
				System.out.println("Subdirectory: " + chapterDirectory + " already exists");
				continue;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		MangaCrawler crawler = new MangaCrawler(
				"http://truyen.vnsharing.net/Truyen/midori-no-hibi-tiep-");

		if (crawler.crawl()) {

		}
	}
}
