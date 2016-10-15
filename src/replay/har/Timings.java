package replay.har;

import java.io.Serializable;

import replay.har.SimpleJSONParser.JSONObject;

public class Timings implements Serializable {
	private static final long serialVersionUID=1l;

	private Number blocked;
	private String comment;
	private Number connect;
	private Number dns;
	private double receive;
	private double send;
	private Number ssl;
	private double wait;

	public Number getBlocked() {
		return blocked;
	}

	public String getComment() {
		return comment;
	}

	public Number getConnect() {
		return connect;
	}

	public Number getDns() {
		return dns;
	}

	public double getReceive() {
		return receive;
	}

	public double getSend() {
		return send;
	}

	public Number getSsl() {
		return ssl;
	}

	public double getWait() {
		return wait;
	}

	public void load(JSONObject o) {
		blocked = o.get(Number.class, "blocked", null);
		dns = o.get(Number.class, "dns", null);
		connect = o.get(Number.class, "connect", null);
		send = o.getSafe(Number.class, "send").doubleValue();
		wait = o.getSafe(Number.class, "wait").doubleValue();
		receive = o.getSafe(Number.class, "receive").doubleValue();
		ssl = o.get(Number.class, "ssl", null);

	}

	public void setBlocked(Number blocked) {
		this.blocked = blocked;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setConnect(Number connect) {
		this.connect = connect;
	}

	public void setDns(Number dns) {
		this.dns = dns;
	}

	public void setReceive(double receive) {
		this.receive = receive;
	}

	public void setSend(double send) {
		this.send = send;
	}

	public void setSsl(Number ssl) {
		this.ssl = ssl;
	}

	public void setWait(double wait) {
		this.wait = wait;
	}

}
