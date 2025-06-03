<template>
  <div class="container-fluid min-vh-100 bg-light">
    <h3>User Transactions</h3>

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
</template>

 <script>
  import api from '@/api/api';

 export default {
     name: 'TransactionHistory',
     data() {
       return {
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
       async fetchUserTransactions() {
         this.loading = true;
         this.errorMessage = '';
         try {
           const response = await api.get('/transactionHistory/fetchLoggedUserTransactions');
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
       await this.fetchUserTransactions();
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