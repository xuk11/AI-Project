// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** tts POST /api/tts */
export async function ttsUsingPost(body: API.TTSRequest, options?: { [key: string]: any }) {
  return request<string>('/api/tts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** ttsByCustom POST /api/tts/custom */
export async function ttsByCustomUsingPost(body: API.TTSRequest, options?: { [key: string]: any }) {
  return request<string>('/api/tts/custom', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** qwen POST /api/tts/qwen */
export async function qwenUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.qwenUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/tts/qwen', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
