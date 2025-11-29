package dev.aulait.amv.interfaces.process;

import static dev.aulait.sqb.ComparisonOperator.LIKE;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.arch.util.BeanUtils.MappingConfig;
import dev.aulait.amv.arch.util.MethodUtils;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.interfaces.process.MethodController.MethodSearchResultDto;
import dev.aulait.sqb.LikePattern;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchCriteriaBuilder;
import dev.aulait.sqb.SearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class MethodFactory {

  private MappingConfig<MethodEntity, MethodDto> searchResultConfig =
      BeanUtils.buildConfig(MethodEntity.class, MethodDto.class).skip(MethodDto::setCalls).build();

  public MethodDto build(MethodEntity entity) {
    MethodDto dto = BeanUtils.map(entity, MethodDto.class);

    if (entity.getId() == null) {
      dto.setDummy(true);
      return dto;
    }

    String qualifiedTypeName =
        StringUtils.substringBefore(entity.getQualifiedSignature(), "." + entity.getName() + "(");

    String namespace = StringUtils.substringBeforeLast(qualifiedTypeName, ".");
    dto.setNamespace(namespace);

    String type = StringUtils.substringAfterLast(qualifiedTypeName, ".");
    dto.setType(type);

    dto.setSimpleSignature(MethodUtils.buildSimpleSignature(entity));

    return dto;
  }

  public SearchCriteria build(MethodSearchCriteriaDto criteria) {
    Object text = LikePattern.contains(criteria.getText());
    return new SearchCriteriaBuilder()
        .select("SELECT m FROM MethodEntity m")
        .where("m.qualifiedSignature", LIKE, text)
        .orderBy(criteria.getSortOrders())
        .build(criteria.getPageControl());
  }

  public MethodSearchResultDto build(SearchResult<MethodEntity> vo) {
    return BeanUtils.map(searchResultConfig, vo, MethodSearchResultDto.class);
  }
}
