package dev.aulait.amv.arch.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

  public static Path res2path(Object owner, String resource) {
    return Path.of(res2uri(owner, resource));
  }

  private static URI res2uri(Object owner, String resource) {
    try {
      return res2url(owner, resource).toURI();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static URL res2url(Object owner, String resource) {
    URL resUrl = owner.getClass().getResource(resource);

    if (resUrl != null) {
      return resUrl;
    }

    String ownerName = owner.getClass().getSimpleName();

    String ownerResource = ownerName + "/" + resource;
    resUrl = owner.getClass().getResource(ownerResource);
    if (resUrl != null) {
      return resUrl;
    }

    if (ownerName.endsWith("Tests")) {
      ownerName = ownerName.substring(0, ownerName.length() - 1);
    }
    ownerResource = ownerName + "Resources/" + resource;

    return owner.getClass().getResource(ownerResource);
  }
}
