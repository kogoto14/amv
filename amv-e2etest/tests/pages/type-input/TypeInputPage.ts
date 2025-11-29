
import TypeInputPageElement from '@pages/type-input/TypeInputPageElement';
import type { TypeModel } from '@api/Api';
import BasePageElement from '@arch/BasePageElement';

export default class TypeInputPage {
  private typeInputPageEl: TypeInputPageElement;

  constructor(page: BasePageElement) {
    this.typeInputPageEl = new TypeInputPageElement(page);
  }

  async create(type: TypeModel) {
    await this.typeInputPageEl.inputId(type.id);
    await this.typeInputPageEl.inputName(type.name);
    await this.typeInputPageEl.inputQualifiedName(type.qualifiedName);
    await this.typeInputPageEl.inputKind(type.kind);
    await this.typeInputPageEl.inputAnnotations(type.annotations);
    await this.typeInputPageEl.inputFields(type.fields);
    await this.typeInputPageEl.inputMethods(type.methods);
    await this.typeInputPageEl.inputSourceFile(type.sourceFile);
    
    await this.typeInputPageEl.clickSaveBtn();
  }

  async update(type: TypeModel) { 
    await this.typeInputPageEl.inputName(type.name);
    await this.typeInputPageEl.inputQualifiedName(type.qualifiedName);
    await this.typeInputPageEl.inputKind(type.kind);
    await this.typeInputPageEl.inputAnnotations(type.annotations);
    await this.typeInputPageEl.inputFields(type.fields);
    await this.typeInputPageEl.inputMethods(type.methods);
    await this.typeInputPageEl.inputSourceFile(type.sourceFile);
    
    await this.typeInputPageEl.clickSaveBtn();
  }

  async expectSavedSuccessfully() {
    await this.typeInputPageEl.expectSavedSuccessfully();
  }

  async expectType(type: TypeModel) {
    await this.typeInputPageEl.expectName(type.name);
    await this.typeInputPageEl.expectQualifiedName(type.qualifiedName);
    await this.typeInputPageEl.expectKind(type.kind);
    await this.typeInputPageEl.expectAnnotations(type.annotations);
  }

  async delete() {    
    await this.typeInputPageEl.clickDeleteBtn();
  }

  async expectDeletedSuccessfully() {
    await this.typeInputPageEl.expectDeletedSuccessfully();
  }
}