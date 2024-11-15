package org.javastack.jhttpcli;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	/**
	 * Transfer from InputStream to OutputStream
	 * 
	 * @param is input
	 * @param os output
	 * @throws IOException if error
	 */
	public static final void transfer(final InputStream is, //
			final OutputStream os) throws IOException {
		final byte[] buf = new byte[4096];
		int len;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
	}

	/**
	 * Close unconditional
	 * 
	 * @param c to close
	 */
	public static final void closeSilent(final Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Throwable ign) {
			}
		}
	}
}
