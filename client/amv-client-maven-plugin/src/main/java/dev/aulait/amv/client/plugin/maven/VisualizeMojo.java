package dev.aulait.amv.client.plugin.maven;

import dev.aulait.amv.client.AmvClient;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "visualize", aggregator = true)
@Getter
public class VisualizeMojo extends AbstractMojo {

  @Parameter(property = "codebaseName", required = true)
  private String codebaseName;

  @Parameter(property = "qualifiedTypeName", required = true)
  private String qualifiedTypeName;

  @Parameter(property = "apiBaseUrl")
  private String apiBaseUrl;

  @Parameter(property = "browserBaseUrl")
  private String browserBaseUrl;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("AMV visualize: starting");
    getLog()
        .info(
            "AMV visualize: codebaseName="
                + codebaseName
                + ", qualifiedTypeName="
                + qualifiedTypeName);

    AmvClient client = AmvClient.createDefault();

    if (apiBaseUrl != null && !apiBaseUrl.isEmpty()) {
      client.setApiBaseUrl(apiBaseUrl);
    }

    if (browserBaseUrl != null && !browserBaseUrl.isEmpty()) {
      client.setBrowserUrl(browserBaseUrl);
    }

    try {
      client.visualize(codebaseName, qualifiedTypeName);
      getLog().info("AMV visualize: completed");
    } catch (RuntimeException e) {
      throw new MojoExecutionException("AMV visualize failed: " + e.getMessage(), e);
    }
  }
}
