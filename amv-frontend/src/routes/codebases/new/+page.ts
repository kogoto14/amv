import type { CodebaseModel } from '$lib/arch/api/Api';
import * as m from '$lib/paraglide/messages';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ url }) => {
  const codebase = {
    name: url.searchParams.get('name'),
    url: url.searchParams.get('url')
  } as CodebaseModel;

  return {
    title: m.register(),
    codebase
  };
};
