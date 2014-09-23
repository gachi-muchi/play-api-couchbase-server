package controllers;

import java.util.List;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import service.ArticleService;
import service.AuthService;
import service.LikeService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import entity.Article;
import entity.Paging;
import entity.PagingBuilder;
import entity.StructuredEntity;

@Singleton
public class ArticleController extends RestController {

	@Inject
	private AuthService authService;

	@Inject
	private ArticleService articleService;

	@Inject
	private LikeService likeService;

	@BodyParser.Of(BodyParser.TolerantJson.class)
	public Result create(String userId) {
//		AuthToken authToken = authorize();
//		if (authToken == null || !userId.equals(authToken.getUserId())) {
//			return badRequest();
//		}
		Article article = Json.fromJson(request().body().asJson(),
				Article.class);
		return ok(Json.toJson(articleService.create(userId, article)));
	}

	public Result read(String userId, String articleId) {
//		AuthToken authToken = authorize();
//		if (authToken == null) {
//			return badRequest();
//		}
		Article article = articleService.read(articleId);
		if (article == null) {
			return notFound();
		}
		return ok(Json.toJson(article));
	}

	public Result list(String userId, Integer offset, Integer limit) {
//		AuthToken authToken = authorize();
//		if (authToken == null) {
//			return badRequest();
//		}
		StructuredEntity<Article> results = new StructuredEntity<>();
		List<Article> list = articleService.list(userId, offset, limit);
		results.setData(list);
		Paging paging = new PagingBuilder("http://" + request().host()
				+ request().path(), offset, limit).setParam(
				request().queryString()).build();
		results.setPaging(paging);
		return ok(Json.toJson(results));
	}

	@BodyParser.Of(BodyParser.TolerantJson.class)
	public Result update(String userId, String articleId) {
//		AuthToken authToken = authorize();
//		if (authToken == null || !userId.equals(authToken.getUserId())) {
//			return badRequest();
//		}
		Article article = Json.fromJson(request().body().asJson(),
				Article.class);
		return ok(Json.toJson(articleService.update(articleId, article)));
	}

	@BodyParser.Of(value = BodyParser.TolerantText.class)
	public Result delete(String userId, String articleId) {
//		AuthToken authToken = authorize();
//		if (authToken == null || !userId.equals(authToken.getUserId())) {
//			return badRequest();
//		}
		return ok(Json.toJson(articleService.delete(userId, articleId)));
	}

	@BodyParser.Of(value = BodyParser.TolerantText.class)
	public Result like(String userId, String articleId) {
//		AuthToken authToken = authorize();
//		if (authToken == null) {
//			return badRequest();
//		}
		return ok(Json.toJson(likeService.like(articleId, userId)));
	}

	public Result getLikeCount(String userId, String articleId) {
		return ok(Json.toJson(likeService.getCount(articleId)));
	}

	@BodyParser.Of(value = BodyParser.TolerantText.class)
	public Result unLike(String userId, String articleId) {
//		AuthToken authToken = authorize();
//		if (authToken == null) {
//			return badRequest();
//		}
		return ok(Json.toJson(likeService.unLike(articleId, userId)));
	}

}
