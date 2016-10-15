package replay;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A cookie store that prevents threads sharing cookies. The code is plagiarised
 * from the Sun implementation.
 * 
 * @author j1015580
 *
 */
public class CookieStorage implements CookieStore {

	class Jar {
		ArrayList<HttpCookie> cookieJar = new ArrayList<>();
		HashMap<String, List<HttpCookie>> byDomain = new HashMap<>();
		HashMap<URI, List<HttpCookie>> byURI = new HashMap<>();
	}

	private ThreadLocal<Jar> jarHolder = new ThreadLocal<Jar>() {
		@Override
		protected Jar initialValue() {
			return new Jar();
		}
	};

	@Override
	public void add(URI uri, HttpCookie cookie) {
		Jar jar = jarHolder.get();
		ArrayList<HttpCookie> cookieJar = jar.cookieJar;
		cookieJar.remove(cookie);

		// add new cookie if it has a non-zero max-age
		if (cookie.getMaxAge() != 0) {
			cookieJar.add(cookie);
			// and add it to domain index
			if (cookie.getDomain() != null) {
				addIndex(jar.byDomain, cookie.getDomain(), cookie);
			}
			if (uri != null) {
				// add it to uri index, too
				addIndex(jar.byURI, getEffectiveURI(uri), cookie);
			}
		}

	}

	private static <T> void addIndex(Map<T, List<HttpCookie>> indexStore, T index, HttpCookie cookie) {
		if (index != null) {
			List<HttpCookie> cookies = indexStore.get(index);
			if (cookies != null) {
				// there may already have the same cookie, so remove it first
				cookies.remove(cookie);

				cookies.add(cookie);
			} else {
				cookies = new ArrayList<HttpCookie>();
				cookies.add(cookie);
				indexStore.put(index, cookies);
			}
		}
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		Jar jar = jarHolder.get();
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		boolean secureLink = "https".equalsIgnoreCase(uri.getScheme());
		// check domainIndex first
		getByDomain(cookies, jar, uri.getHost(), secureLink);
		// check uriIndex then
		getByURI(cookies, jar, getEffectiveURI(uri), secureLink);

		return cookies;
	}

	/*
	 * This is almost the same as HttpCookie.domainMatches except for one
	 * difference: It won't reject cookies when the 'H' part of the domain
	 * contains a dot ('.'). I.E.: RFC 2965 section 3.3.2 says that if host is
	 * x.y.domain.com and the cookie domain is .domain.com, then it should be
	 * rejected. However that's not how the real world works. Browsers don't
	 * reject and some sites, like yahoo.com do actually expect these cookies to
	 * be passed along. And should be used for 'old' style cookies (aka Netscape
	 * type of cookies)
	 */
	private static boolean netscapeDomainMatches(String domain, String host) {
		if (domain == null || host == null) {
			return false;
		}

		// if there's no embedded dot in domain and domain is not .local
		boolean isLocalDomain = ".local".equalsIgnoreCase(domain);
		int embeddedDotInDomain = domain.indexOf('.');
		if (embeddedDotInDomain == 0) {
			embeddedDotInDomain = domain.indexOf('.', 1);
		}
		if (!isLocalDomain && (embeddedDotInDomain == -1 || embeddedDotInDomain == domain.length() - 1)) {
			return false;
		}

		// if the host name contains no dot and the domain name is .local
		int firstDotInHost = host.indexOf('.');
		if (firstDotInHost == -1 && isLocalDomain) {
			return true;
		}

		int domainLength = domain.length();
		int lengthDiff = host.length() - domainLength;
		if (lengthDiff == 0) {
			// if the host name and the domain name are just string-compare
			// euqal
			return host.equalsIgnoreCase(domain);
		} else if (lengthDiff > 0) {
			// need to check H & D component
			String D = host.substring(lengthDiff);
			return (D.equalsIgnoreCase(domain));
		} else if (lengthDiff == -1) {
			// if domain is actually .host
			return (domain.charAt(0) == '.' && host.equalsIgnoreCase(domain.substring(1)));
		}

		return false;
	}

	private void getByDomain(List<HttpCookie> cookies, Jar jar, String host, boolean secureLink) {
		Map<String, List<HttpCookie>> cookieIndex = jar.byDomain;
		ArrayList<HttpCookie> cookieJar = jar.cookieJar;

		// Use a separate list to handle cookies that need to be removed so
		// that there is no conflict with iterators.
		ArrayList<HttpCookie> toRemove = new ArrayList<HttpCookie>();
		for (Map.Entry<String, List<HttpCookie>> entry : cookieIndex.entrySet()) {
			String domain = entry.getKey();
			List<HttpCookie> lst = entry.getValue();
			for (HttpCookie c : lst) {
				if ((c.getVersion() == 0 && netscapeDomainMatches(domain, host))
						|| (c.getVersion() == 1 && HttpCookie.domainMatches(domain, host))) {
					if ((cookieJar.indexOf(c) != -1)) {
						// the cookie still in main cookie store
						if (!c.hasExpired()) {
							// don't add twice and make sure it's the proper
							// security level
							if ((secureLink || !c.getSecure()) && !cookies.contains(c)) {
								cookies.add(c);
							}
						} else {
							toRemove.add(c);
						}
					} else {
						// the cookie has been removed from main store,
						// so also remove it from domain indexed store
						toRemove.add(c);
					}
				}
			}
			// Clear up the cookies that need to be removed
			for (HttpCookie c : toRemove) {
				lst.remove(c);
				cookieJar.remove(c);
			}
			toRemove.clear();
		}
	}

	// @param cookies [OUT] contains the found cookies
	// @param cookieIndex the index
	// @param comparator the prediction to decide whether or not
	// a cookie in index should be returned
	private void getByURI(List<HttpCookie> cookies, Jar jar, URI comparator, boolean secureLink) {
		Map<URI, List<HttpCookie>> cookieIndex = jar.byURI;
		ArrayList<HttpCookie> cookieJar = jar.cookieJar;

		for (URI index : cookieIndex.keySet()) {
			if (comparator.compareTo(index) == 0) {
				List<HttpCookie> indexedCookies = cookieIndex.get(index);
				// check the list of cookies associated with this domain
				if (indexedCookies != null) {
					Iterator<HttpCookie> it = indexedCookies.iterator();
					while (it.hasNext()) {
						HttpCookie ck = it.next();
						if (cookieJar.indexOf(ck) != -1) {
							// the cookie still in main cookie store
							if (!ck.hasExpired()) {
								// don't add twice
								if ((secureLink || !ck.getSecure()) && !cookies.contains(ck))
									cookies.add(ck);
							} else {
								it.remove();
								cookieJar.remove(ck);
							}
						} else {
							// the cookie has beed removed from main store,
							// so also remove it from domain indexed store
							it.remove();
						}
					}
				} // end of indexedCookies != null
			} // end of comparator.compareTo(index) == 0
		} // end of cookieIndex iteration
	}

	@Override
	public List<HttpCookie> getCookies() {
		Jar jar = jarHolder.get();
		Iterator<HttpCookie> it = jar.cookieJar.iterator();
		while (it.hasNext()) {
			if (it.next().hasExpired()) {
				it.remove();
			}
		}

		return Collections.unmodifiableList(jar.cookieJar);
	}

	@Override
	public List<URI> getURIs() {
		Jar jar = jarHolder.get();
		HashMap<URI, List<HttpCookie>> byURI = jar.byURI;

		// clean up byURI mapping
		Iterator<URI> it = byURI.keySet().iterator();
		while (it.hasNext()) {
			URI uri = it.next();
			List<HttpCookie> cookies = byURI.get(uri);
			if (cookies == null || cookies.isEmpty()) {
				// no cookies list or an empty list associated with
				// this URI entry, delete it
				it.remove();
			}
		}

		return new ArrayList<URI>(byURI.keySet());
	}

	@Override
	public boolean remove(URI arg0, HttpCookie ck) {
		return jarHolder.get().cookieJar.remove(ck);
	}

	@Override
	public boolean removeAll() {
		Jar jar = jarHolder.get();

		if (jar.cookieJar.isEmpty()) {
			return false;
		}
		jar.cookieJar.clear();
		jar.byDomain.clear();
		jar.byURI.clear();

		return true;
	}

	//
	// for cookie purpose, the effective URI should only be http://host
	// the path will be taken into account when path-match algorithm applied
	//
	private static URI getEffectiveURI(URI uri) {
		URI effectiveURI = null;
		try {
			effectiveURI = new URI("http", uri.getHost(), null, null, null);
		} catch (URISyntaxException ignored) {
			effectiveURI = uri;
		}

		return effectiveURI;
	}
}
