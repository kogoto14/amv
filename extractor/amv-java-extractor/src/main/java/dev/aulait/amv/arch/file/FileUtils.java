package dev.aulait.amv.arch.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FileUtils {

  public static Path createDir(Path parentDir, String dirName) {
    return createDir(parentDir.resolve(dirName));
  }

  public static Path createDir(Path dir) {
    if (!Files.exists(dir)) {
      log.info("Creating directory: {}", dir.toAbsolutePath().normalize());

      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    return dir;
  }

  public static Path createTempFile(Path dir, String prefix, String suffix) {
    try {
      Path tempFile = Files.createTempFile(dir, prefix, suffix);
      log.debug("Created temporary file: {}", tempFile.toAbsolutePath().normalize());
      return tempFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String read(Path file) {
    try {
      Path fileAbs = file.toAbsolutePath().normalize();
      log.debug("Reading from {}", fileAbs);
      return Files.readString(fileAbs);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path write(Path file, String text) {
    try {

      Path fileAbs = file.toAbsolutePath().normalize();

      Path dir = fileAbs.getParent();
      if (!Files.exists(dir)) {
        log.info("Creating directory {}", dir);
        Files.createDirectories(dir);
      }

      log.debug("Writing to {}", fileAbs);
      return Files.writeString(fileAbs, text);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path copy(Path source, Path target) {
    try {
      Path sourceAbs = source.toAbsolutePath().normalize();
      Path targetAbs = target.toAbsolutePath().normalize();

      Path targetDir = targetAbs.getParent();
      if (!Files.exists(targetDir)) {
        log.info("Creating directory {}", targetDir);
        Files.createDirectories(targetDir);
      }

      log.debug("Copying from {} to {}", sourceAbs, targetAbs);
      return Files.copy(sourceAbs, targetAbs, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Stream<Path> collectMatchedPaths(Path sourceDir, String globPattern) {

    if (!Files.exists(sourceDir)) {
      log.warn("Source directory does not exist: {}", sourceDir);
      return Stream.empty();
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
    // TODO: Make configurable.
    // TODO: Make it subject to parsing equivalent to git mode.
    PathMatcher excludeMatcher =
        List.of("**/node_modules/**", "**/target/**", "**/build/**", "**/.git/**").stream()
            .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + pattern))
            .reduce((m1, m2) -> path -> m1.matches(path) || m2.matches(path))
            .orElse(null);

    List<Path> result = new ArrayList<>();

    FileVisitor<Path> visitor =
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
              throws IOException {
            if (excludeMatcher != null) {
              Path relativePath = sourceDir.relativize(dir);
              if (excludeMatcher.matches(relativePath)) {
                log.info("Excluding directory: {}", relativePath);
                return FileVisitResult.SKIP_SUBTREE;
              }
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            Path relativePath = sourceDir.relativize(file);
            if (matcher.matches(relativePath)) {
              result.add(file);
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            log.warn("Failed to visit file: {}", file, exc);
            return FileVisitResult.CONTINUE;
          }
        };

    try {
      Files.walkFileTree(
          sourceDir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, visitor);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return result.stream();
  }

  public static Stream<Path> collectMatchedDirs(Path sourceDir, String globPattern) {
    if (!Files.exists(sourceDir)) {
      return Stream.empty();
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

    try (Stream<Path> children = Files.list(sourceDir)) {
      return children
          .filter(path -> matcher.matches(path.getFileName()))
          .filter(Files::isDirectory)
          .collect(Collectors.toList())
          .stream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void delete(Path path) {
    if (!Files.exists(path)) {
      return;
    }

    try {
      if (path.toFile().setWritable(true, false)) {
        log.info("Deleting: {}", path.toAbsolutePath().normalize());
        Files.delete(path);
      } else {
        log.warn(
            "Could not set writable attribute, skip delete: {}", path.toAbsolutePath().normalize());
      }
    } catch (FileSystemException fse) {
      log.warn("File is locked or in use, skip delete: {}", path.toAbsolutePath().normalize());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void deleteRecursively(Path root) {
    try (Stream<Path> walk = Files.walk(root)) {
      walk.sorted(Comparator.comparingInt(Path::getNameCount).reversed())
          .forEach(FileUtils::delete);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
