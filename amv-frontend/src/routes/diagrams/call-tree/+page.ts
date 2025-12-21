import ApiHandler from '$lib/arch/api/ApiHandler';
import type {
  MethodSearchCriteriaModel,
  MethodSearchResultModel,
  CallTreeModel,
  CallTreeCriteriaModel
} from '$lib/arch/api/Api';
import type { PageLoad } from './$types';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

export type CriteriaModel = {
  methodCriteria: MethodSearchCriteriaModel;
  callTreeCriteria: CallTreeCriteriaModel;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    methodCriteria: {},
    callTreeCriteria: {
      callTreeRequired: true,
      calledTreeRequired: true
    },
    init: true,
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const methods = await searchMethods(criteria.methodCriteria, fetch);

  criteria.callTreeCriteria.signaturePattern =
    methods.list?.length === 1 ? methods.list[0].qualifiedSignature! : '';

  const callTrees = await getCallTree(criteria.callTreeCriteria);

  return {
    criteria,
    methods,
    callTrees
  };
};

async function searchMethods(criteria: MethodSearchCriteriaModel, fetch: typeof window.fetch) {
  if (!criteria.text || criteria.text.length < 2) {
    return {} as MethodSearchResultModel;
  }

  return (
    (await ApiHandler.handle<MethodSearchResultModel>(fetch, (api) =>
      api.methods.search(criteria)
    )) || ({} as MethodSearchResultModel)
  );
}

async function getCallTree(callTreeCriteria: CallTreeCriteriaModel) {
  if (!callTreeCriteria.signaturePattern) {
    return {} as CallTreeModel[];
  }

  return (
    (await ApiHandler.handle<CallTreeModel[]>(fetch, (api) =>
      api.diagrams.callTree(callTreeCriteria)
    )) || []
  );
}
