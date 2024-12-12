import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/user/login'
  },
  {
    path: '/user/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/user/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/user/forget',
    name: 'forget',
    component: () => import('../views/ForgetView.vue')
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('../views/SearchView.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/ProfileView.vue')
  },
  {
    path: '/collection',
    name: 'collection',
    component: () => import('../views/CollectionView.vue')
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes, // 等同于 routes: routes
});

export default router;
