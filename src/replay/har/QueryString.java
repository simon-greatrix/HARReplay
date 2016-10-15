package replay.har;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import replay.har.SimpleJSONParser.JSONObject;

public class QueryString implements Serializable {
	private static final long serialVersionUID = 1l;

	/** Which bytes of UTF-8 must be encoded */
	private static final boolean[] MUST_ENCODE = new boolean[256];

	/** How to represent hexadecimal digits */
	private static final char[] HEX = new char[16];

	static {
		Arrays.fill(MUST_ENCODE, true);
		for (int i = 'A'; i <= 'Z'; i++) {
			MUST_ENCODE[i] = false;
		}
		for (int i = 'a'; i <= 'z'; i++) {
			MUST_ENCODE[i] = false;
		}
		for (int i = '0'; i <= '9'; i++) {
			MUST_ENCODE[i] = false;
		}
		MUST_ENCODE['.'] = false;
		MUST_ENCODE['-'] = false;
		MUST_ENCODE['_'] = false;
		MUST_ENCODE['*'] = false;

		// space is special
		MUST_ENCODE[' '] = false;

		for (int i = 0; i < 10; i++) {
			HEX[i] = (char) ('0' + i);
		}
		for (int i = 10; i < 16; i++) {
			HEX[i] = (char) ('a' + i - 10);
		}
	}

	/**
	 * Encode a string as UTF-8 percent encoding
	 * 
	 * @param buf
	 *            the buffer to encode onto
	 * @param value
	 *            the string
	 */
	private static void encode(StringBuilder buf, String value) {
		ByteBuffer bytes = StandardCharsets.UTF_8.encode(value);
		int len = bytes.remaining();
		while (len > 0) {
			len--;
			int b = 0xff & bytes.get();
			if (MUST_ENCODE[b]) {
				if (b == ' ') {
					buf.append('+');
					continue;
				}
				buf.append('%');
				buf.append(HEX[(b & 0xf0) >>> 4]);
				buf.append(HEX[b & 0xf]);
			}
			buf.append((char) b);
		}
	}

	private String comment;
	private String name;
	private String value;

	public String getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void load(JSONObject obj) {
		name = obj.get(String.class, "name", null);
		value = obj.get(String.class, "value", null);
		comment = obj.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void appendTo(StringBuilder buf) {
		encode(buf, name);
		buf.append('=');
		encode(buf, value);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		appendTo(buf);
		return buf.toString();
	}
}
