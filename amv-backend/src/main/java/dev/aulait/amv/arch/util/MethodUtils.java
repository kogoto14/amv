package dev.aulait.amv.arch.util;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodUtils {

  private static final int MAX_LINE_LENGTH = 50;
  private static final String INDENT = "    ";
  private static final String NEW_LINE_CHAR = "\\n";

  public static String formatSignatureWithLineBreaks(MethodEntity method) {
    String signature = buildSimpleSignature(method);

    if (signature.length() <= MAX_LINE_LENGTH) {
      return signature;
    }

    return formatMethodSignatureToMultiline(signature);
  }

  public static String buildSimpleSignature(MethodEntity method) {
    StringBuilder sb = new StringBuilder();

    sb.append(method.getName());
    sb.append("(");
    sb.append(buildSimpleParamSignature(method.getMethodParams()));
    sb.append(")");

    return sb.toString();
  }

  private static String buildSimpleParamSignature(Set<MethodParamEntity> params) {
    return params.stream()
        .sorted(Comparator.comparingInt(param -> param.getId().getSeqNo()))
        .map(MethodUtils::buildSimpleParamSignature)
        .collect(Collectors.joining(", "));
  }

  private static String buildSimpleParamSignature(MethodParamEntity param) {
    return SyntaxUtils.toSimpleType(param.getType()) + " " + param.getName();
  }

  private static String formatMethodSignatureToMultiline(String signature) {
    Matcher matcher = Pattern.compile("(?<=\\().*(?=\\))").matcher(signature);

    if (!matcher.find()) {
      return signature;
    }

    String paramsPart = matcher.group().trim();
    String formattedParams = formatParamToMultiline(paramsPart);

    return matcher.replaceFirst(Matcher.quoteReplacement(formattedParams));
  }

  private static String formatParamToMultiline(String paramsPart) {
    StringBuilder paramBuilder = new StringBuilder();
    int depth = 0;

    paramBuilder.append(NEW_LINE_CHAR);
    paramBuilder.append(INDENT);

    for (int i = 0; i < paramsPart.length(); i++) {
      char c = paramsPart.charAt(i);

      if (c == ' ' && depth == 0 && paramsPart.charAt(i - 1) == ',') {
        continue;
      }
      paramBuilder.append(c);

      switch (c) {
        case '<' -> depth++;
        case '>' -> depth--;
        case ',' -> {
          if (depth == 0) {
            paramBuilder.append(NEW_LINE_CHAR);
            paramBuilder.append(INDENT);
          }
        }
      }
    }

    paramBuilder.append(NEW_LINE_CHAR);
    return paramBuilder.toString();
  }
}
