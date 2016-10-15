package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Entry implements Serializable {
	private static final long serialVersionUID=1l;

	private Cache cache;
	private String comment;
	private String connection;
	private String pageref;
	private Request request;
	private Response response;
	private String serverIPAddress;
	private String startedDateTime;
	private int time;
	private Timings timings;

	public Cache getCache() {
		return cache;
	}

	public String getComment() {
		return comment;
	}

	public String getConnection() {
		return connection;
	}

	public String getPageref() {
		return pageref;
	}

	public Request getRequest() {
		return request;
	}

	public Response getResponse() {
		return response;
	}

	public String getServerIPAddress() {
		return serverIPAddress;
	}

	public String getStartedDateTime() {
		return startedDateTime;
	}

	public int getTime() {
		return time;
	}

	public Timings getTimings() {
		return timings;
	}

	public void load(JSONObject obj) {
		pageref = obj.get(String.class, "pageref", null);

		startedDateTime = obj.getSafe(String.class, "startedDateTime");

		Integer i = obj.get(Integer.class, "time", null);
		if (i != null)
			time = i.intValue();

		JSONObject o = obj.getSafe(JSONObject.class, "request");
		request = new Request();
		request.load(o);

		o = obj.getSafe(JSONObject.class, "response");
		response = new Response();
		response.load(o);

		o = obj.getSafe(JSONObject.class, "cache");
		cache = new Cache();
		cache.load(o);

		o = obj.getSafe(JSONObject.class, "timings");
		timings = new Timings();
		timings.load(o);

		serverIPAddress = obj.get(String.class, "serverIPAddress", null);
		connection = obj.get(String.class, "connection", null);
		comment = obj.get(String.class, "comment", null);
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public void setPageref(String pageref) {
		this.pageref = pageref;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public void setServerIPAddress(String serverIPAddress) {
		this.serverIPAddress = serverIPAddress;
	}

	public void setStartedDateTime(String startedDateTime) {
		this.startedDateTime = startedDateTime;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setTimings(Timings timings) {
		this.timings = timings;
	}

}
