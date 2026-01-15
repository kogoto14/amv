package dev.aulait.amv.browser;

import java.util.Objects;

public class URLBuilder {

  private static final String CLASS_DIAGRAM_PATH = "/diagrams/class";
  private static final String CLASS_DIAGRAM_TEMPLATE =
      "(classDiagramCriteria:(depth:10,qualifiedSignature:%s),"
          + "pageControl:(pageNumber:1),typeSearchCriteria:(text:%s))";

  private final String browserBaseUrl;

  public URLBuilder(String browserBaseUrl) {
    this.browserBaseUrl = normalizeBaseUrl(browserBaseUrl);
  }

  public String buildClassDiagramUrl(String qualifiedTypeName) {
    String sanitizedQualifiedTypeName = sanitizeQualifiedTypeName(qualifiedTypeName);
    String query =
        CLASS_DIAGRAM_TEMPLATE.formatted(sanitizedQualifiedTypeName, sanitizedQualifiedTypeName);
    return browserBaseUrl + CLASS_DIAGRAM_PATH + "?c=" + query;
  }

  private String sanitizeQualifiedTypeName(String qualifiedTypeName) {
    String value = Objects.requireNonNull(qualifiedTypeName, "qualifiedTypeName").trim();
    if (value.isEmpty()) {
      throw new IllegalArgumentException("qualifiedTypeName must not be blank");
    }
    return value.replace("'", "%27");
  }

  private String normalizeBaseUrl(String browserBaseUrl) {
    String value = Objects.requireNonNull(browserBaseUrl, "browserBaseUrl").trim();
    if (value.isEmpty()) {
      throw new IllegalArgumentException("browserBaseUrl must not be blank");
    }
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
