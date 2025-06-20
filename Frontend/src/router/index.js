import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/authstore";

import LoginPage from "../views/LoginPage.vue";
import Atm from "../views/Atm.vue";
import AtmLogin from "@/views/AtmLogin.vue";
import RegisterPage from "../views/RegisterPage.vue";
import TestPage from "../views/Testpage.vue";
import EmployeeDashboard from "@/views/EmployeeDashboard.vue";
import UnAuthorizedPage from "@/views/UnAuthorized.vue";
import DailyLimitChangeForm from "@/views/DailyLimitChangeForm.vue";
import AccountInfoPage from "@/views/AccountInfoPage.vue";
import AllAccountsPage from "@/views/AllAccountsPage.vue";
import TransferFundsPage from "@/views/TransferFundsPage.vue";
import TransactionForm from "@/views/TransactionForm.vue";
import TransactionsList from "@/views/TransactionsList.vue";
import ApproveCustomers from "../views/ApproveCustomers.vue";
import CustomerDashboard from "@/views/CustomerDashboard.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterPage },
  { path: "/transaction", component: TransactionForm, meta: { requiresAuth: true } },
  { path: "/transaction-history", component: TransactionsList, meta: { requiresAuth: true } },
  { path: "/daily-limit", component: DailyLimitChangeForm, meta: { requiresAuth: true } },
  { path: "/atm", component: Atm,  meta: { requiresAuth: true }  },  // main ATM interface
  { path: "/atm/login", component: AtmLogin }, 
  { path: "/test", component: TestPage, meta: { requiresAuth: true } },
  {
    path: "/employee",
    name: "EmployeeDashboard",
    component: EmployeeDashboard,
    meta: { requiresAuth: true, role: "EMPLOYEE" }
  },
  { path: "/unauthorized", component: UnAuthorizedPage },
  { path: "/account", component: AccountInfoPage },
  { path: "/account/all", component: AllAccountsPage },
  { path: "/transfer", component: TransferFundsPage },
  {
    path: "/employee/approve-customers",
    name: "ApproveCustomers",
    component: ApproveCustomers
  },
  {
    path: "/customer/dashboard",
    component: CustomerDashboard,
    meta: { requiresAuth: true }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next({ path: "/login", query: { redirect: to.fullPath } });
  }

  if (to.meta.role && authStore.role !== to.meta.role) {
    return next("/unauthorized");
  }

  next();
});

export default router;
