package dev.aulait.amv.arch.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SyntaxUtils {

  public static String toSimpleType(String qualifiedTypeName) {
    if (qualifiedTypeName == null) {
      return null;
    }
    if (qualifiedTypeName.isEmpty()) {
      return "";
    }
    // Handle generics, e.g., java.util.List<java.lang.String> -> List<String>
    int genericStart = qualifiedTypeName.indexOf('<');
    if (genericStart != -1) {
      String rawType = qualifiedTypeName.substring(0, genericStart);
      String simpleRawType = toSimpleType(rawType);
      String genericPart =
          qualifiedTypeName.substring(genericStart + 1, qualifiedTypeName.lastIndexOf('>'));
      String[] genericTypes = genericPart.split(",");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < genericTypes.length; i++) {
        if (i > 0) sb.append(", ");
        sb.append(toSimpleType(genericTypes[i].trim()));
      }
      return simpleRawType + "<" + sb + ">";
    }
    if (qualifiedTypeName.contains(".")) {
      return qualifiedTypeName.substring(qualifiedTypeName.lastIndexOf(".") + 1);
    } else {
      return qualifiedTypeName;
    }
  }

  public static String extractStereotype(String typeName) {
    if (typeName == null) {
      return null;
    }
    if (typeName.isEmpty()) {
      return "";
    }

    for (int i = typeName.length() - 1; i >= 0; i--) {
      char c = typeName.charAt(i);
      if (Character.isUpperCase(c)) {
        if (i == 0) {
          return typeName;
        }

        char prev = typeName.charAt(i - 1);
        if (Character.isLowerCase(prev)) {
          return typeName.substring(i);
        }
      }
    }

    return typeName;
  }
}
