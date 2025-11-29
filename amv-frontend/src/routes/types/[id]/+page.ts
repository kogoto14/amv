import type { TypeModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, params }) => {
  const type = (await ApiHandler.handle<TypeModel>(fetch, (api) => api.type.get(params.id)))!;

  return {
    title: `${type.name}`,
    type
  };
};
