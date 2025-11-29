package dev.aulait.amv.domain.extractor.mybatis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MyBatisExtractorTests {

  MyBatisExtractor extractor = new MyBatisExtractor();

  final String SINGLE_MAPPED_TYPE =
      """
        package a.b.c;

        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import org.apache.ibatis.type.MappedTypes;
        import org.joda.time.DateTime;

        @MappedTypes(DateTime.class)
        public class DateTimeHandler extends BaseTypeHandler<DateTime> {
        }
      """;

  final String MULTIPLE_MAPPED_TYPES =
      """
        package a.b.c;

        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import org.apache.ibatis.type.MappedTypes;
        import org.joda.time.Date;
        import org.joda.time.DateTime;

        @MappedTypes({Date.class, DateTime.class})
        public class DateTimeHandler extends BaseTypeHandler<DateTime> {
        }
      """;

  @Test
  void singleExtractHandledTypesTest() {
    List<String> actual = extractor.extractHandledTypes(SINGLE_MAPPED_TYPE);

    assertEquals(List.of("org.joda.time.DateTime"), actual);
  }

  @Test
  void multipleExtractHandledTypesTest() {
    List<String> actual = extractor.extractHandledTypes(MULTIPLE_MAPPED_TYPES);

    assertEquals(List.of("org.joda.time.Date", "org.joda.time.DateTime"), actual);
  }

  @Test
  void extractHandledTypesTest() {
    List<String> actual = extractor.extractHandledTypes(Path.of("src/main/java"));

    assertTrue(actual.isEmpty());
  }
}
