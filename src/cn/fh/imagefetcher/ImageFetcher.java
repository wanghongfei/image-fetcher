package cn.fh.imagefetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
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
	public static int BUF_SIZE = 1024 * 10 * 10; //100K

	public ImageFetcher(String url) {
		this.url = url;
		
	}

	/**
	 * Read URL from the first command-line parameter
	 * @param args
	 */
	public static void main(String[] args) {
		ImageFetcher imgFetcher = new ImageFetcher(args[0]);
		//ImageFetcher imgFetcher = new ImageFetcher("http://www.baidu.com");
		//ImageFetcher imgFetcher = new ImageFetcher("http://www.qq.com");
		imgFetcher.startDownload();
		
	}

	public void startDownload() {
		try {
			String html = getHtml(url);
			System.out.println(html);
			System.out.println("start parsing...");
			parse(html);
		} catch (IOException e) {
			System.out.println("connection failed");
		}

		this.urlList.stream().forEach( (src) -> {
			saveImage(new DownloadThread(src));
		});
	}

	/**
	 * Parse html document and find out every link of '<img>' tag
	 * @throws IOException
	 */
	private void parse(String html) throws IOException {
		Document doc = Jsoup.parse(html);
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
	 * Write image to file system.
	 * 
	 * <p> Never close the InputStream
	 * 
	 * @param inStream
	 * @return The name of this image file
	 * @throws IOException
	 */
	//private String saveImage(InputStream inStream) throws IOException {
	private void saveImage(Runnable run) {
		new Thread(run).start();
	}

	public static Integer nextSequence() {
		return ImageFetcher.imageNo++;
	}
	
	private String readWithoutLength(InputStream in) throws IOException {
		byte[] buf = new byte[BUF_SIZE]; // 100K
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while ( (len = in.read(buf)) != -1 ) {
			sb.append(new String(buf, 0, len));
		}
		
		return sb.toString();
	}
	
	private String readWithLength(InputStream in, final int LEN) throws IOException {
		byte[] buf = new byte[LEN];
		ByteBuffer byteBuf = ByteBuffer.allocate(LEN);
		int len = 0;
		while ( (len = in.read(buf)) != -1 ) {
			byteBuf.put(buf, 0, len);
		}	
		
		return new String(byteBuf.array());
	}

	/**
	 * Fetch html code from target URL
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getHtml(String url) throws IOException {
		URL u = new URL(url);
		
		System.out.println("connecting to [" + url + "]");
		URLConnection conn = u.openConnection();
		conn.connect();
		System.out.println("connected");
		
		int fileSize = conn.getContentLength();
		System.out.println("file size:" + fileSize);
		System.out.println("downloading html...");
		InputStream inStream = conn.getInputStream();
		if (-1 == fileSize) {
			return readWithoutLength(inStream);
		} else {
			return readWithLength(inStream, fileSize);
		}

	}
}
