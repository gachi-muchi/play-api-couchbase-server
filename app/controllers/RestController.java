package controllers;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import entity.AuthToken;

import play.mvc.Controller;
import service.AuthService;

public abstract class RestController extends Controller {

	@Inject
	private AuthService authService;
	
	protected AuthToken authorize() {
		String token = request().getHeader(Headers.AUTHORIZATION);
		if (Strings.isNullOrEmpty(token)) {
			return null;
		}
		AuthToken authToken = authService.confirmToken(token);
		if (authToken == null) {
			return null;
		}
		return authToken;
	}
}
