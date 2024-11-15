package org.javastack.jhttpcli.example;

import static java.lang.System.out;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.javastack.jhttpcli.Content;
import org.javastack.jhttpcli.Headers;
import org.javastack.jhttpcli.HttpClient;
import org.javastack.jhttpcli.Request;
import org.javastack.jhttpcli.Request.Method;
import org.javastack.jhttpcli.Response;
import org.javastack.jhttpcli.URLCoder;

public class Example {
	/**
	 * Example code
	 * 
	 * @param args parameters
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		HttpClient cli = new HttpClient();
		cli.setConnTimeoutMs(10000);
		cli.setReadTimeoutMs(10000);
		// GET
		{
			Request req = new Request(new URL("https://ifconfig.me/"), //
					Method.GET, //
					Headers.create() //
							.set("Accept", "text/plain") //
							.get(), //
					Content.EMPTY);
			Response res = cli.execute(req);
			out.println("HTTP_CODE: " + res.code);
			out.println("HTTP_HEADERS: " + res.hdrs);
			out.println("HTTP_BODY: " + res.body.toStringUTF8());
			out.println("ERROR: " + res.exception);
		}
		// POST
		{
			Request req = new Request(new URL("http://localhost/search"), //
					Method.POST, //
					Headers.create() // Header
							.add("User-Agent", "dummy") //
							.set("Content-Type", URLCoder.CONTENT_TYPE) //
							.get(), //
					Content.fromStringISOLatin1(URLCoder.create(StandardCharsets.ISO_8859_1) // Body
							.add("query", "api") //
							.add("ver", "2") //
							.get()));
			Response res = cli.execute(req);
			out.println("HTTP_CODE: " + res.code);
			out.println("HTTP_HEADERS: " + res.hdrs);
			out.println("HTTP_BODY: " + res.body.toStringUTF8());
			out.println("ERROR: " + res.exception);
			if (res.exception != null) {
				req.dump(out);
			}
		}
	}
}
