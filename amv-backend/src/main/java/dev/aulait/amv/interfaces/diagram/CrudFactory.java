package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.domain.diagram.CrudElementVo;
import dev.aulait.amv.interfaces.process.MethodDto;
import dev.aulait.amv.interfaces.process.MethodFactory;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CrudFactory {

  private final MethodFactory methodFactory;

  public CrudDto build(List<CrudElementVo> elements, Map<String, String> typeId2url) {
    CrudDto crudDto = new CrudDto();

    elements.forEach(
        e -> {
          MethodDto methodDto = methodFactory.build(e.getMethod());
          methodDto.setSrcUrl(
              // typeId2url.get(e.getMethod().getTypeId()) + "#L" + e.getMethod().getDecLineNo());
              typeId2url.get(e.getMethod().getId().getTypeId()) + "#L" + e.getMethod().getLineNo());
          crudDto.add(e, methodDto);
        });

    return crudDto;
  }
}
