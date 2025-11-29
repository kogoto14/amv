import BasePageElement from '@arch/BasePageElement';


export default class TypeListPageElement extends BasePageElement {
  get pageNameKey() {
    return 'type';
  }

  async clickNewTypeLink() {
    await this.click('#newType');
  }

  async clickTypeNoLinkById(id: string) {
    await this.clickInRow(id);
  }
}