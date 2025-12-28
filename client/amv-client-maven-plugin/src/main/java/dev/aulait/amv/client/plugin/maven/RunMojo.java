package dev.aulait.amv.client.plugin.maven;

import dev.aulait.amv.client.AmvClient;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "run")
@Getter
public class RunMojo extends AbstractMojo {

  @Parameter(property = "baseUrl")
  private String baseUrl;

  @Parameter(property = "codebaseName", required = true)
  private String codebaseName;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    AmvClient client = new AmvClient();

    if (baseUrl != null && !baseUrl.isEmpty()) {
      client.setBaseUrl(baseUrl);
    }

    client.saveAndAnalyzeCodebase(codebaseName);
  }
}
