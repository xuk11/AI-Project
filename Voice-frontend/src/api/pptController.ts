// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** convertPPT POST /api/ppt/convert */
export async function convertPptUsingPost(
  body: API.PPTConvertRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString_>('/api/ppt/convert', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
