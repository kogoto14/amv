import type { CrudModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
  const crud =
    (await ApiHandler.handle<CrudModel>(fetch, (api) => api.diagrams.getCrudDiagram())) ||
    ({} as CrudModel);

  return {
    crud
  };
};
