package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Page implements Serializable {
	private static final long serialVersionUID=1l;
	
	private String comment;
	private String id;
	private PageTimings pageTimings;
	private String startedDateTime;
	private String title;

	public String getComment() {
		return comment;
	}

	public String getId() {
		return id;
	}

	public PageTimings getPageTimings() {
		return pageTimings;
	}

	public String getStartedDateTime() {
		return startedDateTime;
	}

	public String getTitle() {
		return title;
	}

	public void load(JSONObject data) {
		startedDateTime = data.getSafe(String.class, "startedDateTime");
		id = data.getSafe(String.class, "id");
		title = data.getSafe(String.class, "title");
		comment = data.get(String.class, "comment", null);

		JSONObject t = data.getSafe(JSONObject.class, "pageTimings");
		pageTimings = new PageTimings();
		pageTimings.load(t);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPageTimings(PageTimings pageTimings) {
		this.pageTimings = pageTimings;
	}

	public void setStartedDateTime(String startedDateTime) {
		this.startedDateTime = startedDateTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
