
import { LocalDate, LocalDateTime } from '@api/Api';
import BasePageElement from '@arch/BasePageElement';
import * as m from '@paraglide/messages';

export default class TypeInputPageElement extends BasePageElement {
  get pageNameKey() {
    return 'newType';
  }

  async inputId(id: string) {
    await this.inputText('#id', id);
  }    
  
  async inputName(name: string) {
    await this.inputText('#name', name);
  }
  async inputQualifiedName(qualifiedName?: string) {
    await this.inputText('#qualifiedName', qualifiedName?? '');
  }
  async inputKind(kind?: string) {
    await this.inputText('#kind', kind?? '');
  }
  async inputAnnotations(annotations?: string) {
    await this.inputText('#annotations', annotations?? '');
  }
  async inputFields(fields?: any[]) {
    await this.expectSelectedOption('#fields', fields?? '');
  }
  async inputMethods(methods?: any[]) {
    await this.expectSelectedOption('#methods', methods?? '');
  }
  async inputSourceFile(sourceFile?: any) {
    await this.expectSelectedOption('#sourceFile', sourceFile?? '');
  }

  async expectId(id: string) {
    await this.expectText('#id', id);
  }

  async expectName(name: string) {
    await this.expectText('#name', name);
  }
  async expectQualifiedName(qualifiedName?: string) {
    await this.expectText('#qualifiedName', qualifiedName?? '');
  }
  async expectKind(kind?: string) {
    await this.expectText('#kind', kind?? '');
  }
  async expectAnnotations(annotations?: string) {
    await this.expectText('#annotations', annotations?? '');
  }
  async expectFields(fields?: any[]) {
    await this.expectSelectedOption('#fields', fields?? '');
  }
  async expectMethods(methods?: any[]) {
    await this.expectSelectedOption('#methods', methods?? '');
  }
  async expectSourceFile(sourceFile?: any) {
    await this.expectSelectedOption('#sourceFile', sourceFile?? '');
  }

  async clickSaveBtn() {
    await this.click('#save');
  }

  async expectSavedSuccessfully() {
    await this.expectGlobalMessage(m.saved());
  }

  async clickDeleteBtn() {
    await this.click('#del');
  }

  async expectDeletedSuccessfully() {
    await this.expectGlobalMessage(m.deleted());
  }
}
