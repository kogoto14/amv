package dev.aulait.amv.interfaces.project;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDataFactory {

  public static ProjectDto createProject() {
    return ProjectDto.builder()
        .id(RandomStringUtils.random(22, true, true))
        .name(RandomStringUtils.random(36, true, true))
        .path(RandomStringUtils.random(36, true, true))
        // .sourceDirs(RandomStringUtils.random(36, true, true))
        .build();
  }
}
