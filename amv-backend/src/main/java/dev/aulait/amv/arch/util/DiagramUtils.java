package dev.aulait.amv.arch.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;

@Slf4j
public class DiagramUtils {

  public static String draw(String text) {
    SourceStringReader reader = new SourceStringReader(text);

    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      DiagramDescription desc = reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
      if (desc != null) {
        log.debug("{}", desc.getDescription());
      }
      return new String(os.toByteArray(), Charset.forName("UTF-8"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
