package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class PageTimings implements Serializable {
	private static final long serialVersionUID=1l;

	private String comment;
	private int onContentLoad = -1;
	private int onLoad = -1;

	public String getComment() {
		return comment;
	}

	public int getOnContentLoad() {
		return onContentLoad;
	}

	public int getOnLoad() {
		return onLoad;
	}

	public void load(JSONObject t) {
		Integer i = t.get(Integer.class, "onContentLoad", null);
		if (i != null)
			onContentLoad = i.intValue();

		i = t.get(Integer.class, "onLoad", null);
		if (i != null)
			onLoad = i.intValue();

		comment = t.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setOnContentLoad(int onContentLoad) {
		this.onContentLoad = onContentLoad;
	}

	public void setOnLoad(int onLoad) {
		this.onLoad = onLoad;
	}

}
