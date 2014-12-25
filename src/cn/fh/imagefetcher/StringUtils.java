package cn.fh.imagefetcher;

public class StringUtils {
	/**
	 * Some urls of image will be in the form of '//cat.gif', 'http://server/dog.gif' or 'parent/top/kitty.png'.
	 * Convert them to the form of 'http://hostname//image'
	 * @param src
	 * @param context
	 * @return
	 */
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
