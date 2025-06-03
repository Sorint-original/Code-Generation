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
          <!-- Add more nav items here later -->
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
          <h3>Unapproved Customers</h3>

          <div v-if="loading" class="alert alert-info">Loading customers...</div>
          <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
          <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

          <table v-if="customers.length" class="table table-hover mt-3">
            <thead class="table-light">
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>BSN</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="customer in customers" :key="customer.id">
                <td>{{ customer.firstName }} {{ customer.lastName }}</td>
                <td>{{ customer.email }}</td>
                <td>{{ customer.phoneNumber }}</td>
                <td>{{ customer.bsnNumber }}</td>
                <td>
                  <button class="btn btn-success btn-sm" @click="approveCustomer(customer.id)">Approve</button>
                  <button class="btn btn-danger btn-sm" @click="unapproveCustomer(customer.id)">Decline</button>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="!loading && customers.length === 0">No unapproved customers.</p>
        </div>

        <div v-if="activePage === 'transactionhistory'">
          <h3>Transaction History</h3>
          <!-- Filter Section -->
          <div class="card mb-4">
            <div class="card-body">
              <h5 class="card-title">Filter Options</h5>
              <div class="row g-3">
                <!-- Date Range Filter -->
                <div class="col-md-4">
                  <label class="form-label">Start Date</label>
                  <input type="date" class="form-control" v-model="filters.startDate">
                </div>
                <div class="col-md-4">
                  <label class="form-label">End Date</label>
                  <input type="date" class="form-control" v-model="filters.endDate">
                </div>

                <!-- Amount Filter -->
                <div class="col-md-4">
                  <label class="form-label">Amount</label>
                  <div class="input-group">
                    <select class="form-select" v-model="filters.amountOperator">
                      <option value=">">Greater than</option>
                      <option value="<">Less than</option>
                      <option value="=">Equal to</option>
                    </select>
                    <input type="number" step="0.01" class="form-control" v-model="filters.amountValue" placeholder="Amount">
                  </div>
                </div>

                <!-- IBAN Filter -->
                <div class="col-md-6">
                  <label class="form-label">IBAN (From/To Account)</label>
                  <input type="text" class="form-control" v-model="filters.iban" placeholder="Enter IBAN">
                </div>

                <!-- Filter Button -->
                <div class="col-md-6 d-flex align-items-end">
                  <button class="btn btn-primary me-2" @click="applyFilters">
                    <i class="bi bi-funnel-fill me-1"></i> Apply Filters
                  </button>
                  <button class="btn btn-outline-secondary" @click="resetFilters">
                    <i class="bi bi-arrow-counterclockwise me-1"></i> Reset
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="loading" class="alert alert-info">Loading transactions...</div>
          <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
          <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

          <table v-if="transactions.length" class="table table-hover mt-3">
            <thead class="table-light">
            <tr>
              <th>Timestamp</th>
              <th>From Account</th>
              <th>To Account</th>
              <th>Amount</th>
              <th>Initiated By</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="transaction in filteredTransactions" :key="transaction.id">
              <td>{{ formatDate(transaction.date) }}</td>
              <td>{{ transaction.fromAccount.iban }}</td>
              <td>{{ transaction.toAccount.iban }}</td>
              <td :class="{'text-danger': transaction.amount < 0, 'text-success': transaction.amount > 0}">
                {{ formatCurrency(transaction.amount) }}
              </td>
              <td>
                {{ transaction.initiatingUser.userName }}
                <span class="badge" :class="transaction.initiatingUser.role === 'EMPLOYEE' ? 'bg-info' : 'bg-secondary'">
              {{ transaction.initiatingUser.role }}
            </span>
              </td>
            </tr>
            </tbody>
          </table>
          <p v-if="!loading && transactions.length === 0">No transactions found.</p>

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
import api from '@/api/api';
import AllAccounts from '@/views/AllAccountsPage.vue';
import TransferFunds from '@/views/TransferFundsPage.vue';

export default {
  name: 'EmployeeDashboard',
  components: {
    AllAccounts,
    TransferFunds
  },
  data() {
    return {
      activePage: 'unapproved',
      customers: [],
      transactions: [],
      filteredTransactions: [],
      loading: false,
      errorMessage: '',
      successMessage: '',
      filters: {
        startDate: null,
        endDate: null,
        amountOperator: '>',
        amountValue: null,
        iban: null
      }
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
