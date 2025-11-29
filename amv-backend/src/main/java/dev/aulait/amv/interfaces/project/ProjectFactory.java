package dev.aulait.amv.interfaces.project;

import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchCriteriaBuilder;
import dev.aulait.sqb.SearchResult;
import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.arch.util.BeanUtils.MappingConfig;
import dev.aulait.amv.domain.project.ProjectEntity;
import dev.aulait.amv.interfaces.project.ProjectController.ProjectSearchResultDto;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectFactory {

  private MappingConfig<ProjectEntity, ProjectDto> searchResultConfig =
      BeanUtils.buildConfig(ProjectEntity.class, ProjectDto.class)
          .build(); 

  public SearchCriteria build(ProjectSearchCriteriaDto criteria) {
    return new SearchCriteriaBuilder()
        .select("SELECT p FROM ProjectEntity p")
        .orderBy(criteria.getSortOrders())
        .build(criteria.getPageControl());
  }

  public ProjectSearchResultDto build(SearchResult<ProjectEntity> vo) { 
    return BeanUtils.map(searchResultConfig, vo, ProjectSearchResultDto.class); 
  }
}
