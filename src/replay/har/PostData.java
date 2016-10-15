package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONArray;
import replay.har.SimpleJSONParser.JSONObject;

public class PostData implements Serializable {
	private static final long serialVersionUID=1l;

	private String comment;
	private String mimeType;
	private Param[] params;
	private String text;

	public String getComment() {
		return comment;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Param[] getParam() {
		return params;
	}

	public Param getParam(int i) {
		return params[i];
	}

	public void setParam(int i, Param p) {
		params[i] = p;
	}

	public String getText() {
		return text;
	}

	public void load(JSONObject p) {
		mimeType = p.getSafe(String.class, "mimeType");
		
		JSONArray a = p.getSafe(JSONArray.class, "params");
		params = new Param[a.size()];
		for (int i = 0; i < a.size(); i++) {
			JSONObject obj = a.get(JSONObject.class, i, null);
			Param pa = new Param();
			pa.load(obj);
			params[i] = pa;
		}

		text = p.getSafe(String.class, "text");
		comment = p.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setParam(Param[] params) {
		this.params = params;
	}

	public void setText(String text) {
		this.text = text;
	}

}
