package dev.aulait.amv.arch.async;

import dev.aulait.amv.arch.test.RestClientUtils;

public class AsyncExecClient {
  public String getStatus(String execId) {
    return RestClientUtils.get(AsyncExecController.BASE_PATH + "/" + execId, String.class);
  }
}
