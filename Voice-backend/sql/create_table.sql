#
数据库初始化
-- 创建库
create
database if not exists voice_db;
-- 切换库
use
voice_db;

-- 文章表
-- auto-generated definition
create table Article
(
    id             bigint auto_increment comment 'id'
        primary key,
    title          varchar(256) null comment '标题',
    content        longtext                           not null comment '内容',
    summary        text null comment '摘要',
    author         varchar(256) null comment '作者',
    tags           varchar(256) null comment '标签(JSON数组)',
    coverImage     varchar(256) null comment '封面图片',
    viewsCount     bigint null comment '阅读量',
    likesCount     bigint null comment '点赞量',
    SEOTitle       varchar(256) null comment 'SEO标题',
    SEODescription varchar(256) null comment 'SEO描述',
    SEOKeywords    varchar(256) null comment 'SEO关键字',
    priority       int null comment '优先级',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    userId         bigint null comment '用户ID',
    countSize      bigint null comment '总字数',
    categoryCount  int null comment '总章节数',
    type           int null comment '1-散文  2-小说 ',
    description    varchar(256) null comment '描述',
    status         int null comment '0 - 草稿 1 - 已发布',
    category       varchar(256) null
) comment '文章表' collate = utf8mb4_unicode_ci;

-- auto-generated definition
create table audio_community
(
    id          bigint auto_increment comment '音频文件唯一ID'
        primary key,
    fileName    varchar(255)                       not null comment '音频文件原始文件名',
    filePath    varchar(255)                       not null comment '音频文件存储路径',
    fileSize    bigint                             not null comment '音频文件大小（字节）',
    fileType    varchar(50)                        not null comment '音频文件MIME类型（如audio/mp3）',
    duration    varchar(256) null comment '音频时长（秒）',
    userId      bigint null comment '上传文件的用户ID（可选）',
    title       varchar(255) null comment '音频标题（可选）',
    description text null comment '音频描述（可选）',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    picture     varchar(256) null,
    tags        varchar(1024) null comment '标签（JSON数组 ）',
    type        int null comment '类型（1: 普通话，2: 英语，3: 方言 ，4: 自定义 ）',
    playCount   int null comment '播放次数',
    likeCount   int null comment '点赞次数',
    isShare     tinyint null comment '他人是否可见  0 - 不可见  1 - 可见'
) comment '声韵社区表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on audio_community (userId) comment '用户ID索引';
-- auto-generated definition
create table audio_file
(
    id          bigint auto_increment comment '音频文件唯一ID'
        primary key,
    fileName    varchar(255)                       not null comment '音频文件原始文件名',
    filePath    varchar(255)                       not null comment '音频文件存储路径',
    fileSize    bigint                             not null comment '音频文件大小（字节）',
    fileType    varchar(50)                        not null comment '音频文件MIME类型（如audio/mp3）',
    duration    varchar(256) null comment '音频时长（秒）',
    userId      bigint null comment '上传文件的用户ID（可选）',
    title       varchar(255) null comment '音频标题（可选）',
    description text null comment '音频描述（可选）',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    picture     varchar(256) null,
    tags        varchar(1024) null comment '标签（JSON数组 ）',
    type        int null comment '类型（1: 普通话，2: 英语，3: 方言 ，4: 自定义 ）'
) comment '音频文件存储表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on audio_file (userId) comment '用户ID索引';

-- auto-generated definition
create table AuditoryPPT
(
    id         bigint auto_increment comment 'id'
        primary key,
    name       varchar(256) null comment '文件名',
    filePath   varchar(256) null comment '存储地址',
    fileSize   bigint null comment '文件大小（字节）',
    fileType   varchar(50) null comment '文件类型',
    duration   varchar(256) null comment '时长',
    userId     bigint null comment '用户ID',
    spaceId    bigint null comment '个人中心ID',
    audioId    bigint null comment '音色ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '有声PPT' collate = utf8mb4_unicode_ci;

create index idx_audio_id
    on AuditoryPPT (audioId) comment '用户ID索引';

create index idx_space_id
    on AuditoryPPT (spaceId) comment '个人中心ID索引';

create index idx_user_id
    on AuditoryPPT (userId) comment '用户ID索引';

-- auto-generated definition
create table Bookshelf
(
    id          bigint auto_increment comment 'id'
        primary key,
    userId      bigint null comment '用户ID',
    articleId   bigint null comment '文章ID',
    collectTime datetime default CURRENT_TIMESTAMP not null comment '收藏时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    constraint Bookshelf_ibfk_1
        foreign key (articleId) references Article (id)
) comment '书架，保存用户收藏的书' collate = utf8mb4_unicode_ci;

create index articleId
    on Bookshelf (articleId);

-- auto-generated definition
create table Chapter
(
    id           bigint auto_increment comment '章节ID'
        primary key,
    articleId    bigint                             not null comment '书本ID（关联article表）',
    chapterTitle varchar(256)                       not null comment '章节标题',
    content      longtext                           not null comment '章节内容',
    chapterOrder int                                not null comment '章节顺序（从1开始）',
    wordCount    bigint   default 0                 not null comment '本章字数',
    isFree       tinyint  default 0                 not null comment '是否免费章节（0-付费，1-免费）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    constraint Chapter_ibfk_1
        foreign key (articleId) references Article (id)
            on delete cascade
) comment '书本章节表' collate = utf8mb4_unicode_ci;

create index idx_article_order
    on Chapter (articleId, chapterOrder);

-- auto-generated definition
create table like_audio
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    userId     bigint null comment '用户ID',
    audioId    bigint null comment '音频ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '音频点赞表' collate = utf8mb4_unicode_ci;

create index idx_audio_id
    on like_audio (audioId) comment '音频ID索引';

create index idx_user_id
    on like_audio (userId) comment '用户ID索引';

-- auto-generated definition
create table play_audio
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    userId     bigint null comment '用户ID',
    audioId    bigint null comment '音频ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '音频播放次数表' collate = utf8mb4_unicode_ci;

create index idx_audio_id
    on play_audio (audioId) comment '音频ID索引';

create index idx_user_id
    on play_audio (userId) comment '用户ID索引';

-- auto-generated definition
create table space
(
    id         bigint auto_increment comment 'id'
        primary key,
    name       varchar(256) null comment '个人空间名',
    videoCount bigint null comment '视频数量',
    pptCount   bigint null comment '有声PPT数量',
    userId     bigint null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '有声PPT' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on space (userId) comment '用户ID索引';

-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256) null comment '用户昵称',
    userAvatar   varchar(1024) null comment '用户头像',
    userProfile  varchar(512) null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- auto-generated definition
create table video
(
    id         bigint auto_increment comment 'id'
        primary key,
    name       varchar(256) null comment '文件名',
    filePath   varchar(256) null comment '存储地址',
    fileSize   bigint null comment '文件大小（字节）',
    fileType   varchar(50) null comment '文件类型',
    userId     bigint null comment '用户ID',
    spaceId    bigint null comment '个人中心ID',
    audioId    bigint null comment '音色ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    duration   varchar(256) null
) comment '视频' collate = utf8mb4_unicode_ci;

create index idx_audio_id
    on video (audioId) comment '用户ID索引';

create index idx_space_id
    on video (spaceId) comment '个人中心ID索引';

create index idx_user_id
    on video (userId) comment '用户ID索引';





