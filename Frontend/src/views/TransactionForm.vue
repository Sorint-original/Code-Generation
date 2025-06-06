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

        <div class="mb-3">
          <label class="form-label">Recipient First Name</label>
          <input v-model="recipientFirstName" type="text" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Recipient Last Name</label>
          <input v-model="recipientLastName" type="text" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Matching Recipient Accounts</label>
          <div v-if="filteredResults.length">
            <div 
              v-for="iban in filteredResults" 
              :key="iban.iban"
              class="selectable-box border p-2 mb-2 rounded"
              :class="{ 'selected': toIban === iban.iban }"
              @click="selectIban(iban.iban)">
              {{ iban.type }} - {{ iban.iban }}
            </div>
          </div>
          <p v-else class="text-muted">No results to show</p>
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
      recipientFirstName: '',
      recipientLastName: '',
      recipientIbans: [],
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
    },
    filteredResults() {
      const first = this.recipientFirstName.replace(/[^a-zA-Z]/g, '').toLowerCase();
      const last = this.recipientLastName.replace(/[^a-zA-Z]/g, '').toLowerCase();
      if ((first.length >= 2 || last.length >= 2) && this.recipientIbans.length > 0) {
        return this.recipientIbans;
      }
      return [];
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
        const res = await api.get('/api/account/info');
        res.data.accounts.forEach(account => {
          this.userIbans[account.accountType] = account.iban;
        });
      } catch (err) {
        this.errorMessage = 'Failed to load your IBANs';
      }
    },
    updateFromIban() {
      this.fromIban = this.userIbans[this.selectedAccountType];
    },
    async fetchRecipientIbans() {
      try {
        const res = await api.post('/customer/search-ibans', {
          firstName: this.recipientFirstName,
          lastName: this.recipientLastName
        });
        this.recipientIbans = res.data;
      } catch (err) {
        this.recipientIbans = [];
      }
    },
    selectIban(iban) {
      this.toIban = iban;
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
        this.toIban = '';
        this.amount = null;
        this.recipientIbans = [];
        this.recipientFirstName = '';
        this.recipientLastName = '';
      } catch (err) {
        this.errorMessage = err.response?.data || 'Transfer failed. Please try again.';
      }
    }
  },
  watch: {
    recipientFirstName() {
      this.triggerSearch();
    },
    recipientLastName() {
      this.triggerSearch();
    }
  }
};
</script>

<style scoped>
.transfer-page {
  background: linear-gradient(to right, #93FB9D, #09C7FB);
}
.selectable-box {
  cursor: pointer;
  transition: 0.2s;
}
.selectable-box:hover,
.selected {
  background-color: #d0f0ff;
}
</style>
