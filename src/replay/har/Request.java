package replay.har;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import replay.har.SimpleJSONParser.JSONArray;
import replay.har.SimpleJSONParser.JSONObject;

public class Request implements Serializable {
	private static final long serialVersionUID=1l;

	private URL actualUrl = null;
	private int bodySize = -1;
	private String comment;
	private Cookie[] cookies;
	private Header[] headers;
	private int headerSize = -1;
	private String httpVersion;
	private String method;
	private PostData postData;
	private QueryString[] queryString;
	private String url;

	public int getBodySize() {
		return bodySize;
	}

	public String getComment() {
		return comment;
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

	public String getMethod() {
		return method;
	}

	public PostData getPostData() {
		return postData;
	}

	public QueryString[] getQueryString() {
		return queryString;
	}

	public String getUrl() {
		return url;
	}

	public void load(JSONObject o) {
		JSONArray empty = new JSONArray();
		
		method = o.get(String.class, "method", "GET").toUpperCase();
		url = o.getSafe(String.class, "url");
		httpVersion = o.get(String.class, "httpVersion", "unknown");

		JSONArray a = o.get(JSONArray.class, "cookies", empty);
		cookies = new Cookie[a.size()];
		for (int i = 0; i < a.size(); i++) {
			Cookie c = new Cookie();
			cookies[i] = c;
			c.load(a.get(JSONObject.class, i, null));
		}

		a = o.get(JSONArray.class, "headers",empty);
		headers = new Header[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONObject obj = a.get(JSONObject.class, i, null);
			Header h = new Header();
			headers[i] = h;
			h.load(obj);
		}

		a = o.get(JSONArray.class, "queryString", empty);
		queryString = new QueryString[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONObject obj = a.get(JSONObject.class, i, null);
			QueryString p = new QueryString();
			p.load(obj);
			queryString[i] = p;
		}
		actualUrl = null;

		JSONObject p = o.get(JSONObject.class, "postData", null);
		if (p != null) {
			postData = new PostData();
			postData.load(p);
		}

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

	public void setCookie(Cookie[] cookies) {
		if( cookies==null ) cookies = new Cookie[0];
		this.cookies = cookies;
	}

	public void setHeader(Header[] headers) {
		if( headers==null ) headers = new Header[0];
		this.headers = headers;
	}
	
	public void setHeader(int i, Header h) {
		headers[i]=h;
	}
	public Header getHeader(int i) {
		return headers[i];
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public void setMethod(String method) {
		this.method = method.toLowerCase();
	}

	public void setPostData(PostData postData) {
		this.postData = postData;
	}

	public void setQueryString(QueryString[] queryString) {
		if( queryString==null ) queryString = new QueryString[0];
		this.queryString = queryString;
		actualUrl = null;
	}

	public void setUrl(String url) {
		this.url = url;
		actualUrl = null;
	}

	
	public URL makeUrl() throws MalformedURLException, URISyntaxException {
		if( actualUrl != null ) return actualUrl;		

		URI uri = new URI(url);
		uri.normalize();

		if( ! (uri.getScheme().equals("http") || uri.getScheme().equals("https")) ) {
			throw new MalformedURLException("Scheme \""+uri.getScheme()+"\" is not an HTTP scheme");
		}
		
		if( queryString.length > 0 ) {
			StringBuilder query = new StringBuilder();
			for(QueryString qs : queryString) {
				qs.appendTo(query);
				query.append('&');
			}
			query.setLength(query.length()-1);
			
			uri = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),uri.getPort(),
					uri.getPath(),query.toString(),uri.getFragment());
			actualUrl = new URL(uri.toASCIIString());
		} else {
			actualUrl = new URL(url);
		}
		return actualUrl;
	}
}
