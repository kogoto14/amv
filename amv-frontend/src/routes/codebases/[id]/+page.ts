import type { CodebaseModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params }) => {
  const codebase = (await ApiHandler.handle<CodebaseModel>(fetch, (api) =>
    api.codebases.get(params.id)
  ))!;

  return {
    title: `${codebase.name}`,
    codebase
  };
};
