package service.impl;

import java.util.Date;

import play.libs.Json;
import service.LikeService;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.BucketType;
import dao.CouchbaseClientFactory;
import entity.Like;

@Singleton
public class LikeServiceImpl implements LikeService {

	private CouchbaseClient likeClient;

	@Inject
	public LikeServiceImpl(CouchbaseClientFactory factory) {
		likeClient = factory.getClient(BucketType.LIKE);
	}

	@Override
	public long like(String id, String userId) {
		if (likeClient.get(id + "." + userId) != null) {
			return getCount(id);
		}

		Like like = new Like();
		like.setToId(id);
		like.setUserId(userId);
		like.setDate(new Date());
		likeClient.set(id + "." + userId, Json.stringify(Json.toJson(like)));

		return likeClient.incr(id + ".count", 1, 1);
	}

	@Override
	public long unLike(String id, String userId) {
		if (likeClient.get(id + "." + userId) == null) {
			return getCount(id);
		}
		likeClient.delete(id + "." + userId);
		return likeClient.decr(id + ".count", 1);
	}

	@Override
	public long getCount(String id) {
		Object obj = likeClient.get(id + ".count");
		return obj == null ? 0L : Long.valueOf((String) obj);
	}

}
