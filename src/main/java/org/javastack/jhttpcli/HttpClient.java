package org.javastack.jhttpcli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The most simple http client facade
 */
public class HttpClient {
	protected boolean followRedirects = true;
	protected boolean useCaches = false;
	protected int connectionTimeoutMillis = 60_000;
	protected int readTimeoutMillis = 180_000;
	protected Consumer<HttpURLConnection> hookPreConnection = null;
	protected Consumer<HttpURLConnection> hookPostConnection = null;
	protected BiConsumer<Request, Response> hookDone = null;
	protected BiConsumer<Request, Response> hookFail = null;

	/**
	 * Follow Redirects? (default: true)
	 * 
	 * @param b boolean
	 * @return this
	 * @see HttpURLConnection#setInstanceFollowRedirects(boolean)
	 */
	public HttpClient setFollowRedirects(final boolean b) {
		this.followRedirects = b;
		return this;
	}

	/**
	 * Use Caches? (default: false)
	 * 
	 * @param b boolean
	 * @return this
	 * @see HttpURLConnection#setUseCaches(boolean)
	 */
	public HttpClient setUseCaches(final boolean b) {
		this.useCaches = b;
		return this;
	}

	/**
	 * Set connection timeout (millis) (default: 60 seconds)
	 * 
	 * @param millis connection timeout
	 * @return this
	 * @see HttpURLConnection#setConnectTimeout(int)
	 */
	public HttpClient setConnTimeoutMs(final int millis) {
		this.connectionTimeoutMillis = millis;
		return this;
	}

	/**
	 * Set read timeout (millis) (default: 180 seconds)
	 * 
	 * @param millis read timeout
	 * @return this
	 * @see HttpURLConnection#setReadTimeout(int)
	 */
	public HttpClient setReadTimeoutMs(final int millis) {
		this.readTimeoutMillis = millis;
		return this;
	}

	/**
	 * Set hook previous to connection
	 * 
	 * @param action The action to be performed before URLConnection is stablished
	 * @return this
	 */
	public HttpClient setHookPreConnection(final Consumer<HttpURLConnection> action) {
		this.hookPreConnection = action;
		return this;
	}

	/**
	 * Set hook after connection stablished
	 * 
	 * @param action The action to be performed after connection is stablished and before read body
	 * @return this
	 */
	public HttpClient setHookPostConnection(final Consumer<HttpURLConnection> action) {
		this.hookPostConnection = action;
		return this;
	}

	/**
	 * Set hook after execution finish (without exception)
	 * 
	 * @param action The action to be performed after request finish without errors
	 * @return this
	 */
	public HttpClient setHookDone(final BiConsumer<Request, Response> action) {
		this.hookDone = action;
		return this;
	}

	/**
	 * Set hook after execution fails (with exception)
	 * 
	 * @param action The action to be performed after execution fails (with exception)
	 * @return this
	 */
	public HttpClient setHookFail(final BiConsumer<Request, Response> action) {
		this.hookFail = action;
		return this;
	}

	/**
	 * Execute the request
	 * 
	 * @param req to execute
	 * @return response
	 */
	public Response execute(final Request req) {
		return execute(req, null);
	}

	/**
	 * Execute the request and write body response to a file
	 * 
	 * @param req to execute
	 * @param outFile to write body response
	 * @return response
	 */
	public Response execute(final Request req, //
			final File outFile) {
		Response.Code code = Response.Code.UNKNOWN;
		Map<String, List<String>> hdrs = Collections.emptyMap();
		Content body = Content.EMPTY;
		Exception ex = null;
		try {
			final HttpURLConnection conn = (HttpURLConnection) req.url.openConnection();
			conn.setRequestMethod(req.method.name());
			conn.setDoOutput(req.method.reqBody && (!req.body.isEmpty()));
			conn.setDoInput(req.method.resBody);
			conn.setInstanceFollowRedirects(followRedirects);
			conn.setUseCaches(useCaches);
			conn.setAllowUserInteraction(false);
			conn.setConnectTimeout(connectionTimeoutMillis);
			conn.setReadTimeout(readTimeoutMillis);
			if ((req.hdrs != null) && !req.hdrs.isEmpty()) {
				req.hdrs.forEach((key, list) -> {
					if ((list != null) && !list.isEmpty()) {
						list.forEach(value -> {
							conn.addRequestProperty(key, value);
						});
					}
				});
			}
			if ((outFile != null) && (outFile.lastModified() > 0L)) {
				conn.setIfModifiedSince(outFile.lastModified());
			}
			if (conn.getDoOutput()) {
				conn.setFixedLengthStreamingMode(req.body.size());
			}
			if (hookPreConnection != null) {
				hookPreConnection.accept(conn);
			}
			conn.connect();
			if (conn.getDoOutput()) {
				try (final InputStream xis = req.body.getInputStream(); //
						final OutputStream xos = conn.getOutputStream()) {
					IOUtil.transfer(xis, xos);
				}
			}
			final int httpCode = conn.getResponseCode();
			code = Response.Code.valueOf(httpCode);
			hdrs = conn.getHeaderFields();
			if (hookPostConnection != null) {
				hookPostConnection.accept(conn);
			}
			try (final InputStream is = conn.getInputStream()) {
				if (httpCode >= 200 && httpCode <= 299) {
					body = getContent(is, outFile);
				} else if (code == Response.Code.NOT_MODIFIED) {
					drop(is);
				} else {
					body = getContent(is, null);
				}
			}
		} catch (final Exception e) {
			ex = e;
		}
		final Response res = new Response(code, hdrs, body, ex);
		if (ex == null) {
			if (hookDone != null) {
				hookDone.accept(req, res);
			}
		} else {
			if (hookFail != null) {
				hookFail.accept(req, res);
			}
		}
		return res;
	}

	/**
	 * Consume inputstream (discard)
	 * 
	 * @param is to consume
	 * @throws IOException if error
	 */
	protected static final void drop(final InputStream is) throws IOException {
		final byte[] buf = new byte[4096];
		while (is.read(buf) != -1) {
			// drop is
		}
	}

	/**
	 * Retrieve content from InputStream and write to file if especified
	 * 
	 * @param is source of content
	 * @param outFile to write or in memory if null
	 * @return content
	 * @throws IOException if error
	 */
	protected static final Content getContent(final InputStream is, File outFile) throws IOException {
		final byte[] buf = new byte[4096];
		OutputStream os = null;
		Content body = Content.EMPTY;
		int limit = Integer.MAX_VALUE;
		if (outFile == null) {
			os = new ByteArrayOutputStream(4096);
			limit = 64 * 1024; // memory limit (64kb)
		} else {
			os = new FileOutputStream(outFile, false);
			body = Content.fromFile(outFile);
		}
		int len;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
			limit -= len;
			if (limit <= 0) {
				// Buffer overflow
				if (os instanceof ByteArrayOutputStream) {
					outFile = File.createTempFile("overflow-", ".tmp");
					final ByteArrayOutputStream osold = (ByteArrayOutputStream) os;
					os = new FileOutputStream(outFile, false);
					osold.writeTo(os);
					body = Content.fromFile(outFile);
				}
				limit = Integer.MAX_VALUE;
			}
		}
		if (os instanceof ByteArrayOutputStream) {
			final ByteArrayOutputStream osold = (ByteArrayOutputStream) os;
			body = Content.fromBytes(osold.toByteArray());
		}
		return body;
	}
}
