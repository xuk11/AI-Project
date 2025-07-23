import { createRouter, createWebHistory } from 'vue-router'
import PPTView from '../pages/PPT-To-Video.vue'
import example from '@/pages/Home.vue'
import ArticleDetail from '@/pages/Article/ArticleDetail.vue'
import ReadArticle from '@/pages/ReadArticle.vue'
import login from '@/pages/user/login.vue'
import register from '@/pages/user/register.vue'
import ArticleLibrary from '@/pages/Article/ArticleLibrary.vue'
import AddArticlePage from '@/pages/Article/AddArticlePage.vue'
import UpdateArticlePage from '@/pages/Article/UpdateArticlePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: example,
    },
    {
      path: '/article/detail/:id',
      name: 'article-detail',
      component: ArticleDetail,
      props: true,
      meta: {
        hideInNav: true,
        noActiveMenu: true,
      },
    },
    {
      path: '/article/library',
      name: 'articleLibrary',
      component: ArticleLibrary,
    },
    {
      path: '/read/:id/:type',
      name: 'article-read',
      component: ReadArticle,
      props: true,
      meta: {
        hideInNav: true,
        noActiveMenu: true,
      },
    },
    {
      path: '/add/article',
      name: 'addArticle',
      component: AddArticlePage,
    },
    {
      path: '/add/chapter',
      name: 'addChapter',
      component: () => import('../pages/admin/AddChapterPage.vue'),
    },
    {
      path: '/update/article/:id',
      name: 'updateArticle',
      component: UpdateArticlePage,
      props: true,
      meta: {
        hideInNav: true,
        noActiveMenu: true,
      },
    },
    {
      path: '/ppt-to-audio',
      name: 'ppt-to-audio',
      component: PPTView,
    },
    {
      path: '/person/center',
      name: '个人中心',
      component: () => import('../pages/PersonCenter.vue'),
    },
    {
      path: '/user/login',
      name: 'login',
      component: login,
    },
    {
      path: '/user/register',
      name: 'register',
      component: register,
    },
    {
      path: '/admin/video',
      name: '视频管理',
      component: () => import('../pages/admin/VideoManagePage.vue'),
    },
    {
      path: '/admin/ppt',
      name: 'PPT管理',
      component: () => import('../pages/admin/PPTManagePage.vue'),
    },
    {
      path: '/admin/book',
      name: '书籍管理',
      component: () => import('../pages/admin/BookManagePage.vue'),
    },
    {
      path: '/sound/community',
      name: 'SoundCommunity',
      component: () => import('@/pages/VoiceLibrary.vue')
    },
    {
      path: '/video/sound-replace',
      name: 'VideoSoundReplace',
      component: () => import('@/pages/Video-Reset.vue')
    },
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/add/chapter',
      name: '微调',
      component: () => import('../pages/admin/AddChapterPage.vue'),
    },
  ],
})

export default router
