package net.foreworld.yx.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.StringUtils;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
public class GZIPUtil {

	public static final String GZIP_ENCODE_UTF_8 = "UTF-8";

	public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] compress(String str) {
		return compress(str, GZIP_ENCODE_UTF_8);
	}

	/**
	 * 
	 * @param str
	 * @param encoding
	 * @return
	 */
	public static byte[] compress(String str, String encoding) {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes(encoding));
			gzip.close();
		} catch (IOException e) {
		}
		return out.toByteArray();
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] uncompress(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
		}

		return out.toByteArray();
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String uncompressToString(byte[] bytes) {
		return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
	}

	/**
	 * 
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public static String uncompressToString(byte[] bytes, String encoding) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			GZIPInputStream ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			return out.toString(encoding);
		} catch (IOException e) {
		}
		return null;
	}

	public static void main(String[] args) {
		// String str =
		// "%5B%7B%22lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221";
		String str = "%5B%7B%2s啥宋德福2lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221啥h打饭";
		System.out.println("原长度：" + str.length());

		byte[] b = GZIPUtil.compress(str);

		System.out.println(b.toString());

		System.out.println("压缩后字符串：" + b.toString().length());
		System.out.println("解压缩后字符串：" + StringUtils.newStringUtf8(GZIPUtil.uncompress(b)));
		System.out.println("解压缩后字符串：" + GZIPUtil.uncompressToString(b));
	}
}