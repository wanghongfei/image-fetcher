package cn.fh.imagefetcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImageFetcher {
	private String url;
	private List<String> urlList = new ArrayList<String>();

	public static int imageNo = 1;

	public ImageFetcher(String url) {
		this.url = url;
		
	}

	public static void main(String[] args) {
		ImageFetcher imgFetcher = new ImageFetcher(args[0]);
		imgFetcher.startDownload();
		
	}

	public void startDownload() {
		try {
			parse();
		} catch (IOException e) {
			System.out.println("connection failed");
		}

		this.urlList.stream().forEach( (src) -> {
			try {
				URL u = new URL(src);

				System.out.println("downloading: " + src);
				String fileName = saveImage(u.openStream());
				System.out.println("[" + fileName + "] done");

			} catch (MalformedURLException e) {
				// not a valid URL
				System.out.println("unknown:" + src);
			} catch (IOException e) {
				System.out.println("resource unreachable: " + src);
			}
		});
	}

	/**
	 * Parse html document and find out every link of '<img>' tag
	 * @throws IOException
	 */
	private void parse() throws IOException {
		Document doc = Jsoup.parse(getHtml(url));
		Elements imgElements = doc.select("img");

		for (Element tag : imgElements) {
			String src = tag.attr("src");
			// this <img> tag does not have 'src' attribute
			if (null == src) {
				continue;
			}

			src = StringUtils.adjustUrl(src, this.url);

			System.out.println("image detected: " + src);
			this.urlList.add(src);
		}
	}

	/**
	 * Write image to file system
	 * @param inStream
	 * @return The name of this image file
	 * @throws IOException
	 */
	private String saveImage(InputStream inStream) throws IOException {
		// determine image format
		byte[] tenBytes = new byte[10];
		inStream.read(tenBytes);
		String extension = ImageFormat.getExtension(tenBytes);

		// save image
		// generate file name
		String fileName = nextSequence() + extension;
		FileOutputStream fOut = new FileOutputStream(fileName);
		fOut.write(tenBytes);
		byte[] buf = new byte[4096];
		int len;
		while ((len = inStream.read(buf)) != -1) {
			fOut.write(buf, 0, len);
		}

		fOut.close();
		inStream.close();

		return fileName;
	}

	public static int nextSequence() {
		return ImageFetcher.imageNo++;
	}

	/**
	 * Fetch html code from target URL
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getHtml(String url) throws IOException {
		URL u = new URL(url);
		
		System.out.println("connecting...");

		BufferedInputStream bis = new BufferedInputStream(u.openStream());
		InputStreamReader reader = new InputStreamReader(bis);
		BufferedReader bufReader = new BufferedReader(reader);

		String line = null;
		StringBuilder bufHtml = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			bufHtml.append(line);
		}

		bis.close();

		return bufHtml.toString();
	}
}
