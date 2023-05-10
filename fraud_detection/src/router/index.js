import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeLayout from "@/layout/HomeLayout";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    component: HomeLayout,
    redirect: '/model',
    children: [
      {
        path: "/model",
        component: () => import('@/views/ModelInfo')
      },
      {
        path: "/detect",
        component: () => import('@/views/DetectUtil')
      },
      {
        path: "/history",
        component: () => import('@/views/QueryHistory')
      },
      {
        path: "/error",
        component: () => import('@/views/ErrorCorrection')
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
