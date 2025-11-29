import BaseFacade from '@arch/BaseFacade';
import MenuBar from '@pages/menu-bar/MenuBar';


export class TypeFacade extends BaseFacade {
  async referenceTypeById(menuBar: MenuBar, id: string) {
    this.logStart("Type Reference");

    const typeListPage = await menuBar.gotoTypeListPage();
    const typeInputPage = await typeListPage.gotoTypeById(id);

    return typeInputPage;
  }
}