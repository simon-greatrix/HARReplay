package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Param implements Serializable {
	private static final long serialVersionUID=1l;

	private String comment;
	private String contentType;
	private String fileName;
	private String name;
	private String value;

	public String getComment() {
		return comment;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void load(JSONObject obj) {
		name = obj.getSafe(String.class, "name");
		value = obj.get(String.class, "value", null);
		fileName = obj.get(String.class, "fileName", null);
		contentType = obj.get(String.class, "contentType", null);
		comment = obj.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
