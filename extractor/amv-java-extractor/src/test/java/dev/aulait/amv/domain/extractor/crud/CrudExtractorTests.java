package dev.aulait.amv.domain.extractor.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.domain.extractor.fdo.CrudPointFdo;
import java.util.List;
import org.junit.jupiter.api.Test;

class CrudExtractorTests {

  CrudExtractor extractor = new CrudExtractor();

  @Test
  void insertTest() {
    String sql =
        """
        INSERT INTO a_table (id, name) VALUES (1, 'test')
        """;

    List<CrudPointFdo> crudList = extractor.extract(sql);

    assertEquals("C", crudList.get(0).getCrud());
    assertEquals("a_table", crudList.get(0).getDataName());
  }

  @Test
  void selectTest() {
    String sql =
        """
        SELECT * FROM a_table AS a
        JOIN b_table AS b ON a.id = b.a_id
        """;
    List<CrudPointFdo> crudList = extractor.extract(sql);

    assertEquals("R", crudList.get(0).getCrud());
    assertEquals("a_table", crudList.get(0).getDataName());

    assertEquals("R", crudList.get(1).getCrud());
    assertEquals("b_table", crudList.get(1).getDataName());
  }

  @Test
  void updateTest() {
    String sql = "UPDATE a_table SET name = 'updated' WHERE id = 1";
    List<CrudPointFdo> crudList = extractor.extract(sql);

    assertEquals("U", crudList.get(0).getCrud());
    assertEquals("a_table", crudList.get(0).getDataName());
  }

  @Test
  void deleteTest() {
    String sql = "DELETE FROM a_table WHERE id = 1";
    List<CrudPointFdo> crudList = extractor.extract(sql);

    assertEquals("D", crudList.get(0).getCrud());
    assertEquals("a_table", crudList.get(0).getDataName());
  }
}
