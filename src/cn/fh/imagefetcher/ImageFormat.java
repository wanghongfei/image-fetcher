package cn.fh.imagefetcher;

public class ImageFormat {
	public static String getExtension(byte[] tenBytes) {
		if (tenBytes[1] == 'P' && tenBytes[2] == 'N' && tenBytes[3] == 'G') {
			return ".png";
		}
		
		if (tenBytes[6] == 'J' && tenBytes[7] == 'F' && tenBytes[8] == 'I' && tenBytes[9] == 'F') {
			return ".jpg";
		}
		
		if (tenBytes[0] == 'G' && tenBytes[1] == 'I' && tenBytes[2] == 'F') {
			return ".gif";
		}
		
		return ".unknown";
	}
}
