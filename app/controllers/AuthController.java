package controllers;

import java.io.IOException;
import java.net.URISyntaxException;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import service.AuthService;

import batch.RegisterDataBatch;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import entity.AuthToken;
import entity.LoginRequest;

@Singleton
public class AuthController extends RestController {

	@Inject
	private AuthService authService;

	@BodyParser.Of(BodyParser.TolerantJson.class)
	public Result createToken() {
//		LoginRequest request = Json.fromJson(request().body().asJson(),
//				LoginRequest.class);
//		if (request == null || Strings.isNullOrEmpty(request.getMail())
//				|| Strings.isNullOrEmpty(request.getPassword())) {
//			return badRequest();
//		}
//		AuthToken token = authService.createToken(request.getMail(),
//				request.getPassword());
//		if (token == null) {
//			return badRequest();
//		}
		try {
			RegisterDataBatch.main();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok();
	}

	public Result confirmToken(String token) {
		AuthToken authToken = authService.confirmToken(token);
		if (authToken == null) {
			return notFound();
		}
		return ok(Json.toJson(authToken));
	}

	@BodyParser.Of(BodyParser.TolerantJson.class)
	public Result deleteToken(String token) {
		return ok(Json.toJson(authService.deleteToken(token)));
	}

}
