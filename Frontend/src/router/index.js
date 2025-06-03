import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/authstore";
import LoginPage from "../views/LoginPage.vue";
import Atm from "../views/Atm.vue";
import RegisterPage from "../views/RegisterPage.vue";
import TransferForm from '../views/TransactionForm.vue'
import TestPage from "../views/Testpage.vue";
import TransactionHistory from "../views/CustomerTransactions.vue";
import EmployeeDashboard from "@/views/EmployeeDashboard.vue";
import UnAuthorizedPage from "@/views/UnAuthorized.vue";
import AccountInfoPage from "@/views/AccountInfoPage.vue";
import AllAccountsPage from "@/views/AllAccountsPage.vue";
import TransferFundsPage from "@/views/TransferFundsPage.vue";

const routes = [
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterPage },
  { path: '/transaction', component: TransferForm, meta: {requiresAuth: true} },
  { path: "/transaction-history", component: TransactionHistory, meta: { requiresAuth: true } },
  {path: "/Atm", component: Atm },
  { path: "/test", component: TestPage, meta: { requiresAuth: true } },
  {
    path: '/employee',
    name: 'EmployeeDashboard',
    component: EmployeeDashboard,
    meta: { requiresAuth: true, role: 'EMPLOYEE' }
  },
    { path: "/unauthorized", component: UnAuthorizedPage },
    { path: "/account", component: AccountInfoPage },
    { path: "/account/all", component: AllAccountsPage },
    { path: "/transfer", component: TransferFundsPage },

];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next({ path: '/login', query: { redirect: to.fullPath } });
  }

  if (to.meta.role && authStore.role !== to.meta.role) {
    return next('/unauthorized'); 
  }

  next();
});


export default router;
