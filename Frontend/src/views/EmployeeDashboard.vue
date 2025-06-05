<template>
  <div class="container-fluid min-vh-100 bg-light">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-3 bg-white shadow-sm p-3">
        <h4 class="mb-4">Employee Dashboard</h4>
        <ul class="nav flex-column nav-pills">
          <li class="nav-item mb-2">
            <button class="btn w-100" :class="{ 'btn-primary': activePage === 'unapproved' }" @click="activePage = 'unapproved'">
              Approve Customers
            </button>
          </li>
          <li class="nav-item mb-2">
            <router-link to="/account/all" class="btn w-100 btn-primary">
              View All Accounts
            </router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link to="/account" class="btn w-100 btn-primary">
              My Accounts
            </router-link>
          </li>
          <li class="nav-item mb-2">
            <button class="btn w-100" :class="{ 'btn-primary': activePage === 'transactionhistory' }" @click="activePage = 'transactionhistory'">
              Transactions
            </button>
          </li>
        </ul>
      </div>

      <!-- Main Content -->
      <div class="col-md-9 p-4">
        <div v-if="activePage === 'unapproved'">
          <ApproveCustomers />
        </div>

        <div v-else-if="activePage === 'transactionhistory'">
          <TransactionList :transactionSource="'AllTransactions'" />
        </div>

        <div v-else>
          <p>Select a section from the sidebar.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TransactionList from '../views/TransactionsList.vue';
import ApproveCustomers from './ApproveCustomers.vue';

export default {
  name: 'EmployeeDashboard',
  components: {
    TransactionList,
    ApproveCustomers
  },
  data() {
    return {
      activePage: 'unapproved'
    };
  }
};
</script>

<style scoped>
.container-fluid {
  background-color: #f8f9fa;
}
.nav .btn {
  text-align: left;
}
.nav .btn:hover {
  background-color: #e2e6ea;
}
</style>
