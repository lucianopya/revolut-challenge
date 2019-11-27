package revolut.challenge;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.challenge.router.Router;
import spark.Spark;

import java.util.Properties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {

            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties"));
            String prop = properties.getProperty("port");

            int port = StringUtils.isNotBlank(prop) ? Integer.parseInt(prop) : 8080;

            Spark.port(port);
            new Router().init();

            logger.info("Listening on http://localhost:{}/", port);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize server");
        }
    }

}
