package org.javastack.jhttpcli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Representation of HTTP request
 */
public class Request {
	public final URL url;
	public final Request.Method method;
	public final Map<String, List<String>> hdrs;
	public final Content body;

	/**
	 * Create a HTTP request to especified URL, of type GET
	 * 
	 * @param url destination
	 */
	public Request(final URL url) {
		this(url, null, null, null);
	}

	/**
	 * Create a HTTP request to especified URL and Method
	 * 
	 * @param url destination
	 * @param method like GET, POST, DELETE,...
	 */
	public Request(final URL url, //
			final Request.Method method) {
		this(url, method, null, null);
	}

	/**
	 * Create a HTTP request to especified parameters
	 * 
	 * @param url destination
	 * @param method like GET, POST, DELETE,...
	 * @param hdrs map with keys and values
	 * @param body optional body
	 */
	public Request(final URL url, //
			final Request.Method method, //
			final Map<String, List<String>> hdrs, //
			final Content body) {
		this.url = url;
		this.method = ((method != null) ? method : Method.GET);
		this.hdrs = ((hdrs != null) ? hdrs : Collections.emptyMap());
		this.body = ((body != null) ? body : Content.EMPTY);
	}

	/**
	 * Useful for debuging
	 * 
	 * @param out destination stream
	 * @throws IOException if error
	 */
	public void dump(final PrintStream out) throws IOException {
		out.println("---DUMP");
		out.println(method + " " + url);
		if ((hdrs != null) && !hdrs.isEmpty()) {
			hdrs.forEach((k, v) -> {
				if ((k != null) && (v != null)) {
					out.println(k + "=" + v);
				}
			});
		}
		out.println("---BODY[" + body.type + "]");
		try (final InputStream is = body.getInputStream()) {
			final byte[] buf = new byte[4096];
			int len;
			while ((len = is.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		}
		out.println();
		out.println("---END");
	}

	/**
	 * Request Method
	 */
	public static enum Method {
		DELETE(false, true), //
		HEAD(false, false), //
		GET(false, true), //
		POST(true, true), //
		PUT(true, true), //
		;

		/**
		 * Can have request body?
		 */
		final boolean reqBody;
		/**
		 * Can have response body?
		 */
		final boolean resBody;

		Method(final boolean reqBody, //
				final boolean resBody) {
			this.reqBody = reqBody;
			this.resBody = resBody;
		}
	}
}