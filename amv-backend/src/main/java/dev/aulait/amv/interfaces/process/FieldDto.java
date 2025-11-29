package dev.aulait.amv.interfaces.process;

import lombok.Data;

@Data
public class FieldDto {
  private FieldDtoId id;
  private String name;
  private String type;
}
