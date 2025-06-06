// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 创建评分结果 POST /api/scoringresult/add */
export async function addScoringResultUsingPost(
  body: API.ScoringResultAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/api/scoringresult/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除评分结果 POST /api/scoringresult/delete */
export async function deleteScoringResultUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/scoringresult/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 编辑评分结果 POST /api/scoringresult/edit */
export async function editScoringResultUsingPost(
  body: API.ScoringResultEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/scoringresult/edit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据id获取评分结果 GET /api/scoringresult/get/vo */
export async function getScoringResultVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getScoringResultVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseScoringResultVO_>(
    "/api/scoringresult/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 分页获取评分结果列表(管理员) POST /api/scoringresult/list/page */
export async function listScoringResultByPageUsingPost(
  body: API.ScoringResultQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageScoringResult_>(
    "/api/scoringresult/list/page",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 分页获取评分结果列表 POST /api/scoringresult/list/page/vo */
export async function listScoringResultVoByPageUsingPost(
  body: API.ScoringResultQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageScoringResultVO_>(
    "/api/scoringresult/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 分页获取当前登录用户创建的评分结果列表 POST /api/scoringresult/my/list/page/vo */
export async function listMyScoringResultVoByPageUsingPost(
  body: API.ScoringResultQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageScoringResultVO_>(
    "/api/scoringresult/my/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 更新评分结果(管理员) POST /api/scoringresult/update */
export async function updateScoringResultUsingPost(
  body: API.ScoringResultUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/scoringresult/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
