package dev.aulait.amv.interfaces.process;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessLoadRequestDto {
  private String codebaseId;
  @Builder.Default private List<String> projectIds = new ArrayList<>();
}
