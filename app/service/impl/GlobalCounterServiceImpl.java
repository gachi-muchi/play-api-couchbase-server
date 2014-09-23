package service.impl;

import service.GlobalCounterService;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.BucketType;
import dao.CouchbaseClientFactory;

@Singleton
public class GlobalCounterServiceImpl implements GlobalCounterService {

	private CouchbaseClient counterClient;

	@Inject
	public GlobalCounterServiceImpl(CouchbaseClientFactory factory) {
		counterClient = factory.getClient(BucketType.COUNTER);
	}

	@Override
	public String generateId() {
		return String.valueOf(counterClient.incr("global_id_counter", 1, 1));
	}
}
