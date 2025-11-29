import ApiHandler from '$lib/arch/api/ApiHandler';
import type {
  InteractionDocumentCriteriaModel,
  InteractionResponseModel,
  MethodSearchCriteriaModel,
  MethodSearchResultModel
} from '$lib/arch/api/Api';
import type { PageLoad } from './$types';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

export type CriteriaModel = {
  methodCriteria: MethodSearchCriteriaModel;
  documentCriteria: InteractionDocumentCriteriaModel;
  init: boolean;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    methodCriteria: {},
    documentCriteria: {
      participableStereotypes: []
    },
    init: true,
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const methods = await searchMethods(criteria.methodCriteria, fetch);

  criteria.documentCriteria.qualifiedSignature =
    methods.list?.length === 1 ? methods.list[0].qualifiedSignature! : '';

  const doc = await getDoc(criteria.documentCriteria, fetch);

  if (doc.sequenceDiagram && criteria.init) {
    criteria.documentCriteria.participableStereotypes = doc.sequenceDiagram.participantStereotypes;
    criteria.init = false;
  }

  return {
    criteria,
    methods,
    doc
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

async function getDoc(criteria: InteractionDocumentCriteriaModel, fetch: typeof window.fetch) {
  if (!criteria.qualifiedSignature) {
    return {} as InteractionResponseModel;
  }

  return (
    (await ApiHandler.handle<InteractionResponseModel>(fetch, (api) =>
      api.documents.getInteractionDocument(criteria)
    )) || ({} as InteractionResponseModel)
  );
}
