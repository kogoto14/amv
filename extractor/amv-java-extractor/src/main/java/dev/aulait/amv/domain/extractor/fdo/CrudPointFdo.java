package dev.aulait.amv.domain.extractor.fdo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CrudPointFdo {
  private String kind;
  private String dataName;
  private String type;
  private String crud;
}
