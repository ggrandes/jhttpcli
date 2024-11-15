package org.javastack.jhttpcli;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Simple facade to manage a bunch of parameters of type
 * <p>
 * <code>application/x-www-form-urlencoded</code>
 */
public class URLCoder {
	public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private final StringBuilder sb = new StringBuilder();
	private final String enc;

	private URLCoder(final String enc) {
		this.enc = enc;
	}

	/**
	 * Create empty bag with default charset (UTF-8)
	 * 
	 * @return encoder instance
	 */
	public static URLCoder create() {
		return new URLCoder("UTF-8");
	}

	/**
	 * Create empty bag with especified charset
	 * 
	 * @param cs especified charset (UTF-8, ISO-8859-1, ...)
	 * @return encoder instance
	 */
	public static URLCoder create(final Charset cs) {
		return new URLCoder(cs.name());
	}

	/**
	 * Return a string encoded
	 * 
	 * @return string
	 */
	public String get() {
		return sb.toString();
	}

	/**
	 * Clear backed map
	 * 
	 * @return this
	 */
	public URLCoder clear() {
		sb.setLength(0);
		return this;
	}

	/**
	 * Add specified value to referenced key
	 * 
	 * @param key to be added
	 * @param value to be added
	 * @return this
	 */
	public URLCoder add(final String key, final String value) {
		if ((key == null) || key.isEmpty() //
				|| (value == null) || value.isEmpty()) {
			return this;
		}
		try {
			final int len = sb.length();
			if (len > 0) {
				if (sb.charAt(len - 1) != '&') {
					sb.append("&");
				}
			}
			sb.append(URLEncoder.encode(key, enc));
			sb.append("=");
			sb.append(URLEncoder.encode(value, enc));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
}
