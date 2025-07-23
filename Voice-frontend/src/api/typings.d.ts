declare namespace API {
  type addAudioCommunityUsingPOSTParams = {
    /** type */
    type?: number
    /** uploadPathPrefix */
    uploadPathPrefix?: string
  }

  type addAudioFileUsingPOSTParams = {
    /** type */
    type?: number
    /** uploadPathPrefix */
    uploadPathPrefix?: string
  }

  type addBookshelfUsingPOSTParams = {
    /** articleId */
    articleId?: number
  }

  type Article = {
    author?: string
    category?: string
    categoryCount?: number
    content?: string
    countSize?: number
    coverImage?: string
    createTime?: string
    description?: string
    id?: number
    isDelete?: number
    likesCount?: number
    priority?: number
    seodescription?: string
    seokeywords?: string
    seotitle?: string
    status?: number
    summary?: string
    tags?: string
    title?: string
    type?: number
    updateTime?: string
    userId?: number
    viewsCount?: number
  }

  type ArticleAddRequest = {
    author?: string
    content?: string
    contentImages?: string[]
    coverImage?: string
    summary?: string
    title?: string
  }

  type ArticleQueryRequest = {
    author?: string
    content?: string[]
    coverImage?: string
    current?: number
    id?: number
    likesCount?: number
    pageSize?: number
    priority?: number
    searchText?: string
    seodescription?: string
    seokeywords?: string
    seotitle?: string
    sortField?: string
    sortOrder?: string
    summary?: string[]
    tags?: string[]
    title?: string
    userId?: number
    viewsCount?: number
  }

  type ArticleUpdateRequest = {
    author?: string
    content?: string
    contentImages?: string[]
    coverImage?: string
    id?: number
    summary?: string
    title?: string
  }

  type ArticleVO = {
    author?: string
    category?: string
    categoryCount?: number
    content?: string[]
    countSize?: number
    coverImage?: string
    createTime?: string
    description?: string
    id?: number
    likesCount?: number
    priority?: number
    seodescription?: string
    seokeywords?: string
    seotitle?: string
    status?: number
    summary?: string[]
    tags?: string[]
    title?: string
    type?: number
    updateTime?: string
    userId?: number
    userVO?: UserVO
    viewsCount?: number
  }

  type AudioCommunity = {
    createTime?: string
    description?: string
    duration?: string
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    isShare?: number
    likeCount?: number
    picture?: string
    playCount?: number
    tags?: string
    title?: string
    type?: number
    updateTime?: string
    userId?: number
  }

  type AudioCommunityQueryRequest = {
    current?: number
    description?: string
    duration?: number
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    pageSize?: number
    searchText?: string
    sortField?: string
    sortOrder?: string
    tags?: string[]
    title?: string
    userId?: number
  }

  type AudioCommunityVO = {
    createTime?: string
    description?: string
    duration?: string
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    isShare?: number
    likeCount?: number
    picture?: string
    playCount?: number
    tags?: string[]
    title?: string
    type?: number
    updateTime?: string
    userId?: number
    userVO?: UserVO
  }

  type AudioFile = {
    createTime?: string
    description?: string
    duration?: string
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    picture?: string
    tags?: string
    title?: string
    type?: number
    updateTime?: string
    userId?: number
  }

  type AudioFileQueryRequest = {
    current?: number
    description?: string
    duration?: number
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    pageSize?: number
    searchText?: string
    sortField?: string
    sortOrder?: string
    tags?: string[]
    title?: string
    userId?: number
  }

  type AudioFileVO = {
    createTime?: string
    description?: string
    duration?: string
    fileName?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    picture?: string
    tags?: string[]
    title?: string
    type?: number
    updateTime?: string
    userId?: number
    userVO?: UserVO
  }

  type Auditoryppt = {
    audioId?: number
    createTime?: string
    duration?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    name?: string
    spaceId?: number
    updateTime?: string
    userId?: number
  }

  type AuditorypptQueryRequest = {
    audioId?: number
    current?: number
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    name?: string
    pageSize?: number
    sortField?: string
    sortOrder?: string
    spaceId?: number
    userId?: number
  }

  type BaseResponseArticleVO_ = {
    code?: number
    data?: ArticleVO
    message?: string
  }

  type BaseResponseAudioCommunityVO_ = {
    code?: number
    data?: AudioCommunityVO
    message?: string
  }

  type BaseResponseAudioFileVO_ = {
    code?: number
    data?: AudioFileVO
    message?: string
  }

  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseChapter_ = {
    code?: number
    data?: Chapter
    message?: string
  }

  type BaseResponseInt_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponseListArticle_ = {
    code?: number
    data?: Article[]
    message?: string
  }

  type BaseResponseListAudioCommunityVO_ = {
    code?: number
    data?: AudioCommunityVO[]
    message?: string
  }

  type BaseResponseListAudioFileVO_ = {
    code?: number
    data?: AudioFileVO[]
    message?: string
  }

  type BaseResponseListAuditoryppt_ = {
    code?: number
    data?: Auditoryppt[]
    message?: string
  }

  type BaseResponseListChapter_ = {
    code?: number
    data?: Chapter[]
    message?: string
  }

  type BaseResponseListVideo_ = {
    code?: number
    data?: Video[]
    message?: string
  }

  type BaseResponseLoginUserVO_ = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageArticle_ = {
    code?: number
    data?: PageArticle_
    message?: string
  }

  type BaseResponsePageArticleVO_ = {
    code?: number
    data?: PageArticleVO_
    message?: string
  }

  type BaseResponsePageAudioCommunity_ = {
    code?: number
    data?: PageAudioCommunity_
    message?: string
  }

  type BaseResponsePageAudioCommunityVO_ = {
    code?: number
    data?: PageAudioCommunityVO_
    message?: string
  }

  type BaseResponsePageAudioFile_ = {
    code?: number
    data?: PageAudioFile_
    message?: string
  }

  type BaseResponsePageAudioFileVO_ = {
    code?: number
    data?: PageAudioFileVO_
    message?: string
  }

  type BaseResponsePageAuditoryppt_ = {
    code?: number
    data?: PageAuditoryppt_
    message?: string
  }

  type BaseResponsePageUser_ = {
    code?: number
    data?: PageUser_
    message?: string
  }

  type BaseResponsePageUserVO_ = {
    code?: number
    data?: PageUserVO_
    message?: string
  }

  type BaseResponsePageVideo_ = {
    code?: number
    data?: PageVideo_
    message?: string
  }

  type BaseResponseString_ = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO_ = {
    code?: number
    data?: UserVO
    message?: string
  }

  type Chapter = {
    articleId?: number
    chapterOrder?: number
    chapterTitle?: string
    content?: string
    createTime?: string
    id?: number
    isDelete?: number
    isFree?: number
    updateTime?: string
    wordCount?: number
  }

  type ChapterAddRequest = {
    articleId?: number
    chapterOrder?: number
    chapterTitle?: string
    content?: string
    isFree?: number
    wordCount?: number
  }

  type DeleteRequest = {
    id?: number
  }

  type getArticleVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getAudioCommunityVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getAudioFileVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getChapterUsingGETParams = {
    /** chapterId */
    chapterId?: number
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type isLikedUsingGETParams = {
    /** id */
    id?: number
  }

  type isMyBookUsingPOSTParams = {
    /** articleId */
    articleId?: number
  }

  type likeAudioUsingPOSTParams = {
    /** id */
    id?: number
  }

  type listChapterUsingPOSTParams = {
    /** articleId */
    articleId?: number
  }

  type LoginUserVO = {
    createTime?: string
    id?: number
    updateTime?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type OrderItem = {
    asc?: boolean
    column?: string
  }

  type PageArticle_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: Article[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageArticleVO_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: ArticleVO[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageAudioCommunity_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: AudioCommunity[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageAudioCommunityVO_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: AudioCommunityVO[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageAudioFile_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: AudioFile[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageAudioFileVO_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: AudioFileVO[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageAuditoryppt_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: Auditoryppt[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  type PageUser_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: User[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageUserVO_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: UserVO[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageVideo_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: Video[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type playAudioUsingPOSTParams = {
    /** id */
    id?: number
  }

  type PPTConvertRequest = {
    fileHash?: string
    imageList?: string[]
    type?: number
  }

  type qwenUsingPOSTParams = {
    /** message */
    message: string
  }

  type ReadArticleRequest = {
    text?: string
    type?: number
  }

  type testDownloadFileUsingGETParams = {
    /** filepath */
    filepath?: string
  }

  type TTSRequest = {
    batch_size?: number
    batch_threshold?: number
    parallel_infer?: boolean
    prompt_lang?: string
    prompt_text?: string
    ref_audio_path?: string
    repetition_penalty?: number
    seed?: number
    speed_factor?: number
    split_bucket?: boolean
    streaming_mode?: boolean
    temperature?: number
    text?: string
    text_lang?: string
    text_split_method?: string
    top_k?: number
    top_p?: number
    type?: number
  }

  type UpdateAudio = {
    id?: number
    isShare?: number
    title?: string
  }

  type UpdateVideo = {
    id?: number
    title?: string
  }

  type uploadAudioUsingPOST1Params = {
    biz?: string
    isShare?: number
    title?: string
  }

  type uploadAudioUsingPOSTParams = {
    biz?: string
    isShare?: number
    title?: string
  }

  type uploadFilePathUsingPOSTParams = {
    /** path */
    path?: string
  }

  type uploadFileUsingPOSTParams = {
    biz?: string
    isShare?: number
    title?: string
  }

  type uploadUsingPOST1Params = {
    biz?: string
    isShare?: number
    title?: string
  }

  type uploadUsingPOSTParams = {
    biz?: string
    isShare?: number
    title?: string
  }

  type User = {
    createTime?: string
    id?: number
    isDelete?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userPassword?: string
    userProfile?: string
    userRole?: string
  }

  type UserAddRequest = {
    userAccount?: string
    userAvatar?: string
    userName?: string
    userRole?: string
  }

  type UserLoginRequest = {
    type?: number
    userAccount?: string
    userPassword?: string
  }

  type UserQueryRequest = {
    current?: number
    id?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    checkPassword?: string
    type?: number
    userAccount?: string
    userPassword?: string
  }

  type UserUpdateMyRequest = {
    userAvatar?: string
    userName?: string
    userProfile?: string
  }

  type UserUpdateRequest = {
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    createTime?: string
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type Video = {
    audioId?: number
    createTime?: string
    duration?: string
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    isDelete?: number
    name?: string
    spaceId?: number
    updateTime?: string
    userId?: number
  }

  type VideoQueryRequest = {
    audioId?: number
    current?: number
    filePath?: string
    fileSize?: number
    fileType?: string
    id?: number
    name?: string
    pageSize?: number
    sortField?: string
    sortOrder?: string
    spaceId?: number
    userId?: number
  }

  type VideoRequest = {
    filePath?: string
    language?: string
    type?: number
  }
}
