package org.javastack.jhttpcli;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Representation of HTTP body content
 */
public class Content {
	/**
	 * Constante representing empty content
	 */
	public static final Content EMPTY = new Content();

	/**
	 * Backed storage type
	 */
	public final Content.Type type;
	private final byte[] buf;
	private final File file;

	private Content() {
		this.type = Type.MEMORY;
		this.buf = null;
		this.file = null;
	}

	private Content(final byte[] buf) {
		this.type = Type.MEMORY;
		this.buf = buf;
		this.file = null;
	}

	private Content(final File file) {
		this.type = Type.FILE;
		this.buf = null;
		this.file = file;
	}

	/**
	 * Get content size
	 * 
	 * @return size in bytes
	 */
	public long size() {
		if (isEmpty()) {
			return 0;
		}
		switch (type) {
			case MEMORY:
				return ((buf != null) ? buf.length : 0);
			case FILE:
				return ((file != null) ? file.length() : 0);
		}
		throw new IllegalArgumentException("invalid type: " + type);
	}

	/**
	 * Is empty?
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		}
		switch (type) {
			case MEMORY:
				return ((buf == null) || (buf.length == 0));
			case FILE:
				return ((file == null) || (file.length() <= 0));
		}
		throw new IllegalArgumentException("invalid type: " + type);
	}

	/**
	 * Delete content and remove backed file from disk if content is persistent
	 */
	public void delete() {
		switch (type) {
			case MEMORY:
				if ((buf != null) && (buf.length > 0)) {
					Arrays.fill(buf, (byte) 0);
				}
				break;
			case FILE: {
				if (file != null) {
					try {
						file.delete();
					} catch (Exception ign) {
					}
				}
				break;
			}
		}
	}

	/**
	 * Get InputStream to content
	 * 
	 * @return inputstream to get content
	 * @throws IOException if error
	 */
	public InputStream getInputStream() throws IOException {
		if (isEmpty()) {
			return new ByteArrayInputStream(new byte[0]);
		}
		switch (type) {
			case MEMORY:
				return new ByteArrayInputStream(buf);
			case FILE:
				return new BufferedInputStream(new FileInputStream(file), 4096);
		}
		throw new IllegalArgumentException("invalid type: " + type);
	}

	/**
	 * Write content to outputstream
	 * 
	 * @param os destination stream
	 * @throws IOException if error
	 */
	public void writeTo(final OutputStream os) throws IOException {
		if (isEmpty()) {
			return;
		}
		switch (type) {
			case MEMORY:
				os.write(buf);
				return;
			case FILE:
				try (final FileInputStream is = new FileInputStream(file)) {
					IOUtil.transfer(is, os);
				}
				return;
		}
		throw new IllegalArgumentException("invalid type: " + type);
	}

	/**
	 * Wrapper content of a file
	 * 
	 * @param file backed file
	 * @return reference object
	 */
	public static Content fromFile(final File file) {
		return new Content(file);
	}

	/**
	 * Wrapper content of a byte array
	 * 
	 * @param value array
	 * @return reference object
	 */
	public static Content fromBytes(final byte[] value) {
		return new Content(value);
	}

	/**
	 * Wrapper content of a string with specified charset (UTF-8 / ISO-8859-1)
	 * 
	 * @param value of string
	 * @param cs source charset
	 * @return reference object
	 */
	public static Content fromString(final String value, final Charset cs) {
		return new Content(value.getBytes(cs));
	}

	/**
	 * Wrapper content of a string with UTF-8 charset
	 * 
	 * @param value of string
	 * @return reference object
	 */
	public static Content fromStringUTF8(final String value) {
		return fromString(value, StandardCharsets.UTF_8);
	}

	/**
	 * Wrapper content of a string with ISO-8859-1 charset
	 * 
	 * @param value of string
	 * @return reference object
	 */
	public static Content fromStringISOLatin1(final String value) {
		return fromString(value, StandardCharsets.ISO_8859_1);
	}

	/**
	 * Export content to string with especified charset
	 * 
	 * @param cs source encoding
	 * @return string
	 * @throws IOException if error
	 */
	public String toString(final Charset cs) throws IOException {
		if (isEmpty()) {
			return "";
		}
		switch (type) {
			case MEMORY:
				return new String(buf, cs);
			case FILE:
				try (final FileInputStream is = new FileInputStream(file)) {
					final long size = file.length();
					if (size > Integer.MAX_VALUE) {
						throw new BufferOverflowException();
					}
					final int len = (int) size;
					is.read(buf, 0, len);
					return new String(buf, 0, len, cs);
				}
		}
		throw new IllegalArgumentException("invalid type: " + type);
	}

	/**
	 * Export content to string with UTF-8 charset
	 * 
	 * @return string
	 * @throws IOException if error
	 */
	public String toStringUTF8() throws IOException {
		return toString(StandardCharsets.UTF_8);
	}

	/**
	 * Export content to string with ISO-8859-1 charset
	 * 
	 * @return string
	 * @throws IOException if error
	 */
	public String toStringISOLatin1() throws IOException {
		return toString(StandardCharsets.ISO_8859_1);
	}

	/**
	 * Export content to StringBuilder with especified charset
	 * 
	 * @param cs source encoding
	 * @return stringbuilder
	 * @throws IOException if error
	 */
	public StringBuilder toStringBuilder(final Charset cs) throws IOException {
		final long size = size();
		if (size > Integer.MAX_VALUE) {
			throw new BufferOverflowException();
		}
		final StringBuilder sb = new StringBuilder((int) size);
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(), cs))) {
			int c = 0;
			while ((c = br.read()) != -1) {
				sb.append((char) c);
			}
		}
		return sb;
	}

	/**
	 * Backed storage type
	 */
	public static enum Type {
		/**
		 * In memory content
		 */
		MEMORY,
		/**
		 * File stored content
		 */
		FILE;
	}
}