package ca.paulshin.yunatube.image.gifs.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GifMemoryCache {
	private Map<String, String> cache = Collections.synchronizedMap(new LinkedHashMap<String, String>(10, 1.5f, true));// Last argument true for LRU ordering

	public GifMemoryCache() {
	}

	public String get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			// NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void put(String id, String path) {
		cache.put(id, path);
	}

	public void clear() {
		try {
			// NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
}