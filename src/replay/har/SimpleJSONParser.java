package replay.har;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Very simple JSON parser.
 * 
 * @author Simon
 *
 */
public class SimpleJSONParser {
	/**
	 * Representation of an array in JSON
	 */
	static public class JSONArray extends ArrayList<Primitive> {
		/** serial version UID */
		private static final long serialVersionUID = 2l;

		public void add(Boolean bool) {
			if (bool != null) {
				add(new Primitive(Type.BOOLEAN, bool));
			}
		}

		public void add(JSONArray array) {
			if (array != null) {
				add(new Primitive(Type.ARRAY, array));
			}
		}

		public void add(JSONObject object) {
			if (object != null) {
				add(new Primitive(Type.OBJECT, object));
			}
		}

		public void add(Number num) {
			if (num != null) {
				add(new Primitive(Type.NUMBER, num));
			}
		}

		public void add(String str) {
			if (str != null) {
				add(new Primitive(Type.STRING, str));
			}
		}

		public void addNull() {
			add(new Primitive(Type.NULL, null));
		}

		/**
		 * Get a type-safe value
		 * 
		 * @param type
		 *            the type required
		 * @param index
		 *            the value's index
		 * @param dflt
		 *            default value if missing or wrong type
		 * @param <T>
		 *            the value's class to return
		 * @return the value
		 */
		public <T> T get(Class<T> type, int index, T dflt) {
			Primitive prim = get(index);
			if (prim == null)
				return dflt;
			Object val = prim.getValue();
			if (val == null)
				return null;
			if (!type.isInstance(val))
				return dflt;
			return type.cast(val);
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append('[');
			for (Primitive e : this) {
				buf.append(String.valueOf(e));
				buf.append(", ");
			}
			if (buf.length() > 1) {
				buf.setLength(buf.length() - 2);
			}
			buf.append(']');
			return buf.toString();
		}
	}

	/**
	 * Representation of an object in JSON
	 */
	static public class JSONObject extends LinkedHashMap<String, Primitive> {
		/** serial version UID */
		private static final long serialVersionUID = 1l;

		/**
		 * Get a type-safe value
		 * 
		 * @param type
		 *            the type required
		 * @param name
		 *            the name of the field
		 * @param dflt
		 *            default value if missing or wrong type
		 * @param <T>
		 *            the value's class to return
		 * @return the value
		 */
		public <T> T get(Class<T> type, String name, T dflt) {
			Primitive prim = get(name);
			if (prim == null)
				return dflt;
			Object val = prim.getValue();
			if (val == null)
				return null;
			if (!type.isInstance(val))
				return dflt;
			return type.cast(val);
		}

		/**
		 * Get a type-safe value
		 * 
		 * @param type
		 *            the type required
		 * @param name
		 *            the name of the field
		 * @param dflt
		 *            default value if missing or wrong type
		 * @param <T>
		 *            the value's class to return
		 * @return the value
		 */
		public <T> T getSafe(Class<T> type, String name) {
			Primitive prim = get(name);
			if (prim == null) {
				throw new IllegalArgumentException("Property \"" + name + "\" is missing.");
			}
			Object val = prim.getValue();
			if (val == null)
				return null;
			if (!type.isInstance(val)) {
				throw new IllegalArgumentException("Property \"" + name + "\" is the wrong type.");
			}
			return type.cast(val);
		}

		public void set(String key, Boolean bool) {
			if (bool != null) {
				put(key, new Primitive(Type.BOOLEAN, bool));
			}
		}

		public void set(String key, JSONArray array) {
			if (array != null) {
				put(key, new Primitive(Type.ARRAY, array));
			}
		}

		public void set(String key, JSONObject object) {
			if (object != null) {
				put(key, new Primitive(Type.OBJECT, object));
			}
		}

		public void set(String key, Number num) {
			if (num != null) {
				put(key, new Primitive(Type.NUMBER, num));
			}
		}

		public void set(String key, String str) {
			if (str != null) {
				put(key, new Primitive(Type.STRING, str));
			}
		}

		public void setNull(String key) {
			put(key, new Primitive(Type.NULL, null));
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append('{');
			for (Map.Entry<String, Primitive> e : entrySet()) {
				buf.append(escapeString(e.getKey()));
				buf.append(':');
				buf.append(String.valueOf(e.getValue()));
				buf.append(", ");
			}
			if (buf.length() > 1) {
				buf.setLength(buf.length() - 2);
			}
			buf.append('}');
			return buf.toString();
		}
	}

	/**
	 * Container for a JSON primitive
	 *
	 */
	static public class Primitive {
		/** The type represented by this primitive */
		final Type type_;

		/** The encapsulated value */
		final Object value_;

		/**
		 * Create new primitive container.
		 * 
		 * @param type
		 *            contained type
		 * @param value
		 *            contained value
		 */
		public Primitive(Type type, Object value) {
			type.getType().cast(value);
			if ((type == Type.NULL) != (value == null)) {
				throw new IllegalArgumentException("Null if and only if NULL");
			}
			type_ = type;
			value_ = value;
		}

		/**
		 * Get the type encapsulated by this primitive
		 * 
		 * @return the type
		 */
		public Type getType() {
			return type_;
		}

		/**
		 * Get the value encapsulated by this primitive
		 * 
		 * @return the value
		 */
		public Object getValue() {
			return value_;
		}

		/**
		 * Get the value encapsulated by this primitive
		 * 
		 * @param <T>
		 *            required type
		 * @param type
		 *            the required type
		 * @param dflt
		 *            default value if type is not correct
		 * @return the value
		 */
		public <T> T getValue(Class<T> type, T dflt) {
			if (type.isInstance(value_)) {
				return type.cast(value_);
			}
			return dflt;
		}

		/**
		 * Get the value encapsulated by this primitive
		 * 
		 * @param <T>
		 *            required type
		 * @param type
		 *            the required type
		 * @return the value
		 */
		public <T> T getValueSafe(Class<T> type) {
			return type.cast(value_);
		}

		@Override
		public String toString() {
			if (type_ == Type.STRING)
				return escapeString((String) value_);
			return String.valueOf(value_);
		}
	}

	static public class Source {
		int line = 1;
		int pos = 0;
		PushbackReader reader;

		public Source(Reader input) {
			if (!(input instanceof PushbackReader)) {
				reader = new PushbackReader(input);
			} else {
				reader = (PushbackReader) input;
			}
		}

		public int read() throws IOException {
			int r;
			try {
				r = reader.read();
			} catch (IOException ioe) {
				IOException i2 = new IOException(ioe.getMessage() + " at line " + line + ", position " + pos);
				if (ioe.getCause() != null)
					i2.initCause(ioe.getCause());
				throw i2;
			}
			if (r == '\n') {
				line++;
				pos = 0;
			} else {
				pos++;
			}
			return r;
		}

		public void unread(int r) throws IOException {
			try {
				reader.unread(r);
			} catch (IOException ioe) {
				IOException i2 = new IOException(ioe.getMessage() + " at line " + line + ", position " + pos);
				if (ioe.getCause() != null)
					i2.initCause(ioe.getCause());
				throw i2;
			}
			if (r == '\n') {
				line--;
				pos = 0;
			}
			pos--;
		}
	}

	/**
	 * Enumeration of JSON types
	 */
	static public enum Type {
		/** A JSON [...] construct */
		ARRAY(JSONArray.class),

		/** A true or a false */
		BOOLEAN(Boolean.class),

		/** A null */
		NULL(Void.class),

		/** Any number */
		NUMBER(Number.class),

		/** A JSON { ... } construct */
		OBJECT(JSONObject.class),

		/** A string */
		STRING(String.class);

		/** Class of associated encapsulated values */
		private final Class<?> type_;

		/**
		 * Create type
		 * 
		 * @param type
		 *            encapsulated value class
		 */
		Type(Class<?> type) {
			type_ = type;
		}

		/**
		 * Get encapsulate value class
		 * 
		 * @return the type of the encapsulated value
		 */
		public Class<?> getType() {
			return type_;
		}
	}

	/** Hexidecimal digits */
	private static char[] HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * Letters for the "false" literal
	 */
	private static final char[] LITERAL_FALSE = new char[] { 'A', 'a', 'L', 'l', 'S', 's', 'E', 'e' };

	/** Letters for the "null" literal */
	private static final char[] LITERAL_NULL = new char[] { 'U', 'u', 'L', 'l', 'L', 'l' };

	/** Letters for the "true" literal */
	private static final char[] LITERAL_TRUE = new char[] { 'R', 'r', 'U', 'u', 'E', 'e' };

	/**
	 * Escape a String using JSON escaping rules
	 * 
	 * @param val
	 *            the string
	 * @return the escaped and quotes string
	 */
	static String escapeString(String val) {
		StringBuilder buf = new StringBuilder();
		buf.append('\"');
		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);
			switch (ch) {
			case '\"':
				buf.append("\\\"");
				break;
			case '\\':
				buf.append("\\\\");
				break;
			// case '/':
			// buf.append("\\/");
			// break;
			case '\b':
				buf.append("\\b");
				break;
			case '\f':
				buf.append("\\f");
				break;
			case '\n':
				buf.append("\\n");
				break;
			case '\r':
				buf.append("\\r");
				break;
			case '\t':
				buf.append("\\t");
				break;
			default:
				if (32 <= ch && ch < 127) {
					buf.append(ch);
					break;
				}
				buf.append("\\u");
				buf.append(HEX[(ch >>> 12) & 0xf]);
				buf.append(HEX[(ch >>> 8) & 0xf]);
				buf.append(HEX[(ch >>> 4) & 0xf]);
				buf.append(HEX[ch & 0xf]);
			}
		}
		buf.append('\"');
		return buf.toString();
	}

	/**
	 * Check if input represents whitespace
	 * 
	 * @param r
	 *            the input
	 * @return true if whitespace
	 */
	private static boolean isWhite(int r) {
		return (r == ' ' || r == '\n' || r == '\r' || r == '\t' || r == '\f');
	}

	/**
	 * Match a literal.
	 * 
	 * @param literal
	 *            the literal to match (excluding first character)
	 * @param input
	 *            input
	 * @throws IOException
	 *             if literal is not matched
	 */
	private static void matchLiteral(char[] literal, Source input) throws IOException {
		for (int i = 0; i < literal.length; i += 2) {
			int r = input.read();
			if (r == -1)
				throw new EOFException();
			if (r != literal[i] && r != literal[i + 1])
				throw new IOException("Invalid character in literal " + Integer.toHexString(r) + " when expecting '"
						+ literal[i] + "' at line " + input.line + ", position " + input.pos);
		}
	}

	/**
	 * Read the next primitive from the input
	 * 
	 * @param input
	 *            the input
	 * @return the next primitive
	 * @throws IOException
	 *             if the input cannot be read, or there is invalid JSON data
	 */
	public static Primitive parse(Reader input) throws IOException {
		Source source = new Source(input);
		return parseAny(source);
	}

	/**
	 * Read the next primitive from the input
	 * 
	 * @param input
	 *            the input
	 * @return the next primitive
	 * @throws IOException
	 *             if the input cannot be read, or there is invalid JSON data
	 */
	private static Primitive parseAny(Source input) throws IOException {
		int r = skipWhite(input);
		if (r == '{')
			return parseObject(input);
		if (r == '[')
			return parseArray(input);
		if (r == '\"')
			return parseString(input);
		if (r == 't' || r == 'T' || r == 'f' || r == 'F')
			return parseBoolean(input, r);
		if (r == 'n' || r == 'N')
			return parseNull(input);
		if (r == '-' || r == '.' || ('0' <= r && r <= '9'))
			return parseNumber(input, r);
		throw new IOException("Invalid input byte 0x" + Integer.toHexString(r) + " at line " + input.line
				+ ", position " + input.pos);
	}

	/**
	 * Parse an array from the input
	 * 
	 * @param input
	 *            the input
	 * @return the array
	 * @throws IOException
	 */
	private static Primitive parseArray(Source input) throws IOException {
		JSONArray arr = new JSONArray();
		Primitive prim = new Primitive(Type.ARRAY, arr);

		// check for empty array
		int r = skipWhite(input);
		if (r == ']')
			return prim;
		input.unread(r);

		while (true) {
			Primitive val = parseAny(input);
			arr.add(val);
			r = skipWhite(input);
			if (r == ']')
				return prim;
			if (r != ',')
				throw new IOException("Array coninuation was not ',' but 0x" + Integer.toHexString(r) + " at line "
						+ input.line + ", position " + input.pos);
		}

	}

	/**
	 * Parse a boolean from the input
	 * 
	 * @param input
	 *            the input
	 * @param r
	 *            the initial character of the literal
	 * @return the boolean
	 * @throws IOException
	 */
	private static Primitive parseBoolean(Source input, int r) throws IOException {
		Boolean val = Boolean.valueOf(r == 'T' || r == 't');
		matchLiteral(val.booleanValue() ? LITERAL_TRUE : LITERAL_FALSE, input);
		return new Primitive(Type.BOOLEAN, val);
	}

	/**
	 * Parse a null from the input
	 * 
	 * @param input
	 *            the input
	 * @return the null
	 * @throws IOException
	 */
	private static Primitive parseNull(Source input) throws IOException {
		matchLiteral(LITERAL_NULL, input);
		return new Primitive(Type.NULL, null);
	}

	/**
	 * Parse a number from the input
	 * 
	 * @param input
	 *            the input
	 * @param r
	 *            the initial character of the number
	 * @return the number
	 * @throws IOException
	 */
	private static Primitive parseNumber(Source input, int r) throws IOException {
		StringBuilder buf = new StringBuilder();
		int s = 1;
		if (r == '-') {
			buf.append('-');
			s = 0;
		} else if (r == '.') {
			buf.append("0.");
			s = 2;
		} else {
			buf.append((char) r);
		}
		boolean notDone = true;
		while (notDone) {
			r = input.read();
			if (r == -1) {
				notDone = false;
			}

			switch (s) {
			case 0:
				// expecting first digit or period
				if (r == '.') {
					buf.append("0.");
					s = 2;
				} else if ('0' <= r && r <= '9') {
					buf.append((char) r);
					s = 1;
				} else {
					buf.append((char) r);
					throw new IOException("Invalid numeric input: \"" + buf.toString() + "\" at line " + input.line
							+ ", position " + input.pos);
				}
				break;
			case 1:
				// seen at least one digit, expecting digit, period or 'e'
				if (r == '.') {
					buf.append('.');
					s = 2;
				} else if ('0' <= r && r <= '9') {
					buf.append((char) r);
				} else if (r == 'e' || r == 'E') {
					buf.append('e');
					s = 3;
				} else {
					notDone = false;
				}
				break;
			case 2:
				// seen a period, expecting digit or 'e'
				if ('0' <= r && r <= '9') {
					buf.append((char) r);
				} else if (r == 'e' || r == 'E') {
					buf.append('e');
					s = 3;
				} else {
					notDone = false;
				}
				break;
			case 3:
				// seen an 'e', must see '+' or '-'
				if (r == '+' || r == '-') {
					buf.append((char) r);
					s = 4;
				} else {
					buf.append((char) r);
					throw new IOException("Invalid numeric input: \"" + buf.toString() + "\" at line " + input.line
							+ ", position " + input.pos);
				}
				break;
			case 4:
				// seen "e+" or "e-", must see digit
				if ('0' <= r && r <= '9') {
					buf.append((char) r);
					s = 5;
				} else {
					buf.append((char) r);
					throw new IOException("Invalid numeric input: \"" + buf.toString() + "\" at line " + input.line
							+ ", position " + input.pos);
				}
				break;
			case 5:
				// seen "e+" or "e-" and at least one digit. Expect more digits
				// or finish
				if ('0' <= r && r <= '9') {
					buf.append((char) r);
				} else {
					notDone = false;
				}
				break;
			}
		}
		input.unread(r);
		Number val;

		// convert to a good number type
		if (s == 1) {
			Long lval = Long.valueOf(buf.toString());
			if (lval.longValue() == lval.intValue()) {
				val = Integer.valueOf(lval.intValue());
			} else {
				val = lval;
			}
		} else {
			val = Double.valueOf(buf.toString());
		}
		return new Primitive(Type.NUMBER, val);
	}

	/**
	 * Parse an object from the input
	 * 
	 * @param input
	 *            the input
	 * @return the object
	 * @throws IOException
	 */

	private static Primitive parseObject(Source input) throws IOException {
		JSONObject obj = new JSONObject();
		Primitive prim = new Primitive(Type.OBJECT, obj);

		// check for empty object
		int r = skipWhite(input);
		if (r == '}')
			return prim;
		input.unread(r);

		while (true) {
			r = skipWhite(input);
			// name must start with a quote
			if (r != '\"') {
				throw new IOException("Object pair's name did not start with '\"' but with 0x" + Integer.toHexString(r)
						+ " at line " + input.line + ", position " + input.pos);
			}
			Primitive name = parseString(input);

			// then comes the ':'
			r = skipWhite(input);
			if (r != ':') {
				throw new IOException("Object pair-value separator was not start with ':' but 0x"
						+ Integer.toHexString(r) + " at line " + input.line + ", position " + input.pos);
			}

			// and then the value
			Primitive value = parseAny(input);
			obj.put(name.getValueSafe(String.class), value);
			r = skipWhite(input);
			if (r == '}')
				return prim;
			if (r != ',')
				throw new IOException("Object coninuation was not ',' but 0x" + Integer.toHexString(r) + " at line "
						+ input.line + ", position " + input.pos);
		}
	}

	/**
	 * Parse an array from the input
	 * 
	 * @param input
	 *            the input
	 * @return the array
	 * @throws IOException
	 */
	private static Primitive parseString(Source input) throws IOException {
		int s = 0;
		int u = 0;
		StringBuilder buf = new StringBuilder();
		boolean notDone = true;
		while (notDone) {
			int r = input.read();
			switch (s) {
			case 0:
				// regular character probable
				if (r == '"') {
					notDone = false;
					break;
				}
				if (r == '\\') {
					s = 1;
					break;
				}
				if (r == -1)
					throw new EOFException("Unterminated string");
				buf.append((char) r);
				break;

			case 1:
				// expecting an escape sequence
				switch (r) {
				case '"':
					buf.append('\"');
					break;
				case '\\':
					buf.append('\\');
					break;
				case '/':
					buf.append('/');
					break;
				case 'b':
					buf.append('\b');
					break;
				case 'f':
					buf.append('\f');
					break;
				case 'n':
					buf.append('\n');
					break;
				case 'r':
					buf.append('\r');
					break;
				case 't':
					buf.append('\t');
					break;
				case 'u':
					u = 0;
					s = 2;
					break;
				default:
					throw new IOException("Invalid escape sequence \"\\" + ((char) r) + "\" at line " + input.line
							+ ", position " + input.pos);
				}
				if (s == 1)
					s = 0;
				break;

			case 2: // fall thru
			case 3: // fall thru
			case 4: // fall thru
			case 5: // fall thru
				// unicode escape
				u = u * 16;
				if ('0' <= r && r <= '9') {
					u += r - '0';
				} else if ('a' <= r && r <= 'f') {
					u += r - 'a' + 10;
				} else if ('A' <= r && r <= 'F') {
					u += r - 'A' + 10;
				} else {
					throw new IOException(
							"Invalid hex character in \\u escape at line " + input.line + ", position " + input.pos);
				}
				s++;
				if (s == 6) {
					s = 0;
					buf.append((char) u);
				}
				break;
			}
		}

		// finally create the string
		Primitive prim = new Primitive(Type.STRING, buf.toString());
		return prim;
	}

	/**
	 * Save a bean as a JSON object
	 * 
	 * @param bean
	 *            the bean to save
	 * @return the JSON object
	 */
	public static Primitive save(Object bean) {
		if (bean == null) {
			return new Primitive(Type.NULL, null);
		}
		if (bean.getClass().isArray()) {
			JSONArray array = new JSONArray();
			int size = Array.getLength(bean);
			for (int i = 0; i < size; i++) {
				array.add(save(Array.get(bean, i)));
			}
			return new Primitive(Type.ARRAY, array);
		}

		if (bean instanceof Boolean) {
			return new Primitive(Type.BOOLEAN, bean);
		}

		if (bean instanceof Number) {
			return new Primitive(Type.NUMBER, bean);
		}

		if (bean instanceof String) {
			return new Primitive(Type.STRING, bean);
		}

		JSONObject object = new JSONObject();
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(bean.getClass());
		} catch (IntrospectionException e) {
			return new Primitive(Type.STRING, "!!ERROR!!  " + e.getMessage());
		}
		PropertyDescriptor[] props = info.getPropertyDescriptors();
		for (PropertyDescriptor p : props) {
			if (p.getName().equals("class") && p.getPropertyType().equals(Class.class))
				continue;
			Object value;
			try {
				value = p.getReadMethod().invoke(bean);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				value = "!!ERROR!!  " + e.getMessage();
			}
			if (value != null) {
				object.put(p.getName(), save(value));
			}
		}
		return new Primitive(Type.OBJECT, object);
	}

	/**
	 * Skip whitespace and return the first non-white character
	 * 
	 * @param input
	 *            the input
	 * @return the first non-white character
	 * @throws IOException
	 */
	private static int skipWhite(Source input) throws IOException {
		int r;
		do {
			r = input.read();
		} while (isWhite(r));
		if (r == -1)
			throw new EOFException();
		return r;
	}

	public static void write(Writer writer, Primitive prim) throws IOException {
		write(writer, null, prim, 0);
		writer.write("\n");
	}

	private static void write(Writer writer, String key, Primitive prim, int indent) throws IOException {
		for (int i = 0; i < indent; i++) {
			writer.write("    ");
		}
		if (key != null) {
			writer.write(escapeString(key));
			writer.write(" : ");
		}
		Type type = prim.getType();
		if (type == Type.OBJECT) {
			JSONObject object = prim.getValueSafe(JSONObject.class);
			if (object.isEmpty()) {
				writer.write("{}");
				return;
			}
			writer.write("{\n");
			indent++;
			boolean isFirst = true;
			for (Map.Entry<String, Primitive> e : object.entrySet()) {
				if (isFirst) {
					isFirst = false;
				} else {
					writer.write(",\n");
				}
				write(writer, e.getKey(), e.getValue(), indent);
			}
			writer.write("\n");
			indent--;
			for (int i = 0; i < indent; i++) {
				writer.write("    ");
			}
			writer.write("}");
			return;
		}

		if (type == Type.ARRAY) {
			JSONArray array = prim.getValueSafe(JSONArray.class);
			if (array.isEmpty()) {
				writer.write("[]");
				return;
			}
			writer.write("[\n");
			indent++;
			boolean isFirst = true;
			for (Primitive p : array) {
				if (isFirst) {
					isFirst = false;
				} else {
					writer.write(",\n");
				}
				write(writer, null, p, indent);
			}
			writer.write("\n");
			indent--;
			for (int i = 0; i < indent; i++) {
				writer.write("    ");
			}
			writer.write("]");
			return;
		}

		writer.write(prim.toString());
	}
}