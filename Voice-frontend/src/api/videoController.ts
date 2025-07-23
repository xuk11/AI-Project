// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** deleteVideo POST /api/video/delete */
export async function deleteVideoUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/video/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getVideoUrl POST /api/video/getUrl */
export async function getVideoUrlUsingPost(
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

  return request<API.BaseResponseString_>('/api/video/getUrl', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** videoList POST /api/video/list */
export async function videoListUsingPost1(
  body: API.VideoQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListVideo_>('/api/video/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getMyVideoList POST /api/video/list/my */
export async function getMyVideoListUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListVideo_>('/api/video/list/my', {
    method: 'POST',
    ...(options || {}),
  })
}

/** listVideoByPage POST /api/video/list/page */
export async function listVideoByPageUsingPost(
  body: API.VideoQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageVideo_>('/api/video/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** VideoReset POST /api/video/reset */
export async function videoResetUsingPost(
  body: API.VideoRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString_>('/api/video/reset', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateVideo POST /api/video/update */
export async function updateVideoUsingPost(
  body: API.UpdateVideo,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/video/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** upload POST /api/video/upload */
export async function uploadUsingPost1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadUsingPOST1Params,
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

  return request<API.BaseResponseString_>('/api/video/upload', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
