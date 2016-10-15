package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONArray;
import replay.har.SimpleJSONParser.JSONObject;

public class Response implements Serializable {
	private static final long serialVersionUID=1l;

	private int bodySize = -1;
	private String comment;
	private Content content;
	private Cookie[] cookies;
	private Header[] headers;
	private int headerSize = -1;
	private String httpVersion;
	private String redirectURL;
	private int status;
	private String statusText;
	public void setHeader(int i, Header h) {
		headers[i]=h;
	}
	public Header getHeader(int i) {
		return headers[i];
	}
	public int getBodySize() {
		return bodySize;
	}

	public String getComment() {
		return comment;
	}

	public Content getContent() {
		return content;
	}

	public Cookie getCookie(int i) {
		return cookies[i];
	}

	public void setCookie(int i, Cookie c) {
		cookies[i] = c;
	}

	public Cookie[] getCookie() {
		return cookies;
	}
	public Header[] getHeader() {
		return headers;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void load(JSONObject o) {
		Integer s = o.getSafe(Integer.class, "status");
		status = s.intValue();

		statusText = o.getSafe(String.class, "statusText");

		httpVersion = o.getSafe(String.class, "httpVersion");

		JSONArray a = o.getSafe(JSONArray.class, "cookies");
		cookies = new Cookie[a.size()];
		for (int i = 0; i < a.size(); i++) {
			Cookie c = new Cookie();
			cookies[i] = c;
			c.load(a.get(JSONObject.class, i, null));
		}

		a = o.getSafe(JSONArray.class, "headers");
		headers = new Header[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONObject obj = a.get(JSONObject.class, i, null);
			Header h = new Header();
			headers[i] = h;
			h.load(obj);
		}

		JSONObject c = o.getSafe(JSONObject.class, "content");
		content = new Content();
		content.load(c);

		redirectURL = o.get(String.class, "redirectURL", "");

		Integer i = o.get(Integer.class, "headerSize", null);
		if (i != null)
			headerSize = i.intValue();

		i = o.get(Integer.class, "bodySize", null);
		if (i != null)
			bodySize = i.intValue();

		comment = o.get(String.class, "comment", null);
	}

	public void setBodySize(int bodySize) {
		this.bodySize = bodySize;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void setCookie(Cookie[] cookies) {
		this.cookies = cookies;
	}

	public void setHeader(Header[] headers) {
		this.headers = headers;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

}
