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
          <li class="nav-item mb-2">
            <button class="btn w-100" :class="{ 'btn-primary': activePage === 'allaccounts' }" @click="activePage = 'allaccounts'">
              All Accounts
            </button>
          </li>
          <li class="nav-item mb-2">
            <button class="btn w-100" :class="{ 'btn-primary': activePage === 'transferfunds' }" @click="activePage = 'transferfunds'">
              Transfer Funds
            </button>
          </li>
        </ul>
      </div>

      <!-- Main Content -->
      <div class="col-md-9 p-4">
        <div v-if="activePage === 'allaccounts'">
          <AllAccounts />
        </div>

        <div v-if="activePage === 'transferfunds'">
          <TransferFunds />
        </div>
        
        <div v-if="activePage === 'unapproved'">
          <ApproveCustomers />
        </div>

        <div v-else-if="activePage === 'transactionhistory'">
          <TransactionList :transactionSource="'AllTransactions'" />
        </div>

        <!-- All accounts -->
        <div v-if="activePage === 'allaccounts'">
          <div class="employee-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg w-100 mx-5" style="max-width: 1200px; max-height: 90vh; overflow-y: auto;">
      <h2 class="text-center fw-bold mb-4">All Customer Accounts</h2>

      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>User ID</th>
            <th>IBAN</th>
            <th>Type</th>
            <th>Status</th>
            <th class="text-end">Balance</th>
            <th class="text-center">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="account in accounts" :key="account.iban">
            <td>{{ account.userId }}</td>
            <td>{{ account.iban }}</td>
            <td>{{ account.type }}</td>
            <td>{{ account.status }}</td>
            <td class="text-end">€{{ account.amount.toFixed(2) }}</td>
            <td class="text-center">
              <button
                class="btn btn-sm btn-danger"
                :disabled="account.status === 'BLOCKED' || loadingIbans.includes(account.iban)"
                @click="closeAccount(account.iban)"
              >
                {{ loadingIbans.includes(account.iban) ? 'Closing...' : 'Close' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="error" class="text-danger mt-3 text-center">{{ error }}</div>
    </div>
  </div>
        </div>
        <!-- Placeholder for future pages -->
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
import api from '@/api/api';
import AllAccounts from '@/views/AllAccountsPage.vue';
import TransferFunds from '@/views/TransferFundsPage.vue';

export default {
  name: 'EmployeeDashboard',
  components: {
    TransactionList,
    ApproveCustomers,
    AllAccounts,
    TransferFunds
  },
  data() {
    return {
      activePage: 'unapproved'
    };
  },
  methods: {
    async fetchUnapprovedCustomers() {
      this.loading = true;
      this.errorMessage = '';
      try {
        const response = await api.get('/employee/unapproved-customers');
        this.customers = response.data;
      } catch (err) {
        this.errorMessage = 'Failed to load customers.';
      } finally {
        this.loading = false;
      }
    },
    async approveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/approve`);
        this.successMessage = 'Customer approved.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (err) {
        this.errorMessage = 'Failed to approve customer.';
      }
    },
    

    async unapproveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/decline`);
        this.successMessage = 'Customer unapproved.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (err) {
        this.errorMessage = 'Failed to unapprove customer.';
      }
    },

    async fetchTransactions() {
      this.loading = true;
      this.errorMessage = '';
      try {
        const response = await api.get('/employee/transaction-history');
        this.transactions = response.data;
      } catch (err) {
        this.errorMessage = 'Failed to load transactions.';
      } finally {
        this.loading = false;
      }
    },
    applyFilters() {
      this.filteredTransactions = this.transactions.filter(transaction => {
        // Date filter
        if (this.filters.startDate) {
          const startDate = new Date(this.filters.startDate);
          const transactionDate = new Date(transaction.date);
          if (transactionDate < startDate) return false;
        }

        if (this.filters.endDate) {
          const endDate = new Date(this.filters.endDate);
          const transactionDate = new Date(transaction.date);
          if (transactionDate > endDate) return false;
        }

        // Amount filter
        if (this.filters.amountValue !== null) {
          const amount = parseFloat(this.filters.amountValue);
          switch (this.filters.amountOperator) {
            case '>': if (transaction.amount <= amount) return false; break;
            case '<': if (transaction.amount >= amount) return false; break;
            case '=': if (transaction.amount !== amount) return false; break;
          }
        }

        // IBAN filter
        if (this.filters.iban) {
          const searchIban = this.filters.iban.toLowerCase();
          if (!transaction.fromAccount.iban.toLowerCase().includes(searchIban) &&
              !transaction.toAccount.iban.toLowerCase().includes(searchIban)) {
            return false;
          }
        }

        return true;
      });
    },
    resetFilters() {
      this.filters = {
        startDate: null,
        endDate: null,
        amountOperator: '>',
        amountValue: null,
        iban: null
      };
      this.filteredTransactions = [...this.transactions];
    },
    formatDate(timestamp) {
      return new Date(timestamp).toLocaleString();
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'EUR'
      }).format(amount);
    }
  },
  async mounted() {
    this.fetchUnapprovedCustomers();
    await this.fetchTransactions();
    this.applyFilters();
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
