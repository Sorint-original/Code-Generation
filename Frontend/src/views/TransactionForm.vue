<template>
  <div class="transfer-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 500px;">
      <h3 class="text-center mb-4">Transfer Funds</h3>

      <form @submit.prevent="submitTransfer">
        <div v-if="isCustomer">
          <label class="form-label">Select Account Type</label>
          <select v-model="selectedAccountType" class="form-select mb-3" @change="updateFromIban">
            <option disabled value="">-- Select Account --</option>
            <option value="CHECKING">Checking</option>
            <option value="SAVINGS">Savings</option>
          </select>

          <div class="mb-3">
            <label class="form-label">From IBAN</label>
            <input type="text" class="form-control" :value="fromIban" disabled />
          </div>
        </div>

        <div v-else>
          <div class="mb-3">
            <label class="form-label">From IBAN</label>
            <input v-model="fromIban" type="text" class="form-control" required />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">To IBAN</label>
          <input v-model="toIban" type="text" class="form-control" required />
        </div>

        <div class="mb-3">
          <label class="form-label">Amount (&euro;)</label>
          <input v-model.number="amount" type="number" min="0.01" step="0.01" class="form-control" required />
        </div>

        <button type="submit" class="btn w-100 text-white" style="background: linear-gradient(to right, #93FB9D, #09C7FB);">
          Submit Transfer
        </button>

        <p v-if="errorMessage" class="text-danger mt-2 text-center">{{ errorMessage }}</p>
        <p v-if="successMessage" class="text-success mt-2 text-center">{{ successMessage }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import { useAuthStore } from '@/stores/authstore';

export default {
  name: 'TransactionForm',
  data() {
    return {
      selectedAccountType: '',
      fromIban: '',
      toIban: '',
      amount: null,
      errorMessage: '',
      successMessage: '',
      userIbans: {
        CHECKING: '',
        SAVINGS: ''
      },
    };
  },
  computed: {
    isCustomer() {
      const tokenPayload = JSON.parse(atob(useAuthStore().token.split('.')[1]));
      return tokenPayload.role === 'CUSTOMER';
    },
    userEmail() {
      const tokenPayload = JSON.parse(atob(useAuthStore().token.split('.')[1]));
      return tokenPayload.sub;
    }
  },
  mounted() {
    if (this.isCustomer) {
      this.fetchUserIbans();
    }
  },
  methods: {
    async fetchUserIbans() {
      try {
        const res = await api.get('/user/accounts');
        res.data.forEach(account => {
          this.userIbans[account.type] = account.iban;
        });
      } catch (err) {
        this.errorMessage = 'Failed to load account IBANs';
      }
    },
    updateFromIban() {
      this.fromIban = this.userIbans[this.selectedAccountType];
    },
    async submitTransfer() {
      this.errorMessage = '';
      this.successMessage = '';
      if (!this.fromIban || !this.toIban || !this.amount || this.amount <= 0) {
        this.errorMessage = 'All fields are required and amount must be greater than 0';
        return;
      }
      try {
        const payload = {
          fromAccountIban: this.fromIban,
          toAccountIban: this.toIban,
          amount: this.amount,
          initiatorEmail: this.userEmail,
          accountType: this.selectedAccountType
        };
        await api.post('/transaction', payload);
        this.successMessage = 'Transfer successful!';
        this.fromIban = '';
        this.toIban = '';
        this.amount = null;
      } catch (err) {
        this.errorMessage = err.response?.data || 'Transfer failed. Please try again.';
      }
    }
  }
};
</script>

<style scoped>
.transfer-page {
  background: linear-gradient(to right, #93FB9D, #09C7FB);
}
</style>
