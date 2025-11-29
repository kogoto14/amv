package dev.aulait.amv.interfaces.process;

import static dev.aulait.sqb.ComparisonOperator.*;
import static dev.aulait.sqb.LogicalOperator.*;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.arch.util.BeanUtils.MappingConfig;
import dev.aulait.amv.domain.process.TypeEntity;
import dev.aulait.amv.interfaces.process.TypeController.TypeSearchResultDto;
import dev.aulait.sqb.LikePattern;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchCriteriaBuilder;
import dev.aulait.sqb.SearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TypeFactory {

  private final MethodFactory methodFactory;

  private MappingConfig<TypeEntity, TypeDto> searchResultConfig =
      BeanUtils.buildConfig(TypeEntity.class, TypeDto.class).build();

  public TypeDto build(TypeEntity entity) {
    // TODO: optimize to call one time BeanUtils.map
    TypeDto dto = BeanUtils.map(entity, TypeDto.class);
    dto.setMethods(entity.getMethods().stream().map(methodFactory::build).toList());

    return dto;
  }

  public SearchCriteria build(TypeSearchCriteriaDto criteria) {
    Object text = LikePattern.contains(criteria.getText());

    return new SearchCriteriaBuilder()
        .select("SELECT DISTINCT t FROM TypeEntity t")
        .select("LEFT JOIN FETCH t.fields f")
        .select("LEFT JOIN FETCH t.methods m")
        .where("t.qualifiedName", LIKE, text)
        .defaultOrderBy("t.unsolvedCnt", false)
        .orderBy(criteria.getSortOrders())
        .build(criteria.getPageControl());
  }

  public TypeSearchResultDto build(SearchResult<TypeEntity> vo) {
    return BeanUtils.map(searchResultConfig, vo, TypeSearchResultDto.class);
  }
}
