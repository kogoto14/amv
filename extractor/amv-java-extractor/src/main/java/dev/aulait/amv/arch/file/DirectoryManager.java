package dev.aulait.amv.arch.file;

import java.nio.file.Path;

/**
 * Manages application directories. The directory structure is as follows:
 *
 * <pre>
 * - AMV_HOME
 *   - codebase
 *     - {codebaseName}
 *   - extraction
 *     - {codebaseName}_{projectName}_{timestamp}
 *   - h2db  (actually this is not managed here, but created by H2 database engine)
 *     - amv.h2.db
 *     - amv.trace.db
 * </pre>
 */
public class DirectoryManager {

  private static final boolean APP_IN_CONTAINER = System.getenv().containsKey("APP_IN_CONTAINER");

  public static final Path MOUNT_DIR_HOST =
      Path.of("./target/amv-mnt").toAbsolutePath().normalize();

  public static final Path MOUNT_DIR_CONTAINER = Path.of("/mnt/amv").toAbsolutePath().normalize();

  public static final Path MOUNT_DIR = APP_IN_CONTAINER ? MOUNT_DIR_CONTAINER : MOUNT_DIR_HOST;

  public static final Path AMV_HOME_HOST =
      Path.of("./target/amv-home").toAbsolutePath().normalize();

  public static final Path AMV_HOME_CONTAINER = Path.of("./amv").toAbsolutePath().normalize();

  public static final Path AMV_HOME = APP_IN_CONTAINER ? AMV_HOME_CONTAINER : AMV_HOME_HOST;

  public static final Path CODEBASE_ROOT = AMV_HOME.resolve("codebase");

  public static final Path EXTRACTION_ROOT = AMV_HOME.resolve("extraction");

  public static final Path SECURITY_ROOT = AMV_HOME.resolve("security");

  static {
    FileUtils.mkdir(MOUNT_DIR);
    FileUtils.mkdir(AMV_HOME);
    FileUtils.mkdir(CODEBASE_ROOT);
    FileUtils.mkdir(EXTRACTION_ROOT);
    FileUtils.mkdir(SECURITY_ROOT);
  }

  public static Path createExtractionDir(String codebaseName, String hash) {
    String dirName = codebaseName + "_" + hash;
    return FileUtils.createDir(EXTRACTION_ROOT, dirName);
  }

  public static Path getExtractionDir(String codebaseName, String hash) {
    // TODO: refactor
    String dirName = codebaseName + "_" + hash;
    return EXTRACTION_ROOT.resolve(dirName);
  }
}
