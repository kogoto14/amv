/* eslint-disable */
/* tslint:disable */
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

export interface CallTreeCriteriaModel {
  signaturePattern?: string;
  callTreeRequired?: boolean;
  calledTreeRequired?: boolean;
  render?: string;
  /** @format int32 */
  limit?: number;
}

export interface CallTreeModel {
  method: MethodModel;
  callTree: CallTreeElementModel[];
  calledTree: CallTreeElementModel[];
}

export interface CallTreeElementModel {
  call: boolean;
  /** @format int32 */
  depth: number;
  method: MethodModel;
  urlAt: string;
  /** @format int32 */
  lineNoAt: number;
  elementTags: string[];
  typeTags: string[];
  methodTags: string[];
}

export interface CodebaseModel {
  id: string;
  name?: string;
  url?: string;
  token?: string;
  branch?: string;
  commitHash?: string;
  /** @format int64 */
  analyzedIn?: number;
  projects: ProjectModel[];
  /** @format int64 */
  version?: number;
  status: CodebaseStatusModel;
}

export interface CodebaseSearchCriteriaModel {
  text?: string;
  pageControl: PageControl;
  sortOrders?: SortOrder[];
}

export interface CodebaseSearchResultModel {
  list?: CodebaseModel[];
  pageResult?: PageResult;
}

export interface CodebaseStatusModel {
  analyzing: boolean;
  checkedOut: boolean;
  projectsLoaded: boolean;
  metadataExtracted: boolean;
}

export interface CrudModel {
  /** @uniqueItems true */
  entryPoints: string[];
  /** @uniqueItems true */
  tables: string[];
  crud: Record<string, Record<string, string>>;
  methods: Record<string, MethodModel>;
}

export interface CrudPointModel {
  dataName: string;
  type: string;
  crud: string;
}

export interface DiagramModel {
  text: string;
  image: string;
}

export interface EntryPointModel {
  path?: string;
  httpMethod?: string;
}

export interface FieldModel {
  id?: FieldDtoId;
  name?: string;
  type?: string;
}

export interface FieldDtoId {
  typeId?: string;
  /** @format int32 */
  seqNo?: number;
}

export interface InteractionDocumentCriteriaModel {
  qualifiedSignature: string;
  participableStereotypes: string[];
}

export interface InteractionResponseModel {
  sequenceDiagram: SequenceDiagramModel;
  classDiagram: DiagramModel;
}

export interface LoadStatusModel {
  status?: string;
}

export interface MethodCallModel {
  qualifiedSignature?: string;
  interfaceSignature?: string;
  fallbackSignature?: string;
  unsolvedReason?: string;
  /** @format int32 */
  lineNo?: number;
}

export interface MethodModel {
  namespace?: string;
  id: MethodDtoId;
  type: string;
  name: string;
  returnType: string;
  qualifiedSignature?: string;
  simpleSignature?: string;
  srcUrl?: string;
  dummy: boolean;
  /** @uniqueItems true */
  calls: MethodCallModel[];
  entryPoint?: EntryPointModel;
  crudPoints: CrudPointModel[];
}

export interface MethodDtoId {
  typeId?: string;
  /** @format int32 */
  seqNo?: number;
}

export interface MethodSearchCriteriaModel {
  text?: string;
  pageControl: PageControl;
  sortOrders?: SortOrder[];
}

export interface MethodSearchResultModel {
  list?: MethodModel[];
  pageResult?: PageResult;
}

export interface PageControl {
  /** @format int32 */
  pageSize: number;
  /** @format int32 */
  pageNumber: number;
  /** @format int32 */
  pageNumsRange?: number;
  /** @format int32 */
  offset?: number;
}

export interface PageResult {
  /** @format int64 */
  count: number;
  /** @format int32 */
  start: number;
  /** @format int64 */
  end: number;
  /** @format int32 */
  lastPageNum: number;
  pageNums: number[];
}

export interface ProjectModel {
  id: string;
  name?: string;
  path?: string;
  sourceDirs?: string[];
  classpathFile?: string;
  /** @format int64 */
  version: number;
}

export interface ProjectSearchCriteriaModel {
  pageControl: PageControl;
  sortOrders?: SortOrder[];
}

export interface ProjectSearchResultModel {
  list?: ProjectModel[];
  pageResult?: PageResult;
}

export interface SequenceDiagramModel {
  diagram: DiagramModel;
  participantStereotypes: string[];
}

export interface SortOrder {
  asc: boolean;
  field: string;
}

export interface SourceFileModel {
  namespace?: string;
  path?: string;
}

export interface TypeModel {
  id: string;
  name: string;
  qualifiedName?: string;
  kind?: string;
  annotations?: string;
  /** @format int32 */
  unsolvedCnt?: number;
  unsolvedRate?: number;
  fields: FieldModel[];
  methods: MethodModel[];
  sourceFile: SourceFileModel;
  /** @format int64 */
  version: number;
}

export interface TypeSearchCriteriaModel {
  text?: string;
  pageControl: PageControl;
  sortOrders?: SortOrder[];
}

export interface TypeSearchResultModel {
  list?: TypeModel[];
  pageResult?: PageResult;
}

export type QueryParamsType = Record<string | number, any>;
export type ResponseFormat = keyof Omit<Body, "body" | "bodyUsed">;

export interface FullRequestParams extends Omit<RequestInit, "body"> {
  /** set parameter to `true` for call `securityWorker` for this request */
  secure?: boolean;
  /** request path */
  path: string;
  /** content type of request body */
  type?: ContentType;
  /** query params */
  query?: QueryParamsType;
  /** format of response (i.e. response.json() -> format: "json") */
  format?: ResponseFormat;
  /** request body */
  body?: unknown;
  /** base url */
  baseUrl?: string;
  /** request cancellation token */
  cancelToken?: CancelToken;
}

export type RequestParams = Omit<
  FullRequestParams,
  "body" | "method" | "query" | "path"
>;

export interface ApiConfig<SecurityDataType = unknown> {
  baseUrl?: string;
  baseApiParams?: Omit<RequestParams, "baseUrl" | "cancelToken" | "signal">;
  securityWorker?: (
    securityData: SecurityDataType | null
  ) => Promise<RequestParams | void> | RequestParams | void;
  customFetch?: typeof fetch;
}

export interface HttpResponse<D extends unknown, E extends unknown = unknown>
  extends Response {
  data: D;
  error: E;
}

type CancelToken = Symbol | string | number;

export enum ContentType {
  Json = "application/json",
  FormData = "multipart/form-data",
  UrlEncoded = "application/x-www-form-urlencoded",
  Text = "text/plain",
}

export class HttpClient<SecurityDataType = unknown> {
  public baseUrl: string = "http://localhost:8081";
  private securityData: SecurityDataType | null = null;
  private securityWorker?: ApiConfig<SecurityDataType>["securityWorker"];
  private abortControllers = new Map<CancelToken, AbortController>();
  private customFetch = (...fetchParams: Parameters<typeof fetch>) =>
    fetch(...fetchParams);

  private baseApiParams: RequestParams = {
    credentials: "same-origin",
    headers: {},
    redirect: "follow",
    referrerPolicy: "no-referrer",
  };

  constructor(apiConfig: ApiConfig<SecurityDataType> = {}) {
    Object.assign(this, apiConfig);
  }

  public setSecurityData = (data: SecurityDataType | null) => {
    this.securityData = data;
  };

  protected encodeQueryParam(key: string, value: any) {
    const encodedKey = encodeURIComponent(key);
    return `${encodedKey}=${encodeURIComponent(typeof value === "number" ? value : `${value}`)}`;
  }

  protected addQueryParam(query: QueryParamsType, key: string) {
    return this.encodeQueryParam(key, query[key]);
  }

  protected addArrayQueryParam(query: QueryParamsType, key: string) {
    const value = query[key];
    return value.map((v: any) => this.encodeQueryParam(key, v)).join("&");
  }

  protected toQueryString(rawQuery?: QueryParamsType): string {
    const query = rawQuery || {};
    const keys = Object.keys(query).filter(
      (key) => "undefined" !== typeof query[key]
    );
    return keys
      .map((key) =>
        Array.isArray(query[key])
          ? this.addArrayQueryParam(query, key)
          : this.addQueryParam(query, key)
      )
      .join("&");
  }

  protected addQueryParams(rawQuery?: QueryParamsType): string {
    const queryString = this.toQueryString(rawQuery);
    return queryString ? `?${queryString}` : "";
  }

  private contentFormatters: Record<ContentType, (input: any) => any> = {
    [ContentType.Json]: (input: any) =>
      input !== null && (typeof input === "object" || typeof input === "string")
        ? JSON.stringify(input)
        : input,
    [ContentType.Text]: (input: any) =>
      input !== null && typeof input !== "string"
        ? JSON.stringify(input)
        : input,
    [ContentType.FormData]: (input: any) =>
      Object.keys(input || {}).reduce((formData, key) => {
        const property = input[key];
        formData.append(
          key,
          property instanceof Blob
            ? property
            : typeof property === "object" && property !== null
              ? JSON.stringify(property)
              : `${property}`
        );
        return formData;
      }, new FormData()),
    [ContentType.UrlEncoded]: (input: any) => this.toQueryString(input),
  };

  protected mergeRequestParams(
    params1: RequestParams,
    params2?: RequestParams
  ): RequestParams {
    return {
      ...this.baseApiParams,
      ...params1,
      ...(params2 || {}),
      headers: {
        ...(this.baseApiParams.headers || {}),
        ...(params1.headers || {}),
        ...((params2 && params2.headers) || {}),
      },
    };
  }

  protected createAbortSignal = (
    cancelToken: CancelToken
  ): AbortSignal | undefined => {
    if (this.abortControllers.has(cancelToken)) {
      const abortController = this.abortControllers.get(cancelToken);
      if (abortController) {
        return abortController.signal;
      }
      return void 0;
    }

    const abortController = new AbortController();
    this.abortControllers.set(cancelToken, abortController);
    return abortController.signal;
  };

  public abortRequest = (cancelToken: CancelToken) => {
    const abortController = this.abortControllers.get(cancelToken);

    if (abortController) {
      abortController.abort();
      this.abortControllers.delete(cancelToken);
    }
  };

  public request = async <T = any, E = any>({
    body,
    secure,
    path,
    type,
    query,
    format,
    baseUrl,
    cancelToken,
    ...params
  }: FullRequestParams): Promise<HttpResponse<T, E>> => {
    const secureParams =
      ((typeof secure === "boolean" ? secure : this.baseApiParams.secure) &&
        this.securityWorker &&
        (await this.securityWorker(this.securityData))) ||
      {};
    const requestParams = this.mergeRequestParams(params, secureParams);
    const queryString = query && this.toQueryString(query);
    const payloadFormatter = this.contentFormatters[type || ContentType.Json];
    const responseFormat = format || requestParams.format;

    return this.customFetch(
      `${baseUrl || this.baseUrl || ""}${path}${queryString ? `?${queryString}` : ""}`,
      {
        ...requestParams,
        headers: {
          ...(requestParams.headers || {}),
          ...(type && type !== ContentType.FormData
            ? { "Content-Type": type }
            : {}),
        },
        signal:
          (cancelToken
            ? this.createAbortSignal(cancelToken)
            : requestParams.signal) || null,
        body:
          typeof body === "undefined" || body === null
            ? null
            : payloadFormatter(body),
      }
    ).then(async (response) => {
      const r = response.clone() as HttpResponse<T, E>;
      r.data = null as unknown as T;
      r.error = null as unknown as E;

      const data = !responseFormat
        ? r
        : await response[responseFormat]()
            .then((data) => {
              if (r.ok) {
                r.data = data;
              } else {
                r.error = data;
              }
              return r;
            })
            .catch((e) => {
              r.error = e;
              return r;
            });

      if (cancelToken) {
        this.abortControllers.delete(cancelToken);
      }

      if (!response.ok) throw data;
      return data;
    });
  };
}

/**
 * @title amv-backend API
 * @version 1.0-SNAPSHOT
 * @baseUrl http://localhost:8081
 */
export class Api<
  SecurityDataType extends unknown,
> extends HttpClient<SecurityDataType> {
  async = {
    /**
     * No description
     *
     * @tags Async Exec Controller
     * @name GetStatus
     * @summary Get Status
     * @request GET:/api/async/{execId}
     */
    getStatus: (execId: string, params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/async/${execId}`,
        method: "GET",
        ...params,
      }),
  };
  codebases = {
    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Save
     * @summary Save
     * @request POST:/api/codebases
     */
    save: (data: CodebaseModel, params: RequestParams = {}) =>
      this.request<string, void>({
        path: `/api/codebases`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name FindAll
     * @summary Find All
     * @request GET:/api/codebases/all
     */
    findAll: (params: RequestParams = {}) =>
      this.request<CodebaseModel[], any>({
        path: `/api/codebases/all`,
        method: "GET",
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Analyze
     * @summary Analyze
     * @request POST:/api/codebases/analyze/{id}
     */
    analyze: (id: string, params: RequestParams = {}) =>
      this.request<void, any>({
        path: `/api/codebases/analyze/${id}`,
        method: "POST",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Load
     * @summary Load
     * @request POST:/api/codebases/load/{id}
     */
    load: (id: string, params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/codebases/load/${id}`,
        method: "POST",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Search
     * @summary Search
     * @request POST:/api/codebases/search
     */
    search: (data: CodebaseSearchCriteriaModel, params: RequestParams = {}) =>
      this.request<CodebaseSearchResultModel, void>({
        path: `/api/codebases/search`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Update
     * @summary Update
     * @request PUT:/api/codebases/{id}
     */
    update: (id: string, data: CodebaseModel, params: RequestParams = {}) =>
      this.request<string, void>({
        path: `/api/codebases/${id}`,
        method: "PUT",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Get
     * @summary Get
     * @request GET:/api/codebases/{id}
     */
    get: (id: string, params: RequestParams = {}) =>
      this.request<CodebaseModel, any>({
        path: `/api/codebases/${id}`,
        method: "GET",
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Codebase Controller
     * @name Delete
     * @summary Delete
     * @request DELETE:/api/codebases/{id}
     */
    delete: (id: string, data: CodebaseModel, params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/codebases/${id}`,
        method: "DELETE",
        body: data,
        type: ContentType.Json,
        ...params,
      }),
  };
  diagrams = {
    /**
     * No description
     *
     * @tags Diagram Controller
     * @name CallTree
     * @summary Call Tree
     * @request POST:/api/diagrams/call-tree
     */
    callTree: (data: CallTreeCriteriaModel, params: RequestParams = {}) =>
      this.request<CallTreeModel[], void>({
        path: `/api/diagrams/call-tree`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Diagram Controller
     * @name ClassDiagram
     * @summary Class Diagram
     * @request GET:/api/diagrams/class
     */
    classDiagram: (
      query?: {
        qualifiedName?: string;
      },
      params: RequestParams = {}
    ) =>
      this.request<string, any>({
        path: `/api/diagrams/class`,
        method: "GET",
        query: query,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Diagram Controller
     * @name GetCrudDiagram
     * @summary Get Crud Diagram
     * @request GET:/api/diagrams/crud
     */
    getCrudDiagram: (params: RequestParams = {}) =>
      this.request<CrudModel, any>({
        path: `/api/diagrams/crud`,
        method: "GET",
        format: "json",
        ...params,
      }),
  };
  documents = {
    /**
     * No description
     *
     * @tags Document Controller
     * @name GetInteractionDocument
     * @summary Get Interaction Document
     * @request POST:/api/documents/interaction
     */
    getInteractionDocument: (
      data: InteractionDocumentCriteriaModel,
      params: RequestParams = {}
    ) =>
      this.request<InteractionResponseModel, void>({
        path: `/api/documents/interaction`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),
  };
  front = {
    /**
     * No description
     *
     * @tags Front Controller
     * @name Get
     * @summary Get
     * @request GET:/api/front
     */
    get: (params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/front`,
        method: "GET",
        ...params,
      }),
  };
  jpql = {
    /**
     * No description
     *
     * @tags Jpql Exec Controller
     * @name Exec
     * @summary Exec
     * @request GET:/api/jpql
     */
    exec: (
      query?: {
        q?: string;
      },
      params: RequestParams = {}
    ) =>
      this.request<any, any>({
        path: `/api/jpql`,
        method: "GET",
        query: query,
        format: "json",
        ...params,
      }),
  };
  methods = {
    /**
     * No description
     *
     * @tags Method Controller
     * @name Search
     * @summary Search
     * @request POST:/api/methods/search
     */
    search: (data: MethodSearchCriteriaModel, params: RequestParams = {}) =>
      this.request<MethodSearchResultModel, void>({
        path: `/api/methods/search`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),
  };
  processes = {
    /**
     * No description
     *
     * @tags Process Controller
     * @name Get
     * @summary Get
     * @request GET:/api/processes/load/status
     */
    get: (params: RequestParams = {}) =>
      this.request<LoadStatusModel, any>({
        path: `/api/processes/load/status`,
        method: "GET",
        format: "json",
        ...params,
      }),
  };
  project = {
    /**
     * No description
     *
     * @tags Project Controller
     * @name Save
     * @summary Save
     * @request POST:/api/project
     */
    save: (data: ProjectModel, params: RequestParams = {}) =>
      this.request<string, void>({
        path: `/api/project`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Project Controller
     * @name Search
     * @summary Search
     * @request POST:/api/project/search
     */
    search: (data: ProjectSearchCriteriaModel, params: RequestParams = {}) =>
      this.request<ProjectSearchResultModel, void>({
        path: `/api/project/search`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Project Controller
     * @name Update
     * @summary Update
     * @request PUT:/api/project/{id}
     */
    update: (id: string, data: ProjectModel, params: RequestParams = {}) =>
      this.request<string, void>({
        path: `/api/project/${id}`,
        method: "PUT",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Project Controller
     * @name Get
     * @summary Get
     * @request GET:/api/project/{id}
     */
    get: (id: string, params: RequestParams = {}) =>
      this.request<ProjectModel, any>({
        path: `/api/project/${id}`,
        method: "GET",
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Project Controller
     * @name Delete
     * @summary Delete
     * @request DELETE:/api/project/{id}
     */
    delete: (id: string, data: ProjectModel, params: RequestParams = {}) =>
      this.request<string, any>({
        path: `/api/project/${id}`,
        method: "DELETE",
        body: data,
        type: ContentType.Json,
        ...params,
      }),
  };
  type = {
    /**
     * No description
     *
     * @tags Type Controller
     * @name Save
     * @summary Save
     * @request POST:/api/type
     */
    save: (data: TypeModel, params: RequestParams = {}) =>
      this.request<string, void>({
        path: `/api/type`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Type Controller
     * @name Search
     * @summary Search
     * @request POST:/api/type/search
     */
    search: (data: TypeSearchCriteriaModel, params: RequestParams = {}) =>
      this.request<TypeSearchResultModel, void>({
        path: `/api/type/search`,
        method: "POST",
        body: data,
        type: ContentType.Json,
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Type Controller
     * @name Update
     * @summary Update
     * @request PUT:/api/type/{idOrQualifiedName}
     */
    update: (
      idOrQualifiedName: string,
      data: TypeModel,
      params: RequestParams = {}
    ) =>
      this.request<string, void>({
        path: `/api/type/${idOrQualifiedName}`,
        method: "PUT",
        body: data,
        type: ContentType.Json,
        ...params,
      }),

    /**
     * No description
     *
     * @tags Type Controller
     * @name Get
     * @summary Get
     * @request GET:/api/type/{idOrQualifiedName}
     */
    get: (idOrQualifiedName: string, params: RequestParams = {}) =>
      this.request<TypeModel, any>({
        path: `/api/type/${idOrQualifiedName}`,
        method: "GET",
        format: "json",
        ...params,
      }),

    /**
     * No description
     *
     * @tags Type Controller
     * @name Delete
     * @summary Delete
     * @request DELETE:/api/type/{idOrQualifiedName}
     */
    delete: (
      idOrQualifiedName: string,
      data: TypeModel,
      params: RequestParams = {}
    ) =>
      this.request<string, any>({
        path: `/api/type/${idOrQualifiedName}`,
        method: "DELETE",
        body: data,
        type: ContentType.Json,
        ...params,
      }),
  };
}
