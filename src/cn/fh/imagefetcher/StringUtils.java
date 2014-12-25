package cn.fh.imagefetcher;

public class StringUtils {
	public static String adjustUrl(String src, String context) {
		String newSrc = src;
		if (false == src.startsWith("http")) {
			if (src.startsWith("//")) {
				newSrc = "http:" + src;
			} else if (src.startsWith("/")) {
				newSrc = context + src;
			} else {
				newSrc = context + "/" + src;
			}

		}
		
		return newSrc;
	}
}
