package dev.aulait.amv.domain.extractor.crud;

import dev.aulait.amv.domain.extractor.fdo.CrudPointFdo;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

@Slf4j
public class CrudExtractor {

  public List<CrudPointFdo> extract(String sql) {
    String escapedSql = sql.replaceAll(" in( |\\n|$)", " in ()").replace("?", "''");

    try {
      return CCJSqlParserUtil.parseStatements(escapedSql).stream()
          .map(this::extractFromStatement)
          .flatMap(Collection::stream)
          .toList();
    } catch (JSQLParserException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private List<CrudPointFdo> extractFromStatement(Statement stmt) {
    if (stmt instanceof Insert ins) {
      return List.of(CrudPointFdo.builder().crud("C").dataName(ins.getTable().getName()).build());
    }

    if (stmt instanceof Update up) {
      return List.of(CrudPointFdo.builder().crud("U").dataName(up.getTable().getName()).build());
    }

    if (stmt instanceof Delete del) {
      return List.of(CrudPointFdo.builder().crud("D").dataName(del.getTable().getName()).build());
    }

    if (stmt instanceof Select) {
      TablesNamesFinder<Void> finder = new TablesNamesFinder<>();
      return finder.getTables(stmt).stream()
          .map(table -> CrudPointFdo.builder().crud("R").dataName(table).build())
          .toList();
    }

    log.warn("Unsupported statement : {}", stmt);

    return List.of();
  }
}
