package dev.aulait.amv.interfaces.process;

import dev.aulait.amv.arch.util.ShortUuidUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeDataFactory {

  public static TypeDto createType() {
    return TypeDto.builder()
        .id(ShortUuidUtils.generate())
        .name(RandomStringUtils.insecure().next(36, true, true))
        .qualifiedName(RandomStringUtils.insecure().next(36, true, true))
        .kind(RandomStringUtils.insecure().next(1, true, true))
        .annotations(RandomStringUtils.insecure().next(36, true, true))
        .fields(null)
        .methods(null)
        // .sourceFile(null)
        .build();
  }
}
