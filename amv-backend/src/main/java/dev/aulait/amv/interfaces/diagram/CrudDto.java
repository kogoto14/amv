package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.domain.diagram.CrudElementVo;
import dev.aulait.amv.interfaces.process.MethodDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
public class CrudDto {
  private static final String SORT_TEMPLATE = "CRUD";

  @Schema(required = true)
  private Set<String> entryPoints = new TreeSet<>();

  @Schema(required = true)
  private Set<String> tables = new TreeSet<>();

  /*
   * key: entryPoint, value: key: table, value: crud
   */
  @Schema(required = true)
  private Map<String, Map<String, String>> crud = new HashMap<>();

  @Schema(required = true)
  private Map<String, MethodDto> methods = new HashMap<>();

  public void add(CrudElementVo element, MethodDto method) {
    entryPoints.add(element.getEntryPoint());
    tables.add(element.getTable());

    Map<String, String> table2crud =
        crud.computeIfAbsent(element.getEntryPoint(), k -> new HashMap<>());

    String crudOpes = table2crud.get(element.getTable());
    crudOpes = Objects.toString(crudOpes, "") + element.getCrud();
    crudOpes = sort(crudOpes);
    table2crud.put(element.getTable(), crudOpes);

    methods.put(element.getEntryPoint(), method);
  }

  String sort(String str) {
    if (StringUtils.length(str) < 2) {
      return str;
    }

    return SORT_TEMPLATE
        .chars()
        .mapToObj(c -> String.valueOf((char) c))
        .filter(ch -> str.indexOf(ch) >= 0)
        .collect(Collectors.joining());
  }
}
