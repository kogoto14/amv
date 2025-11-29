import {
  CallExpression,
  ClassDeclaration,
  ConstructorDeclaration,
  MethodDeclaration,
  Node,
  ParameterDeclaration,
  PropertyDeclaration,
  SourceFile,
  SyntaxKind,
} from "ts-morph";
import {
  ClassModel,
  PropertyModel,
  MethodModel,
  MethodParamModel,
  SourceFileModel,
  ConstructorModel,
  ConstructorParamModel,
  MethodCallModel,
} from "./Models";

export class MetadataExtractor {
  constructor(
    private rootDir: string,
    private projectDir: string
  ) {}

  private resolveType(param: ParameterDeclaration | PropertyDeclaration) {
    const pType =
      param.getType()?.getText() || param.getTypeNode()?.getText() || null;

    if (pType) {
      const match = pType.match(/(["'])(.*?)\1/);
      if (match) {
        const importPath = match[1];
        return importPath.replace(this.rootDir, "");
      }
    }

    return "";
  }

  extractConstructorParam(param: ParameterDeclaration) {
    return {
      name: param.getName(),
      type: this.resolveType(param),
      isOptional: param.isOptional(),
      visibility: param.getScope() ?? "default",
    } as ConstructorParamModel;
  }

  extractConstructor(constructor: ConstructorDeclaration) {
    const params = constructor.getParameters().map((param) => {
      return this.extractConstructorParam(param);
    });

    return {
      params,
    } as ConstructorModel;
  }

  extractMethodParam(param: ParameterDeclaration) {
    return {
      name: param.getName(),
      type: this.resolveType(param),
      isOptional: param.isOptional(),
    } as MethodParamModel;
  }

  extractMethodCall(call: CallExpression) {
    return {
      expression: call.getExpression().getText(),
    } as MethodCallModel;
  }

  extractMethod(method: MethodDeclaration) {
    const visibility = method.getScope() ?? "default";
    const returnType =
      method.getReturnType()?.getText() ||
      method.getReturnTypeNode()?.getText() ||
      null;

    const params: MethodParamModel[] = method.getParameters().map((param) => {
      return this.extractMethodParam(param);
    });

    const methodCalls = [];
    method.forEachDescendant((node) => {
      if (Node.isCallExpression(node)) {
        methodCalls.push(this.extractMethodCall(node));
      }
    });

    return {
      name: method.getName(),
      returnType,
      isStatic: method.isStatic(),
      visibility,
      params,
      methodCalls,
    } as MethodModel;
  }

  extractField(prop: PropertyDeclaration) {
    const visibility = prop.getScope() ?? "default";
    return {
      name: prop.getName(),
      type: this.resolveType(prop),
      isStatic: prop.isStatic(),
      isReadonly: prop.isReadonly(),
      visibility,
    } as PropertyModel;
  }

  extractPackage(path: string) {
    const index = path.lastIndexOf(this.projectDir);

    return path.substring(index, path.length);
  }

  extractClass(cls: ClassDeclaration) {
    const classModel = {} as ClassModel;
    // const typeChecker = project.getTypeChecker();

    classModel.package = this.extractPackage(cls.getSourceFile().getFilePath());

    classModel.name = cls.getName() ?? "(anonymous)";
    const heritageClauses = cls.getHeritageClauses();

    const extendsClause = heritageClauses.find(
      (h) => h.getToken() === SyntaxKind.ExtendsKeyword
    );
    const implementsClause = heritageClauses.find(
      (h) => h.getToken() === SyntaxKind.ImplementsKeyword
    );

    const extendsType = extendsClause
      ? (extendsClause.getTypeNodes()[0]?.getText() ?? null)
      : null;

    const implementsTypes = implementsClause
      ? implementsClause.getTypeNodes().map((t) => t.getText())
      : [];

    classModel.heritage = {
      extends: extendsType,
      implements: implementsTypes,
    };

    classModel.constructors = cls.getConstructors().map((constructor) => {
      return this.extractConstructor(constructor);
    });

    classModel.properties = cls.getProperties().map((p) => {
      return this.extractField(p);
    });

    // Include get/set accessors as fields if you want:
    for (const acc of cls.getGetAccessors()) {
      const type = acc.getReturnType()?.getText() || null;
      classModel.properties.push({
        name: acc.getName(),
        type,
        isStatic: acc.isStatic?.() ?? false,
        isReadonly: true,
        visibility: acc.getScope?.() ?? "default",
      });
    }

    classModel.methods = cls.getMethods().map(this.extractMethod.bind(this));

    return classModel;
  }

  extractSrcFile(sourceFile: SourceFile) {
    const classes: ClassModel[] = [];
    for (const cls of sourceFile.getClasses()) {
      classes.push(this.extractClass(cls));
    }

    return {
      file: sourceFile.getFilePath(),
      classes: classes,
    } as SourceFileModel;
  }
}
