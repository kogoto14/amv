package dev.aulait.amv.interfaces.project;

import static dev.aulait.sqb.ComparisonOperator.*;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.arch.util.BeanUtils.MappingConfig;
import dev.aulait.amv.domain.project.CodebaseAggregate;
import dev.aulait.amv.domain.project.CodebaseEntity;
import dev.aulait.amv.interfaces.project.CodebaseController.CodebaseSearchResultDto;
import dev.aulait.sqb.LikePattern;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchCriteriaBuilder;
import dev.aulait.sqb.SearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CodebaseFactory {

  private MappingConfig<CodebaseEntity, CodebaseDto> searchResultConfig =
      BeanUtils.buildConfig(CodebaseEntity.class, CodebaseDto.class).build();

  public SearchCriteria build(CodebaseSearchCriteriaDto criteria) {
    Object text = LikePattern.contains(criteria.getText());
    return new SearchCriteriaBuilder()
        .select("SELECT c FROM CodebaseEntity c")
        .where("c.name", LIKE, text)
        .orderBy(criteria.getSortOrders())
        .build(criteria.getPageControl());
  }

  public CodebaseSearchResultDto build(SearchResult<CodebaseEntity> vo) {
    return BeanUtils.map(searchResultConfig, vo, CodebaseSearchResultDto.class);
  }

  public CodebaseDto build(CodebaseAggregate codebase) {
    CodebaseDto dto = BeanUtils.map(codebase.getCodebase(), CodebaseDto.class);
    dto.setStatus(BeanUtils.map(codebase.getStatus(), CodebaseStatusDto.class));

    codebase.getProjects().stream()
        .map(p -> BeanUtils.map(p, ProjectDto.class))
        .forEach(dto.getProjects()::add);

    return dto;
  }

  public List<CodebaseDto> build(List<CodebaseAggregate> codebases) {
    return codebases.stream().map(this::build).toList();
  }
}
