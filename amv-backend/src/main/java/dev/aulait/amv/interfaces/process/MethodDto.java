package dev.aulait.amv.interfaces.process;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class MethodDto {
  private String namespace;

  @Schema(required = true)
  private MethodDtoId id;

  @Schema(required = true)
  private String type;

  @Schema(required = true)
  private String name;

  @Schema(required = true)
  private String returnType;

  private String qualifiedSignature;
  private String simpleSignature;
  private String srcUrl;

  @Schema(required = true)
  private boolean dummy;

  @Schema(required = true)
  private SortedSet<MethodCallDto> calls = new TreeSet<>();

  private EntryPointDto entryPoint;

  @Schema(required = true)
  private List<CrudPointDto> crudPoints = new ArrayList<>();
}
