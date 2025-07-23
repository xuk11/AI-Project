// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** updateAudio POST /api/audioCommunity */
export async function updateAudioUsingPost(
  body: API.UpdateAudio,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/audioCommunity', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addAudioCommunity POST /api/audioCommunity/add */
export async function addAudioCommunityUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addAudioCommunityUsingPOSTParams,
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

  return request<API.BaseResponseLong_>('/api/audioCommunity/add', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** deleteAudioCommunity POST /api/audioCommunity/delete */
export async function deleteAudioCommunityUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/audioCommunity/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getCustomAudio POST /api/audioCommunity/get/customAudio */
export async function getCustomAudioUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListAudioCommunityVO_>('/api/audioCommunity/get/customAudio', {
    method: 'POST',
    ...(options || {}),
  })
}

/** getAudioCommunityVOById GET /api/audioCommunity/get/vo */
export async function getAudioCommunityVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAudioCommunityVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAudioCommunityVO_>('/api/audioCommunity/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** isLiked GET /api/audioCommunity/isLiked */
export async function isLikedUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isLikedUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/audioCommunity/isLiked', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** likeAudio POST /api/audioCommunity/like */
export async function likeAudioUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeAudioUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>('/api/audioCommunity/like', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** listAudioCommunityByPage POST /api/audioCommunity/list/page */
export async function listAudioCommunityByPageUsingPost(
  body: API.AudioCommunityQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAudioCommunity_>('/api/audioCommunity/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listAudioCommunityVOByPage POST /api/audioCommunity/list/page/vo */
export async function listAudioCommunityVoByPageUsingPost(
  body: API.AudioCommunityQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAudioCommunityVO_>('/api/audioCommunity/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listMyAudioCommunityVOByPage POST /api/audioCommunity/my/list/page/vo */
export async function listMyAudioCommunityVoByPageUsingPost(
  body: API.AudioCommunityQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAudioCommunityVO_>('/api/audioCommunity/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** playAudio POST /api/audioCommunity/play */
export async function playAudioUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.playAudioUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>('/api/audioCommunity/play', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** uploadAudio POST /api/audioCommunity/upload */
export async function uploadAudioUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadAudioUsingPOSTParams,
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

  return request<API.BaseResponseString_>('/api/audioCommunity/upload', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
