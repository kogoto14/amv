package dev.aulait.amv.arch.file;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class FilePathVo {
  private Path baseDir;
  private Path filePath;

  public Path getRelative() {
    return baseDir.relativize(filePath);
  }

  public Path getRelativeFileDir() {
    Path relativeFileDir = getRelative().getParent();

    if (relativeFileDir != null) {
      return relativeFileDir;
    } else {
      // If the file is directly under the baseDir, return the baseDir's name as the relative
      // directory
      return baseDir.getFileName();
    }
  }
}
