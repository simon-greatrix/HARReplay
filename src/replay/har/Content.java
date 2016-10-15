package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Content implements Serializable {
	private static final long serialVersionUID = 1l;

	private String comment;
	private int compression;
	private String encoding;
	private String mimeType;
	private int size;
	private String text;

	public String getComment() {
		return comment;
	}

	public int getCompression() {
		return compression;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getMimeType() {
		return mimeType;
	}

	public int getSize() {
		return size;
	}

	public String getText() {
		return text;
	}

	public void load(JSONObject c) {
		Integer i = c.getSafe(Integer.class, "size");
		size = i.intValue();

		i = c.get(Integer.class, "compression", null);
		if (i != null)
			compression = i.intValue();

		mimeType = c.get(String.class, "mimeType", null);
		text = c.get(String.class, "text", null);
		encoding = c.get(String.class, "encoding", null);
		comment = c.get(String.class, "comment", null);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCompression(int compression) {
		this.compression = compression;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setText(String text) {
		this.text = text;
	}

}
