package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Browser implements Serializable {
	private static final long serialVersionUID=1l;
	
	private String comment;
	private String name;
	private String version;

	public String getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void load(JSONObject o) {
		name = o.getSafe(String.class, "name");
		version = o.getSafe(String.class, "version");
		comment = o.get(String.class, "comment", null);
	}
	
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "\"browser\" : { \"name\"=\"" + name + "\", \"version\"=" + version + ", \"comment\"=\"" + comment
				+ "\" }";
	}

}
