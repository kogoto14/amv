import type { TypeModel } from "@api/Api";
import BooleanUtils from '@arch/BooleanUtils';
import DateUtils from '@arch/DateUtils';
import StringUtils from '@arch/StringUtils';
import NumberUtils from '@arch/NumberUtils';

export default class TypeFactory {
  static create() {
    const type = {} as TypeModel;
    return type;
  }

  static createRandomType() {
    return {
      
      id: StringUtils.generateRandomString(),
      name: StringUtils.generateRandomString(),
      qualifiedName: StringUtils.generateRandomString(),
      kind: StringUtils.generateRandomString(),
      annotations: StringUtils.generateRandomString()
    } as TypeModel;
  }

  static createRandomTypeWithId(id : string) {
    return {
      id,
      name: StringUtils.generateRandomString(),
      qualifiedName: StringUtils.generateRandomString(),
      kind: StringUtils.generateRandomString(),
      annotations: StringUtils.generateRandomString()
    } as TypeModel;
  }

}