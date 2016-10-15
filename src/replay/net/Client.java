package replay.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import replay.har.Header;
import replay.har.Request;
import replay.har.Response;

public class Client {


	
	public Response invoke(Request request) throws IOException {
		URL u = new URL(request.getUrl());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod(request.getMethod());
		
		for(Header h : request.getHeader()) {
			conn.setRequestProperty(h.getName(),h.getValue());
		}
		
		// TODO
		
		return null;
	}
}
