package entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.handler.codec.http.QueryStringEncoder;

import com.google.common.collect.Sets;

public class PagingBuilder {

	private static final Set<String> RESERVED_WORDS = Sets.newHashSet("offset",
			"limit");
	
	private String baseUrl;

	private Map<String, List<String>> params = new HashMap<>();

	private Integer offset;

	private Integer limit;

	public PagingBuilder(String baseUrl, Integer offset, Integer limit) {
		this.baseUrl = baseUrl;
		this.offset = offset;
		this.limit = limit;
	}

	public PagingBuilder setParam(Map<String, String[]> params) {
		for (Entry<String, String[]> entry : params.entrySet()) {
			if (!RESERVED_WORDS.contains(entry.getKey())) {
				this.params.put(entry.getKey(), Arrays.asList(entry.getValue()));
			}
		}
		return this;
	}

	public Paging build() {
		QueryStringEncoder nextEncoder = new QueryStringEncoder(baseUrl);
		QueryStringEncoder previousEncoder = new QueryStringEncoder(baseUrl);
		for (Entry<String, List<String>> entry : params.entrySet()) {
			for (String value : entry.getValue()) {
				nextEncoder.addParam(entry.getKey(), value);
				previousEncoder.addParam(entry.getKey(), value);
			}
		}

		if (limit > 0) {
			nextEncoder.addParam("limit", String.valueOf(limit));
			previousEncoder.addParam("limit", String.valueOf(limit));
		}

		Paging paging = new Paging();
		if (limit > 0) {
			int nextOffset = offset + limit;
			nextEncoder.addParam("offset", String.valueOf(nextOffset));
			paging.setNext(nextEncoder.toString());
			if (offset > 0) {
				int previousOffset = offset - limit;
				if (previousOffset > 0) {
					previousEncoder.addParam("offset",
							String.valueOf(previousOffset));
				}
				paging.setPrevious(previousEncoder.toString());
			}
		}
		return paging;
	}

}
