
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Results;
import play.mvc.SimpleResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

import dao.CouchbaseClientFactory;

public class Global extends GlobalSettings {

    private static final Injector INJECTOR = createInjector();

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started.");
    }

    @Override
    public void onStop(Application app) {
        Logger.info(("Application shutdown..."));
        CouchbaseClientFactory factory = INJECTOR.getInstance(CouchbaseClientFactory.class);
        factory.shutdonw();
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return INJECTOR.getInstance(controllerClass);
    }

    @Override
    public Promise<SimpleResult> onHandlerNotFound(Http.RequestHeader request) {
        JsonNode json = buildErrorJson(Http.Status.NOT_FOUND, "http.NotFound", "The requested URL was not found.");
        return Promise.<SimpleResult>pure(Results.notFound(json));
    }

    @Override
    public Promise<SimpleResult> onBadRequest(Http.RequestHeader request, String error) {
        JsonNode json = buildErrorJson(Http.Status.BAD_REQUEST, "http.BadRequest", "The sent request is the could not understand.");
        return Promise.<SimpleResult>pure(Results.badRequest(json));
    }

    private static Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CouchbaseClientFactory.class).in(Scopes.SINGLETON);
            }
        });
    }

    private JsonNode buildErrorJson(int status, String code, String message) {
        ObjectNode json = Json.newObject();
        json.put("status", status);
        json.put("code", code);
        json.put("message", message);
        return json;
    }

}