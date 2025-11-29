package dev.aulait.amv.arch.test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtils4Test {

  public static void deleteOnExit(Path dir) {

    if (dir == null || !Files.exists(dir)) return;

    try (Stream<Path> s = Files.walk(dir)) {
      s.sorted(java.util.Comparator.comparingInt(Path::getNameCount))
          .map(Path::toFile)
          .forEach(File::deleteOnExit);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
