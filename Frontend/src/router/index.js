import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores//authstore";
import LoginPage from "../views/LoginPage.vue";
import RegisterPage from "../views/RegisterPage.vue";
import TransferForm from '../views/TransactionForm.vue'
import TestPage from "../views/Testpage.vue";

const routes = [
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterPage },
  { path: '/transaction', component: TransferForm, meta: {requiresAuth: true} }, 
  { path: "/test", component: TestPage, meta: { requiresAuth: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ path: '/login', query: { redirect: to.fullPath } });
  } else {
    next();
  }
});

export default router;
