# jhttpcli

Simple HTTP Client library, without external dependencies. Open Source Java project under Apache License v2.0

### Current Stable Version is [1.0.0](https://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.javastack%20a%3Ajhttpcli)

---

## DOC

#### Usage Example

```java
import static java.lang.System.out;
import java.net.*;
import java.util.*;
import org.javastack.jhttpcli.*;

public class Example {
	public static void main(String[] args) throws Throwable {
		HttpClient cli = new HttpClient();
		Request req = new Request(new URL("https://ifconfig.me/"), //
				Request.Method.GET, //
				Collections.singletonMap("Accept", //
						Arrays.asList("text/plain")), //
				Content.EMPTY);
		Responseres = cli.execute(req);
		out.println("HTTP_CODE: " + res.code);
		out.println("HTTP_HEADERS: " + res.hdrs);
		out.println("HTTP_BODY: " + res.body.toStringUTF8());
	}
}
```

* More examples in [Example package](https://github.com/ggrandes/jhttpcli/tree/master/src/main/java/org/javastack/jhttpcli/example/)

---

## MAVEN

Add the dependency to your pom.xml:

    <dependency>
        <groupId>org.javastack</groupId>
        <artifactId>jhttpcli</artifactId>
        <version>1.0.0</version>
    </dependency>

---
Inspired in [Apache HttpClient](https://hc.apache.org/), this code is Java-minimalistic version.
