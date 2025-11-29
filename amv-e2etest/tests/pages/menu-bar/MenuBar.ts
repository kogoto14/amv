/**
 * The section marked with "__****__" is dynamically replaced.
 * Do not remove or modify it.
 */
import BasePageElement from '@arch/BasePageElement';
import MenuBarPageElement from '@pages/menu-bar/MenuBarPageElement';
import TypeListPage from '@pages/type-list/TypeListPage';
/* __IMPORT__ */

export default class MenuBar {
  private readonly menuBarEl: MenuBarPageElement;

  constructor(page: BasePageElement) {
    this.menuBarEl = new MenuBarPageElement(page);
  }

  async gotoTypeListPage() {
    await this.menuBarEl.clickTypeLink();
    return new TypeListPage(this.menuBarEl);
  }
  /* __GOTO__ */
}
