package dev.aulait.amv.arch.test;

import lombok.Data;

@Data
public class ConstraintViolationDto {
  private String field;
  private String message;
}
