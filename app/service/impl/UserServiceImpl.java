package service.impl;

import play.libs.Json;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import service.UserService;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.BucketType;
import dao.CouchbaseClientFactory;
import entity.User;

@Singleton
public class UserServiceImpl implements UserService {

	private CouchbaseClient client;

	@Inject
	public UserServiceImpl(CouchbaseClientFactory factory) {
		client = factory.getClient(BucketType.USER);
	}

	@Override
	public User create(User user) {
		return null;
	}

	@Override
	public User read(String id) {
		JsonNode node = Json.toJson((String) client.get(id));
		if (node == null) {
			return null;
		}
		return Json.fromJson(node, User.class);
	}

	@Override
	public User read(String mail, String password) {
		View view = client.getView("users", "by_mail_and_password");
		Query query = new Query();
		query.setIncludeDocs(true);
		query.setKey(Json.stringify(Json.toJson(mail + "." + password)));
		ViewResponse response = client.query(view, query);
		if (response.size() == 0) {
			return null;
		}
		ViewRow row = response.iterator().next();
		JsonNode node = Json.parse(row.getValue());
		return Json.fromJson(node, User.class);
	}

	@Override
	public boolean update(String id, User update) {
		CASValue<Object> casValue = client.getAndLock(id, 1);
		if (casValue == null) {
			return false;
		}
		JsonNode node = Json.parse((String) casValue.getValue());
		if (node == null) {
			return false;
		}
		User user = Json.fromJson(node, User.class);
		if (!Strings.isNullOrEmpty(update.getName())) {
			user.setName(user.getName());
		} else if (!Strings.isNullOrEmpty(update.getMail())) {
			user.setName(user.getMail());
		} else if (!Strings.isNullOrEmpty(update.getPassword())) {
			user.setName(user.getPassword());
		}
		CASResponse response = client.cas(id, casValue.getCas(), user);
		return response == CASResponse.OK;
	}

}
