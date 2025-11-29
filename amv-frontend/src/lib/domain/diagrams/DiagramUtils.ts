import type { CallTreeCriteriaModel } from '$lib/arch/api/Api';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

export default class DiagramUtils {
  static urlToCallTree(criteria: CallTreeCriteriaModel) {
    return `/diagrams/call-tree${CriteriaUtils.encode(criteria)}`;
  }
}
