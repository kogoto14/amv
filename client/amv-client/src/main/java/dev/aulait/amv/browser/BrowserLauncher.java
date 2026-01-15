package dev.aulait.amv.browser;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class BrowserLauncher {

  public void open(String url) {
    String targetUrl = Objects.requireNonNull(url, "url");
    if (isDevContainer()) {
      exec("code", "--openExternal", targetUrl);
      return;
    }
    try {
      Desktop.getDesktop().browse(new URI(targetUrl));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private boolean isDevContainer() {
    return "true".equals(System.getenv("REMOTE_CONTAINERS"))
        || "true".equals(System.getenv("CODESPACES"));
  }

  private int exec(String... command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);

    try {
      Process process = processBuilder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        reader.lines().forEach(System.out::println);
      }

      return process.waitFor();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
