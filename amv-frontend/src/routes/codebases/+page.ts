import type { CodebaseSearchCriteriaModel, CodebaseSearchResultModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    ...CriteriaUtils.decode(url)
  } as CodebaseSearchCriteriaModel;

  const result =
    (await ApiHandler.handle<CodebaseSearchResultModel>(fetch, (api) =>
      api.codebases.search(criteria)
    )) || ({} as CodebaseSearchResultModel);

  return {
    criteria,
    result
  };
};
