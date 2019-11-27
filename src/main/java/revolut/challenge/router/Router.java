package revolut.challenge.router;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.challenge.controllers.AccountController;
import revolut.challenge.controllers.TransferController;
import revolut.challenge.exceptions.ApiException;
import revolut.challenge.injection.BeansBinder;
import revolut.challenge.utils.ExceptionUtils;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.servlet.http.HttpServletResponse;

import static revolut.challenge.utils.CustomJsonUtils.json;


public class Router implements SparkApplication {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    @Override
    public void init() {
        Spark.after((request, response) -> setHeaders(response));

        setupRequestExceptionHandling();
        setupNotFound();
        setUpPing();

        // Create injectors and controllers with routes
        Injector injector = Guice.createInjector(new BeansBinder());

        AccountController accountController = injector.getInstance(AccountController.class);
        TransferController transferController = injector.getInstance(TransferController.class);

        Spark.path("/v1/", () -> {
            Spark.get("accounts/:accountId", accountController::getAccount, json());
            Spark.post("accounts", accountController::createAccount, json());
            Spark.get("accounts/:accountId/transfers", accountController::listTransferByAccount, json());

            Spark.get("transfers", transferController::transferHistory, json());
            Spark.post("transfers", transferController::makeTransfer, json());

        });
    }

    private void setHeaders(Response response) {
        if (!response.raw().containsHeader("Content-Type")) {
            response.header("Content-Type", "application/json");
        }
        response.header("Vary", "Accept,Accept-Encoding");
        response.header("Cache-Control", "max-age=0");
    }

    private void setupRequestExceptionHandling() {
        Spark.exception(Exception.class, (e, request, response) -> {
            Throwable t = ExceptionUtils.getFromChain(e, ApiException.class);

            ApiException apiException = t instanceof ApiException ?
                    (ApiException) t :
                    new ApiException("internal_error", "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            logger.error("Internal error", e);

            response.status(apiException.getStatusCode());
            response.body(apiException.toJson());
            setHeaders(response);
        });
    }

    private void setupNotFound() {
        Spark.notFound((request, response) -> {
            response.status(HttpServletResponse.SC_NOT_FOUND);

            ApiException e = new ApiException("route_not_found", String.format("Route %s not found", request.uri()), HttpServletResponse.SC_NOT_FOUND);
            logger.error("Internal error", e);

            return e.toJson();
        });
    }

    private void setUpPing() {
        Spark.get("/ping", (request, response) -> {

            response.status(HttpServletResponse.SC_OK);

            return "pong";
        });
    }
}
