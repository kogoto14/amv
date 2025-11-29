import type { TypeSearchCriteriaModel, TypeSearchResultModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    ...CriteriaUtils.decode(url)
  } as TypeSearchCriteriaModel;

  const result =
    (await ApiHandler.handle<TypeSearchResultModel>(fetch, (api) => api.type.search(criteria))) ||
    ({} as TypeSearchResultModel);

  const classDiagram =
    result.list && result.list!.length === 1
      ? (await ApiHandler.handle<string>(fetch, (api) =>
          api.diagrams.classDiagram({
            qualifiedName: result.list![0].qualifiedName!
          })
        )) || ''
      : '';

  return {
    criteria,
    result,
    classDiagram
  };
};
