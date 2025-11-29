export interface PropertyModel {
  name: string;
  type: string | null;
  isStatic: boolean;
  isReadonly: boolean;
  visibility: "public" | "protected" | "private" | "default";
}

export interface MethodParamModel {
  name: string;
  type: string | null;
  isOptional: boolean;
}

export interface MethodModel {
  name: string;
  returnType: string | null;
  isStatic: boolean;
  visibility: "public" | "protected" | "private" | "default";
  params: MethodParamModel[];
  methodCalls: MethodCallModel[];
}

export interface MethodCallModel {
  expression: string;
}

export interface ConstructorParamModel {
  name: string;
  type: string | null;
  isOptional: boolean;
  visibility: "public" | "protected" | "private" | "default";
}

export interface ConstructorModel {
  params: ConstructorParamModel[];
}

export interface ClassModel {
  package: string;
  name: string;
  isAbstract: boolean;
  heritage: {
    extends?: string | null;
    implements: string[];
  };
  constructors: ConstructorModel[];
  properties: PropertyModel[];
  methods: MethodModel[];
}

export interface SourceFileModel {
  file: string;
  classes: ClassModel[];
}
