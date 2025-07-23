/**
 * 导航菜单配置
 */
export interface MenuItem {
  name: string // 菜单名称
  path: string // 路由路径
  icon: string // 图标类名
  children?: MenuItem[] // 子菜单
}

/**
 * 导航菜单项
 */
export const menuItems: MenuItem[] = [
  { name: '首页', path: '/home', icon: 'icon-Home' },
  { name: '书库概览', path: '/article/library', icon: 'icon-ArticleLibrary' },
  { name: '声韵社区', path: '/sound/community', icon: 'icon-SoundsCommunity' },
  {
    name: '智能工坊',
    path: '',
    icon: 'icon-AIWorkshop',
    children: [
      { name: 'PPT解析', path: '/ppt-to-audio', icon: 'icon-PPTVideo' },
      { name: '新书入库', path: '/add/article', icon: 'icon-BookAdd' },
      { name: '声像重塑', path: '/video/sound-replace', icon: 'icon-VideoReset' },
    ]
  },
  { name: '个人空间', path: '/person/center', icon: 'icon-PersonCenter' },
  {
    name: '管理枢纽',
    path: '',
    icon: 'icon-admin',
    children: [
      { name: '有声管理', path: '/admin/ppt', icon: 'icon-PPTManage' },
      { name: '书库管理', path: '/admin/book', icon: 'icon-BookManage' },
      { name: '重塑视频管理', path: '/admin/video', icon: 'icon-VideoManage' },
    ]
  },
]
