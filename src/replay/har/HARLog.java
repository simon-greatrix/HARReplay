package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONArray;
import replay.har.SimpleJSONParser.JSONObject;

public class HARLog implements Serializable {
	private static final long serialVersionUID = 1l;

	private Browser browser;
	private String comment;
	private Creator creator;
	private Entry[] entries;
	private Page[] pages;
	private String version = "1.1";

	public Browser getBrowser() {
		return browser;
	}

	public String getComment() {
		return comment;
	}

	public Creator getCreator() {
		return creator;
	}

	public Entry[] getEntry() {
		return entries;
	}

	public Entry getEntry(int i) {
		return this.entries[i];
	}

	public Page[] getPage() {
		return pages;
	}

	public Page getPage(int i) {
		return pages[i];
	}

	public void setPage(int i, Page p) {
		pages[i] = p;
	}

	public String getVersion() {
		return version;
	}

	public void load(JSONObject root) {
		JSONObject src = root.get(JSONObject.class, "log", null);
		if (src == null)
			throw new IllegalArgumentException("Could not find \"log\" in input root.");
		version = src.get(String.class, "version", "1.1");

		JSONObject o = src.getSafe(JSONObject.class, "creator");
		if (o != null) {
			creator = new Creator();
			creator.load(o);
		}

		o = src.get(JSONObject.class, "browser", null);
		if (o != null) {
			browser = new Browser();
			browser.load(o);
		}

		JSONArray a = src.get(JSONArray.class, "pages", null);
		pages = new Page[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONObject obj = a.get(JSONObject.class, i, null);
			Page p = new Page();
			p.load(obj);
			pages[i] = p;
		}

		a = src.getSafe(JSONArray.class, "entries");
		entries = new Entry[a.size()];
		for (int i = 0; i < a.size(); i++) {
			Entry e = new Entry();
			entries[i] = e;
			e.load(a.get(JSONObject.class, i, null));
		}

		comment = src.get(String.class, "comment", null);
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setEntry(Entry[] entries) {
		this.entries = entries;
	}

	public void setEntry(int i, Entry e) {
		this.entries[i] = e;
	}

	public void setPage(Page[] pages) {
		this.pages = pages;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
