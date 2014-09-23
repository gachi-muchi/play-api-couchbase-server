package service.impl;

import java.util.Map;

import play.libs.Json;
import service.AuthService;
import service.UserService;

import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.BucketType;
import dao.CouchbaseClientFactory;
import entity.AuthToken;
import entity.User;

@Singleton
public class AuthServiceImpl implements AuthService {

	private static final int AUTH_LIMIT_SECONDS = 24 * 60 * 60;

	private CouchbaseClient tokenClient;

	private UserService userService;

	@Inject
	public AuthServiceImpl(CouchbaseClientFactory factory,
			UserService userService) {
		tokenClient = factory.getClient(BucketType.USER);
		this.userService = userService;
	}

	@Override
	public AuthToken createToken(String mail, String password) {
		User user = userService.read(mail, password);
		if (user == null) {
			return null;
		}
		String token = (String) tokenClient.get("userId." + user.getUserId());
		AuthToken authToken;
		if (token != null) {
			authToken = confirmToken(token);
			if (authToken != null) {
				return authToken;
			}
		}

		token = Integer.toHexString(new String(user.getPassword()
				+ System.currentTimeMillis()).hashCode());
		authToken = new AuthToken();
		authToken.setUserId(user.getUserId());
		authToken.setToken(token);
		tokenClient.set("userId." + user.getUserId(), token);
		tokenClient.set("token." + token, AUTH_LIMIT_SECONDS,
				Json.stringify(Json.toJson(authToken)));
		authToken.setLimit(Long.valueOf(AUTH_LIMIT_SECONDS));
		return authToken;
	}

	@Override
	public AuthToken confirmToken(String token) {
		String json = (String) tokenClient.get("token." + token);
		if (json == null) {
			return null;
		}
		JsonNode node = Json.parse(json);
		if (node == null) {
			return null;
		}
		AuthToken authToken = Json.fromJson(node, AuthToken.class);
		try {
			Map<String, String> map = tokenClient.getKeyStats("token." + token)
					.get();
			authToken.setLimit(Long.valueOf(map.get("key_exptime"))
					- System.currentTimeMillis() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authToken;
	}

	@Override
	public boolean deleteToken(String token) {
		tokenClient.delete("token." + token);
		return true;
	}

}
