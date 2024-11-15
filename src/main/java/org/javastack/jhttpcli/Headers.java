package org.javastack.jhttpcli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple facade to manage a bunch of HTTP headers
 * 
 * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_header_fields">HTTP_header_fields</a>
 */
public class Headers {
	private final LinkedHashMap<String, ArrayList<String>> hdrs = new LinkedHashMap<>();

	/**
	 * Create empty headers
	 * 
	 * @return headers instance
	 */
	public static Headers create() {
		return new Headers();
	}

	/**
	 * Return a map with keys a list of values, usable in http request
	 * 
	 * @return map
	 * @see org.javastack.jhttpcli.Request
	 */
	public Map<String, List<String>> get() {
		return Collections.unmodifiableMap(hdrs);
	}

	/**
	 * Clear backed map
	 * 
	 * @return this
	 */
	public Headers clear() {
		hdrs.clear();
		return this;
	}

	private final void remove0(final String key) {
		hdrs.remove(key);
	}

	/**
	 * Remove specified key
	 * 
	 * @param key to delete
	 * @return this
	 */
	public Headers remove(String key) {
		if ((key == null) || key.isEmpty()) {
			return this;
		}
		key = key.toLowerCase();
		remove0(key);
		return this;
	}

	private final void add0(final String key, final String value) {
		ArrayList<String> values = hdrs.get(key);
		if (values == null) {
			values = new ArrayList<>();
			hdrs.put(key, values);
		}
		values.add(value);
	}

	/**
	 * Add specified value to referenced key
	 * 
	 * @param key of reference
	 * @param value to be added
	 * @return this
	 */
	public Headers add(String key, final String value) {
		if ((key == null) || key.isEmpty() //
				|| (value == null) || value.isEmpty()) {
			return this;
		}
		key = key.toLowerCase();
		add0(key, value);
		return this;
	}

	/**
	 * Sets the specified value to the reference key (if a previous value existed, it is deleted)
	 * 
	 * @param key of reference
	 * @param value to be established
	 * @return this
	 */
	public Headers set(String key, final String value) {
		if ((key == null) || key.isEmpty() //
				|| (value == null) || value.isEmpty()) {
			return this;
		}
		key = key.toLowerCase();
		remove0(key);
		add0(key, value);
		return this;
	}

}