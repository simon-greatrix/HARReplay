package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Cookie implements Serializable {
	private static final long serialVersionUID=1l;

	private String comment;
	private String domain;
	private String expires;
	private boolean httpOnly = false;
	private String name;
	private String path;
	private boolean secure = false;
	private String value;

	public String getComment() {
		return comment;
	}

	public String getDomain() {
		return domain;
	}

	public String getExpires() {
		return expires;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getValue() {
		return value;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public boolean isSecure() {
		return secure;
	}

	public void load(JSONObject o) {
		name = o.getSafe(String.class, "name");
		value = o.getSafe(String.class, "value");
		path = o.get(String.class, "path", null);
		domain = o.get(String.class, "domain", null);
		expires = o.get(String.class, "expires", null);

		httpOnly = o.get(Boolean.class, "httpOnly", Boolean.FALSE).booleanValue();
		secure = o.get(Boolean.class, "secure", Boolean.FALSE).booleanValue();
		comment = o.get(String.class, "comment", null);
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
