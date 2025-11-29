package dev.aulait.amv.arch.test;

import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@Slf4j
@Getter
@ToString
public class TestConfig {

  private static TestConfig instance;

  private String baseUrl;

  private String wsBaseUrl;

  private boolean usingContainer;

  public static TestConfig getInstance() {
    if (instance == null) {
      instance = new TestConfig();
      instance.load();
    }
    return instance;
  }

  void load() {
    Config config = ConfigProvider.getConfig();

    Optional<String> testHost = config.getOptionalValue("quarkus.http.test-host", String.class);

    // If quarkus.http.test-host is not set, it means that the test is running inside a container.
    // This is based on @QuarkusIntegrationTest specification that it launches the container without
    // setting quarkus.http.test-host.
    // See:
    // https://quarkus.io/guides/getting-started-testing#executing-against-a-running-application
    usingContainer = !testHost.isPresent();

    Optional<Integer> testPort = config.getOptionalValue("quarkus.http.test-port", Integer.class);
    Optional<String> restPath = config.getOptionalValue("quarkus.rest.path", String.class);

    String baseDomain = testHost.orElse("localhost") + ":" + testPort.orElse(8080);

    baseUrl = "http://" + baseDomain + restPath.orElse("/");

    wsBaseUrl = "ws://" + baseDomain + "/";

    log.info("Config: {}", this);
  }
}
