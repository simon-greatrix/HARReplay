package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Header implements Serializable {
	private static final long serialVersionUID=1l;

	private String comment;
	private String name;
	private String value;

	public String getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void load(JSONObject obj) {
		name = obj.get(String.class, "name", null);
		value = obj.get(String.class, "value", null);
		comment = obj.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
