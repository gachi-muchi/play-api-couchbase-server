package batch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import play.libs.Json;
import service.GlobalCounterService;
import service.impl.GlobalCounterServiceImpl;

import com.couchbase.client.CouchbaseClient;

import dao.BucketType;
import dao.CouchbaseClientFactory;
import entity.Article;
import entity.User;

public class RegisterDataBatch {

	public static void main(String... args) throws URISyntaxException,
			IOException {
		CouchbaseClientFactory factory = new CouchbaseClientFactory(
				Arrays.asList("http://10.34.49.64:8091/pools",
						"http://10.34.48.239:8091/pools"));
		GlobalCounterService globalCounterService = new GlobalCounterServiceImpl(
				factory);
		List<String> userIds = new ArrayList<>();
		for (int i = 1; i <= 10000; i++) {
			userIds.add(String.valueOf(i));
		}
//		{
//			List<RegisterUserThread> threads = new ArrayList<>();
//			for (int i = 0; i < 10; i++) {
//				RegisterUserThread thread = new RegisterUserThread(factory,
//						globalCounterService, 1000);
//				thread.start();
//				threads.add(thread);
//			}
//
//			for (RegisterUserThread thread : threads) {
//				try {
//					thread.join();
//					System.out.println(thread.getId() + "\t" + thread.getTime()
//							+ "ms");
//					userIds.addAll(thread.getUserIds());
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		{
			List<RegisterArticleThread> threads = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				int from = (userIds.size() / 10) * i;
				int to = (userIds.size() / 10) * (i + 1);
				if (to > userIds.size()) {
					to = userIds.size();
				}
				RegisterArticleThread thread = new RegisterArticleThread(
						factory, globalCounterService,
						userIds.subList(from, to));
				thread.start();
				threads.add(thread);
			}

			for (RegisterArticleThread thread : threads) {
				try {
					thread.join();
					System.out.println(thread.getId() + "\t" + thread.getTime()
							+ "ms");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		factory.shutdonw();
	}

	public static class RegisterArticleThread extends Thread {
		private static final String ARTICLE_TEST_BODY_BASE = "あいうえおあいうえおあいうえおあいうえおあいうえおあいうえおあいうえおあいうえおあいうえおあいうえお";
		private static String ARTICLE_TEST_BODY = ARTICLE_TEST_BODY_BASE;

		private GlobalCounterService globalCounterService;

		private CouchbaseClient articleClient;

		private List<String> userIds;

		private long start;

		private long end;

		public RegisterArticleThread(CouchbaseClientFactory factory,
				GlobalCounterService globalCounterService, List<String> userIds) {
			this.globalCounterService = globalCounterService;
			articleClient = factory.getClient(BucketType.ARTICLE);
			this.userIds = userIds;
			for (int i = 0; i < 2; i++) {
				ARTICLE_TEST_BODY += ARTICLE_TEST_BODY_BASE;
			}
		}

		@Override
		public void run() {
			start = System.currentTimeMillis();
			for (String userId : userIds) {
				List<String> keys = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					Article article = new Article();
					String articleId = globalCounterService.generateId();
					article.setArticleId(articleId);
					article.setUserId(userId);
					article.setTitle("Hello World");
					article.setBody(ARTICLE_TEST_BODY);
					article.setCreateTime(new Date());
					articleClient.set(articleId, Json.stringify(Json.toJson(article)));
					keys.add(articleId);
				}
				articleClient.set("index_" + userId, Json.stringify(Json.toJson(keys)));
			}
			end = System.currentTimeMillis();
			System.out.println(this.getId() + "\t" + (end - start) + "ms");
		}

		public long getTime() {
			return end - start;
		}
	}

	public static class RegisterUserThread extends Thread {
		private GlobalCounterService globalCounterService;
		private CouchbaseClient userClient;

		private int amount;

		private List<String> userIds = new ArrayList<>();

		private long start;

		private long end;

		public RegisterUserThread(CouchbaseClientFactory factory,
				GlobalCounterService globalCounterService, int amount) {
			this.globalCounterService = globalCounterService;
			userClient = factory.getClient(BucketType.USER);
			this.amount = amount;
		}

		@Override
		public void run() {
			start = System.currentTimeMillis();
			for (int i = 0; i < amount; i++) {
				User user = new User();
				String id = globalCounterService.generateId();
				user.setUserId(id);
				user.setName("name_" + id);
				user.setMail("mail_" + id);
				user.setPassword("pass_" + id);
				if (userClient.set(id, Json.stringify(Json.toJson(user))).getStatus().isSuccess()) {
					userIds.add(id);
				}
			}
			end = System.currentTimeMillis();
		}

		public long getTime() {
			return end - start;
		}

		public List<String> getUserIds() {
			return userIds;
		}

	}
}
