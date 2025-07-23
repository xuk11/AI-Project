// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** addArticle POST /api/article/add */
export async function addArticleUsingPost(
  body: API.ArticleAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/article/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** addBookshelf POST /api/article/add/bookshelf */
export async function addBookshelfUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.addBookshelfUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/article/add/bookshelf', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** addChapter POST /api/article/add/chapter */
export async function addChapterUsingPost(
  body: API.ChapterAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/article/add/chapter', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteArticle POST /api/article/delete */
export async function deleteArticleUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/article/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getAllArticle POST /api/article/get/all */
export async function getAllArticleUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListArticle_>('/api/article/get/all', {
    method: 'POST',
    ...(options || {}),
  })
}

/** getChapter GET /api/article/get/chapter */
export async function getChapterUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getChapterUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseChapter_>('/api/article/get/chapter', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** getTopArticle POST /api/article/get/top */
export async function getTopArticleUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListArticle_>('/api/article/get/top', {
    method: 'POST',
    ...(options || {}),
  })
}

/** getArticleVOById GET /api/article/get/vo */
export async function getArticleVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getArticleVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseArticleVO_>('/api/article/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** isMyBook POST /api/article/is/myBook */
export async function isMyBookUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isMyBookUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/article/is/myBook', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** listChapter POST /api/article/list/chapter */
export async function listChapterUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listChapterUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListChapter_>('/api/article/list/chapter', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** listCreateBooks POST /api/article/list/create/books */
export async function listCreateBooksUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticleVO_>('/api/article/list/create/books', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listMyBooks POST /api/article/list/my/books */
export async function listMyBooksUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticleVO_>('/api/article/list/my/books', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listArticleByPage POST /api/article/list/page */
export async function listArticleByPageUsingPost(
  body: API.ArticleQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticle_>('/api/article/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listArticleVOByPage POST /api/article/list/page/vo */
export async function listArticleVoByPageUsingPost(
  body: API.ArticleQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticleVO_>('/api/article/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** readArticle POST /api/article/read */
export async function readArticleUsingPost(
  body: API.ReadArticleRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString_>('/api/article/read', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateArticle POST /api/article/update */
export async function updateArticleUsingPost(
  body: API.ArticleUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/article/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** uploadContentImg POST /api/article/upload/contentImg */
export async function uploadContentImgUsingPost(
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

  return request<API.BaseResponseString_>('/api/article/upload/contentImg', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** uploadCoverImg POST /api/article/upload/coverImg */
export async function uploadCoverImgUsingPost(
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

  return request<API.BaseResponseString_>('/api/article/upload/coverImg', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
