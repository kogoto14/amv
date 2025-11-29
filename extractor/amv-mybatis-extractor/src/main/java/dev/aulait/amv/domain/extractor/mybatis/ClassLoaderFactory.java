package dev.aulait.amv.domain.extractor.mybatis;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassLoaderFactory {

  public static ClassLoader createClassLoader(String classpath) {
    List<URL> urls =
        Stream.of(classpath.split(File.pathSeparator)).map(ClassLoaderFactory::str2url).toList();

    log.debug("Creating class loader with URLs: {}", urls);

    ClassLoader parent = Thread.currentThread().getContextClassLoader();
    var classLoader =
        new URLClassLoader("MyBatisExtractorClassLoader", urls.toArray(new URL[0]), parent);

    return classLoader;
  }

  public static URL str2url(String str) {
    try {
      return Path.of(str).toUri().toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }
}
