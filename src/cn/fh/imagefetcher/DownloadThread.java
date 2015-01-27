package cn.fh.imagefetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DownloadThread implements Runnable {
	private String urlString;
	private URL url;

	public DownloadThread(String urlString) {
		this.urlString = urlString;
	}

	@Override
	public void run() {
		try {
			this.url = new URL(urlString);
			URLConnection conn = url.openConnection();
			int len = conn.getContentLength();
			saveImage(conn.getInputStream(), len);

		} catch (MalformedURLException e) {
			System.out.println("[ERROR] Invalid url:" + this.urlString);
		} catch (IOException e) {
			System.out.println("[ERROR] cannot create new file");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/*private InputStream connect() throws MalformedURLException, IOException {
		this.url = new URL(this.urlString);
		
		return url.openStream();
	}*/

	private void saveImage(InputStream inStream, int fileSize) throws IOException {
		System.out.println("downloading: " + urlString);

		// determine image format and generate file name
		byte[] tenBytes = new byte[10];
		inStream.read(tenBytes);
		String fileName = ImageFetcher.nextSequence() + ImageFormat.getExtension(tenBytes);

		// save image
		// the NIO way
		ByteBuffer buf = ByteBuffer.allocate(1024 * 100); // 100KB
		//ByteBuffer buf = ByteBuffer.allocate(fileSize);
		buf.put(tenBytes);
		ReadableByteChannel originalChan = Channels.newChannel(inStream);
		try (FileChannel fChan = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
			while (originalChan.read(buf) != -1) {
				buf.flip();
				fChan.write(buf);
				buf.clear();
			}

			//fChan.transferFrom(originalChan, 0, Integer.MAX_VALUE);
		}
		
		System.out.println("[" + fileName + "] done");
		//System.out.println("[] done");

	}

}
