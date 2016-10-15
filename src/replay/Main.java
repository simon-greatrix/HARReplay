package replay;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import replay.har.HARLog;
import replay.har.SimpleJSONParser;
import replay.har.SimpleJSONParser.JSONObject;
import replay.har.SimpleJSONParser.Primitive;

public class Main {
	public static void main(String[] args) throws Exception {
		
		
		File file = new File(args[0]);
		Reader reader = new FileReader(file);
		Primitive parsed = SimpleJSONParser.parse(reader);
		JSONObject object = (JSONObject) parsed.getValue();
		HARLog log = new HARLog();
		log.load(object);
		
		Primitive p = SimpleJSONParser.save(log);
		Writer w = new OutputStreamWriter(System.out);
		SimpleJSONParser.write(w, p);
		w.flush();
		//CookieHandler.setDefault(new CookieManager(new CookieStorage(), CookiePolicy.ACCEPT_ALL));

		//AppWindow.startup();
	}
}
