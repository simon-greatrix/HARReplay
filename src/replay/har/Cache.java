package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Cache implements Serializable {
	private static final long serialVersionUID=1l;

	private CacheEntry afterRequest;
	private CacheEntry beforeRequest;
	private String comment;

	public CacheEntry getAfterRequest() {
		return afterRequest;
	}

	public CacheEntry getBeforeRequest() {
		return beforeRequest;
	}

	public String getComment() {
		return comment;
	}

	public void load(JSONObject o) {
		JSONObject e = o.get(JSONObject.class, "beforeRequest", null);
		if (e != null) {
			beforeRequest = new CacheEntry();
			beforeRequest.load(e);
		}

		e = o.get(JSONObject.class, "afterRequest", null);
		if (e != null) {
			afterRequest = new CacheEntry();
			afterRequest.load(e);
		}

		comment = o.get(String.class, "comment", null);
	}

	
	
	public void setAfterRequest(CacheEntry afterRequest) {
		this.afterRequest = afterRequest;
	}

	public void setBeforeRequest(CacheEntry beforeRequest) {
		this.beforeRequest = beforeRequest;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
