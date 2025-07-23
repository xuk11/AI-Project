// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** deleteAuditoryppt POST /api/auditoryppt/delete */
export async function deleteAuditorypptUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/auditoryppt/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** videoList POST /api/auditoryppt/list */
export async function videoListUsingPost(
  body: API.AuditorypptQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListAuditoryppt_>('/api/auditoryppt/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getMyAuditorypptList POST /api/auditoryppt/list/my */
export async function getMyAuditorypptListUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListAuditoryppt_>('/api/auditoryppt/list/my', {
    method: 'POST',
    ...(options || {}),
  })
}

/** listAuditorypptByPage POST /api/auditoryppt/list/page */
export async function listAuditorypptByPageUsingPost(
  body: API.AuditorypptQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuditoryppt_>('/api/auditoryppt/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateAuditoryppt POST /api/auditoryppt/update */
export async function updateAuditorypptUsingPost(
  body: API.UpdateVideo,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/auditoryppt/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** upload POST /api/auditoryppt/upload */
export async function uploadUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadUsingPOSTParams,
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, JSON.stringify(item))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<API.BaseResponseString_>('/api/auditoryppt/upload', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
