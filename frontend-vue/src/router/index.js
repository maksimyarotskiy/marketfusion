import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import RecommendationsView from '@/views/RecommendationsView.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
  {
    path: '/recommendations',
    name: 'recommendations',
    component: RecommendationsView,
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const isAuthed = !!localStorage.getItem('accessToken')

  if (to.meta.requiresAuth && !isAuthed) {
    return { name: 'login' }
  }

  if (to.name === 'login' && isAuthed) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
