import { test } from '@playwright/test';
import { TypeFacade } from '@facades/TypeFacade';
import { DryRun } from '@arch/DryRun';
import { locale } from '@arch/MultiLng';
import NumberUtils from '@arch/NumberUtils';
import TopPage from '@pages/top/TopPage';
import TypeInputFactory from '@factories/TypeFactory';

test('CRUD of Type', async ({ browser }) => {
  const context = await browser.newContext({
    locale: locale
  });

  const page = await context.newPage();
  const dryRun = DryRun.build();

  // TODO: Enable after fixing issues.
  // const topPage = new TopPage(page, dryRun);
  // const menuBar = await topPage.open();

  // let typeListPage = await menuBar.gotoTypeListPage();
  // let typeInputPage = await typeListPage.gotoNewTypePage();

  // // Create
  // const type = TypeInputFactory.createRandomType();
  // await typeInputPage.create(type);
  // await typeInputPage.expectSavedSuccessfully();

  // // Rererence
  // const typeFacade = new TypeFacade(dryRun);
  // typeInputPage = await typeFacade.referenceTypeById(menuBar, type.id);

  // // Update
  // const updatingType = TypeInputFactory.createRandomTypeWithId(type.id);
  // await typeInputPage.update(updatingType);
  // await typeInputPage.expectSavedSuccessfully();
  // await typeInputPage.expectType(updatingType);

  // // Delete
  // await typeInputPage.delete();
  // await typeInputPage.expectDeletedSuccessfully();
});
