package dev.aulait.amv.interfaces.project;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodebaseDataFactory {

  public static CodebaseDto createCodebase() {
    return CodebaseDto.builder()
        .id(RandomStringUtils.random(22, true, true))
        .name(RandomStringUtils.random(36, true, true))
        .url(RandomStringUtils.random(36, true, true))
        .build();
  }
}
