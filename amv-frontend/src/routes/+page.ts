import type { CodebaseModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
  const codebases =
    (await ApiHandler.handle<CodebaseModel[]>(fetch, (api) => api.codebases.findAll())) ||
    ({} as CodebaseModel[]);

  return {
    codebases
  };
};
