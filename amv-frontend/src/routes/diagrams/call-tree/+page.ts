import type { CallTreeCriteriaModel, CallTreeModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    callTreeRequired: true,
    calledTreeRequired: true,
    render: 'HTML',
    limit: 5,
    ...CriteriaUtils.decode(url)
  } as CallTreeCriteriaModel;

  const open = CriteriaUtils.decodeParam<boolean>(url, 'open') ?? false;

  const callTrees =
    (await ApiHandler.handle<CallTreeModel[]>(fetch, (api) => api.diagrams.callTree(criteria))) ||
    [];

  return {
    title: 'Call Tree',
    criteria,
    open,
    callTrees,
    showExternalPackage: false,
    packageLevel: 3
  };
};
