import { ClassModel, SourceFileModel } from "./Models";

export class DataObjectFilter {
  filter(sourceFile: SourceFileModel) {
    return sourceFile.classes
      .map((cls) => this.filterClass(cls))
      .filter(Boolean);
  }

  filterClass(cls: ClassModel) {
    // Check if the class has constructors and no properties or methods
    return !this.emptyOrUndefined(cls.constructors) &&
      this.emptyOrUndefined(cls.properties) &&
      this.emptyOrUndefined(cls.methods)
      ? cls
      : null;
  }

  private emptyOrUndefined(arr: unknown[]) {
    return !Array.isArray(arr) || arr.length === 0;
  }
}
