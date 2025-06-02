<template>
  <div class="container-fluid min-vh-100 bg-light">
    <h3>User Transacctions</h3>
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
        <tr v-for="transaction in transactions" :key="transaction.id">
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
         loading: false,
         errorMessage: '',
         successMessage: ''
       };
     },
     methods: {
       async fetchUserTransactions() {
         this.loading = true;
         this.errorMessage = '';
         try {
           const response = await api.get('/transactionHistory/fetchAllUserTransactions');
           this.transactions = response.data;
         } catch (err) {
           this.errorMessage = 'Failed to load transactions.';
         } finally {
           this.loading = false;
         }
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
     mounted() {
       this.fetchUserTransactions();
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