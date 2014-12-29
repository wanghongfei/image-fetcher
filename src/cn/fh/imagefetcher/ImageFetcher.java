package cn.fh.imagefetcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

	/**
	 * Read URL from the first command-line parameter
	 * @param args
	 */
	public static void main(String[] args) {
		ImageFetcher imgFetcher = new ImageFetcher(args[0]);
		//ImageFetcher imgFetcher = new ImageFetcher("http://www.baidu.com");
		imgFetcher.startDownload();
		
	}

	public void startDownload() {
		try {
			parse();
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

		// determine image format
/*		byte[] tenBytes = new byte[10];
		inStream.read(tenBytes);
		String extension = ImageFormat.getExtension(tenBytes);

		// generate file name
		String fileName = nextSequence() + extension;

		// save image
		// the NIO way
		ByteBuffer buf = ByteBuffer.wrap(tenBytes);
		buf.put(tenBytes);
		ReadableByteChannel originalChan = Channels.newChannel(inStream);
		try ( FileChannel fChan = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE) ) {
			fChan.write(buf);
			fChan.transferFrom(originalChan, 0, Integer.MAX_VALUE);
		}
*/

/*		FileOutputStream fOut = new FileOutputStream(fileName);
		fOut.write(tenBytes);
		byte[] buf = new byte[4096];
		int len;
		while ((len = inStream.read(buf)) != -1) {
			fOut.write(buf, 0, len);
		}

		fOut.close();*/

		//inStream.close(); // don't need it anymore

		//return fileName;
	}

	public static Integer nextSequence() {
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
		
		System.out.println("connecting to [" + url + "]");

		BufferedInputStream bis = new BufferedInputStream(u.openStream());
		InputStreamReader reader = new InputStreamReader(bis);
		BufferedReader bufReader = new BufferedReader(reader);

		System.out.println("connected");
		String line = null;
		StringBuilder bufHtml = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			bufHtml.append(line);
		}

		bis.close();

		return bufHtml.toString();
	}
}
