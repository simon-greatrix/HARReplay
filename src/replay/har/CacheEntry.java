package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class CacheEntry implements Serializable {
	private static final long serialVersionUID=1l;
	
	private String comment;
	private String eTag;
	private String expires;
	private Integer hitCount;
	private String lastAccess;

	public String getComment() {
		return comment;
	}

	public String getETag() {
		return eTag;
	}

	public String getExpires() {
		return expires;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public String getLastAccess() {
		return lastAccess;
	}

	public void load(JSONObject e) {
		expires = e.get(String.class, "expires", null);
		lastAccess = e.get(String.class, "lastAccess", null);
		eTag = e.get(String.class, "eTag", null);
		hitCount = e.get(Integer.class, "hitCount", null);
		comment = e.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public void setLastAccess(String lastAccess) {
		this.lastAccess = lastAccess;
	}

}
