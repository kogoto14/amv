package dev.aulait.amv.interfaces.process;

import lombok.Data;

@Data
public class EntryPointDto {
  private String path;
  private String httpMethod;
}
