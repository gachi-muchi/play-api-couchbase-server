package service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import service.ArticleService;
import service.GlobalCounterService;
import utils.JsonUtils;

import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.BucketType;
import dao.CouchbaseClientFactory;
import entity.Article;

@Singleton
public class ArticleServiceImpl implements ArticleService {

	private CouchbaseClient articleClient;

	private GlobalCounterService globalCounterService;

	@Inject
	public ArticleServiceImpl(CouchbaseClientFactory factory,
			GlobalCounterService globalCounterService) {
		articleClient = factory.getClient(BucketType.ARTICLE);
		this.globalCounterService = globalCounterService;
	}

	@Override
	public Article create(String userId, Article article) {
		String id = globalCounterService.generateId();
		article.setArticleId(id);
		article.setUserId(userId);
		article.setCreateTime(new Date());
		CASValue<Object> casValue = articleClient.getAndLock("index_" + userId,
				1);
		if (casValue == null) {
			List<String> articleIds = new ArrayList<>();
			articleIds.add(id);
			articleClient.set("index_" + userId, articleIds);
			articleClient.set(id, JsonUtils.toString(article));
			return article;
		}
		ArrayNode node = (ArrayNode) JsonUtils.toNode((String) casValue
				.getValue());
		node.add(id);
		CASResponse response = articleClient.cas("index_" + userId,
				casValue.getCas(), JsonUtils.toString(node));
		if (response == CASResponse.OK) {
			articleClient.set(id, JsonUtils.toString(article));
			return article;
		}
		return null;
	}

	@Override
	public boolean update(String id, Article update) {
		CASValue<Object> casValue = articleClient.getAndLock(id, 1);
		if (casValue == null) {
			return false;
		}
		Article article = JsonUtils.toObject(
				JsonUtils.toNode((String) casValue.getValue()), Article.class);
		if (article == null) {
			return false;
		}
		if (update.getTitle() != null && "".equals(update.getTitle())) {
			article.setTitle(update.getTitle());
		}
		if (update.getBody() != null && "".equals(update.getBody())) {
			article.setBody(update.getBody());
		}
		article.setUpdateTime(new Date());
		CASResponse response = articleClient.cas(id, casValue.getCas(),
				JsonUtils.toString(article));
		return response == CASResponse.OK;
	}

	@Override
	public List<Article> list(String userId, int offset, int limit) {
		String keyStr = (String) articleClient.get("index_" + userId);
		TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {
		};
		List<String> keys = JsonUtils.toObject(keyStr, typeReference);
		Map<String, Object> map = articleClient.getBulk(keys);
		List<Article> list = new ArrayList<>();
		for (String key : keys) {
			String obj = (String) map.get(key);
			if (obj != null) {
				list.add(JsonUtils.toObject(
						JsonUtils.toNode((String) map.get(key)), Article.class));
			}
		}
		return list;
	}

	@Override
	public Article read(String id) {
		JsonNode node = JsonUtils.toNode((String) articleClient.get(id));
		if (node == null) {
			return null;
		}
		return JsonUtils.toObject(node, Article.class);
	}

	@Override
	public boolean delete(String userId, String id) {
		try {
			String keyStr = (String) articleClient.get("index_" + userId);
			TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {
			};
			List<String> keys = JsonUtils.toObject(keyStr, typeReference);
			keys.remove(id);
			articleClient.set("index_" + userId, JsonUtils.toString(keys));
			return articleClient.delete(id).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

}
