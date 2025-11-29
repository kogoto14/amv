import TypeListPageElement from '@pages/type-list/TypeListPageElement';
import TypeInputPage from '@pages/type-input/TypeInputPage';
import BasePageElement from '@arch/BasePageElement';


export default class TypeListPage {
  private typeListPageEl: TypeListPageElement;

  constructor(page: BasePageElement) {
    this.typeListPageEl = new TypeListPageElement(page);
  }

  async gotoNewTypePage() {
    await this.typeListPageEl.clickNewTypeLink();
    return new TypeInputPage(this.typeListPageEl);
  }

  async gotoTypeById(id: string) {
    await this.typeListPageEl.clickTypeNoLinkById(id);
    return new TypeInputPage(this.typeListPageEl);
  }
}