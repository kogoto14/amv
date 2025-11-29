package dev.aulait.amv.arch.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

  private static ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(new Jdk8Module());
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static String obj2json(Object obj) {
    try {
      return MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String obj2fmtjson(Object object) {
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T path2obj(Path filePath, Class<T> clazz) {
    try {
      return MAPPER.readValue(filePath.toFile(), clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T str2obj(String str, Class<T> clazz) {
    try {
      return MAPPER.readValue(str, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
