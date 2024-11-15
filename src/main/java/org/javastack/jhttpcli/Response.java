package org.javastack.jhttpcli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Response {
	/**
	 * HTTP Response code
	 * 
	 * @see Response.Code
	 */
	public final Response.Code code;
	/**
	 * HTTP Response headers
	 * 
	 * @see java.net.URLConnection#getHeaderFields()
	 */
	public final Map<String, List<String>> hdrs;
	/**
	 * HTTP Response body
	 */
	public final Content body;
	/**
	 * Java Exception if error
	 */
	public final Exception exception;

	/**
	 * Create empty response, without headers or body, only http code
	 * 
	 * @param code http
	 */
	public Response(final Response.Code code) {
		this(code, null, null, null);
	}

	/**
	 * Create response with code, headers and body
	 * 
	 * @param code http
	 * @param hdrs headers
	 * @param body content
	 * @param exception error
	 */
	public Response(final Response.Code code, //
			final Map<String, List<String>> hdrs, //
			final Content body, //
			final Exception exception) {
		this.code = ((code != null) ? code : Code.UNKNOWN);
		this.hdrs = ((hdrs != null) ? hdrs : Collections.emptyMap());
		this.body = ((body != null) ? body : Content.EMPTY);
		this.exception = exception;
	}

	/**
	 * Useful for debuging
	 * 
	 * @param out destination stream
	 * @throws IOException if error
	 */
	public void dump(final PrintStream out) throws IOException {
		out.println("---DUMP");
		out.println("HTTP " + code.code + " " + code.name());
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
	 * HTTP Response code
	 */
	public static enum Code {
		/**
		 * HTTP Status-Code 200: OK.
		 */
		OK(200), //

		/**
		 * HTTP Status-Code 201: Created.
		 */
		CREATED(201), //

		/**
		 * HTTP Status-Code 202: Accepted.
		 */
		ACCEPTED(202), //

		/**
		 * HTTP Status-Code 203: Non-Authoritative Information.
		 */
		NOT_AUTHORITATIVE(203), //

		/**
		 * HTTP Status-Code 204: No Content.
		 */
		NO_CONTENT(204), //

		/**
		 * HTTP Status-Code 205: Reset Content.
		 */
		RESET(205), //

		/**
		 * HTTP Status-Code 206: Partial Content.
		 */
		PARTIAL(206), //

		/* 3XX: relocation/redirect */

		/**
		 * HTTP Status-Code 300: Multiple Choices.
		 */
		MULT_CHOICE(300), //

		/**
		 * HTTP Status-Code 301: Moved Permanently.
		 */
		MOVED_PERM(301), //

		/**
		 * HTTP Status-Code 302: Temporary Redirect.
		 */
		MOVED_TEMP(302), //

		/**
		 * HTTP Status-Code 303: See Other.
		 */
		SEE_OTHER(303), //

		/**
		 * HTTP Status-Code 304: Not Modified.
		 */
		NOT_MODIFIED(304), //

		/**
		 * HTTP Status-Code 305: Use Proxy.
		 */
		USE_PROXY(305), //

		/* 4XX: client error */

		/**
		 * HTTP Status-Code 400: Bad Request.
		 */
		BAD_REQUEST(400), //

		/**
		 * HTTP Status-Code 401: Unauthorized.
		 */
		UNAUTHORIZED(401), //

		/**
		 * HTTP Status-Code 402: Payment Required.
		 */
		PAYMENT_REQUIRED(402), //

		/**
		 * HTTP Status-Code 403: Forbidden.
		 */
		FORBIDDEN(403), //

		/**
		 * HTTP Status-Code 404: Not Found.
		 */
		NOT_FOUND(404), //

		/**
		 * HTTP Status-Code 405: Method Not Allowed.
		 */
		BAD_METHOD(405), //

		/**
		 * HTTP Status-Code 406: Not Acceptable.
		 */
		NOT_ACCEPTABLE(406), //

		/**
		 * HTTP Status-Code 407: Proxy Authentication Required.
		 */
		PROXY_AUTH(407), //

		/**
		 * HTTP Status-Code 408: Request Time-Out.
		 */
		CLIENT_TIMEOUT(408), //

		/**
		 * HTTP Status-Code 409: Conflict.
		 */
		CONFLICT(409), //

		/**
		 * HTTP Status-Code 410: Gone.
		 */
		GONE(410), //

		/**
		 * HTTP Status-Code 411: Length Required.
		 */
		LENGTH_REQUIRED(411), //

		/**
		 * HTTP Status-Code 412: Precondition Failed.
		 */
		PRECON_FAILED(412), //

		/**
		 * HTTP Status-Code 413: Request Entity Too Large.
		 */
		ENTITY_TOO_LARGE(413), //

		/**
		 * HTTP Status-Code 414: Request-URI Too Large.
		 */
		REQ_TOO_LONG(414), //

		/**
		 * HTTP Status-Code 415: Unsupported Media Type.
		 */
		UNSUPPORTED_TYPE(415), //

		/* 5XX: server error */

		/**
		 * HTTP Status-Code 500: Internal Server Error.
		 */
		INTERNAL_ERROR(500), //

		/**
		 * HTTP Status-Code 501: Not Implemented.
		 */
		NOT_IMPLEMENTED(501), //

		/**
		 * HTTP Status-Code 502: Bad Gateway.
		 */
		BAD_GATEWAY(502), //

		/**
		 * HTTP Status-Code 503: Service Unavailable.
		 */
		UNAVAILABLE(503), //

		/**
		 * HTTP Status-Code 504: Gateway Timeout.
		 */
		GATEWAY_TIMEOUT(504), //

		/**
		 * HTTP Status-Code 505: HTTP Version Not Supported.
		 */
		VERSION(505), //

		/**
		 * Unknown HTTP Code
		 */
		UNKNOWN(-1); //

		public final int code;

		Code(final int code) {
			this.code = code;
		}

		/**
		 * Return code from http-code
		 * 
		 * @param code numerical (200-599)
		 * @return {@link Response.Code}
		 */
		public static Response.Code valueOf(final int code) {
			switch (code) {
				case 200:
					return OK;
				case 201:
					return CREATED;
				case 202:
					return ACCEPTED;
				case 203:
					return NOT_AUTHORITATIVE;
				case 204:
					return NO_CONTENT;
				case 205:
					return RESET;
				case 206:
					return PARTIAL;
				case 300:
					return MULT_CHOICE;
				case 301:
					return MOVED_PERM;
				case 302:
					return MOVED_TEMP;
				case 303:
					return SEE_OTHER;
				case 304:
					return NOT_MODIFIED;
				case 305:
					return USE_PROXY;
				case 400:
					return BAD_REQUEST;
				case 401:
					return UNAUTHORIZED;
				case 402:
					return PAYMENT_REQUIRED;
				case 403:
					return FORBIDDEN;
				case 404:
					return NOT_FOUND;
				case 405:
					return BAD_METHOD;
				case 406:
					return NOT_ACCEPTABLE;
				case 407:
					return PROXY_AUTH;
				case 408:
					return CLIENT_TIMEOUT;
				case 409:
					return CONFLICT;
				case 410:
					return GONE;
				case 411:
					return LENGTH_REQUIRED;
				case 412:
					return PRECON_FAILED;
				case 413:
					return ENTITY_TOO_LARGE;
				case 414:
					return REQ_TOO_LONG;
				case 415:
					return UNSUPPORTED_TYPE;
				case 500:
					return INTERNAL_ERROR;
				case 501:
					return NOT_IMPLEMENTED;
				case 502:
					return BAD_GATEWAY;
				case 503:
					return UNAVAILABLE;
				case 504:
					return GATEWAY_TIMEOUT;
				case 505:
					return VERSION;
				default:
					return UNKNOWN;
			}
		}
	}
}