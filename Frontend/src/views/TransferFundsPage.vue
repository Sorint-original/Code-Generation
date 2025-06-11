<template>
  <div class="transfer-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 600px;">
      <h3 class="text-center mb-4">Transfer Funds</h3>

      <form @submit.prevent="submitTransfer">

        <!-- FROM ACCOUNT SEARCH -->
        <div class="mb-3">
          <label class="form-label">Search Sender (min 2 letters)</label>
          <input v-model="fromQuery" type="text" class="form-control" placeholder="Start typing name..." />
        </div>

        <div class="search-results-box mb-3">
          <div v-if="fromQuery.length < 2" class="text-muted text-center py-2">
            No results to show
          </div>
          <table v-else class="table table-hover mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>IBAN</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="sender in senderIbans"
                :key="sender.iban"
                @click="selectSender(sender)"
                :class="{ 'table-active': sender.iban === fromIban }"
                style="cursor: pointer;"
              >
                <td>{{ sender.fullName }}</td>
                <td>{{ sender.accountType }}</td>
                <td>{{ sender.iban }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="mb-3">
          <label class="form-label">From IBAN</label>
          <input v-model="fromIban" type="text" class="form-control" readonly />
        </div>

        <!-- RECIPIENT SEARCH -->
        <div class="mb-3">
          <label class="form-label">Search Recipient (min 2 letters)</label>
          <input v-model="recipientQuery" type="text" class="form-control" placeholder="Start typing name..." />
        </div>

        <div class="search-results-box mb-3">
          <div v-if="recipientQuery.length < 2" class="text-muted text-center py-2">
            No results to show
          </div>
          <table v-else class="table table-hover mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>IBAN</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="recipient in recipientIbans"
                :key="recipient.iban"
                @click="selectRecipient(recipient)"
                :class="{ 'table-active': recipient.iban === toIban }"
                style="cursor: pointer;"
              >
                <td>{{ recipient.fullName }}</td>
                <td>{{ recipient.accountType }}</td>
                <td>{{ recipient.iban }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="mb-3">
          <label class="form-label">To IBAN</label>
          <input v-model="toIban" type="text" class="form-control" readonly />
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
      fromQuery: '',
      recipientQuery: '',
      senderIbans: [],
      recipientIbans: [],
      fromIban: '',
      toIban: '',
      amount: null,
      errorMessage: '',
      successMessage: '',
      debounceTimeout: null,
    };
  },
  computed: {
    userEmail() {
      const tokenPayload = JSON.parse(atob(useAuthStore().token.split('.')[1]));
      return tokenPayload.sub;
    }
  },
  methods: {
    async fetchSenderIbans() {
      if (this.fromQuery.length < 2) {
        this.senderIbans = [];
        return;
      }
      try {
        const res = await api.post('/customer/search', {
          query: this.fromQuery
        });
        this.senderIbans = res.data;
      } catch (err) {
        this.senderIbans = [];
        this.errorMessage = 'Error searching sender accounts.';
      }
    },
    async fetchRecipientIbans() {
      if (this.recipientQuery.length < 2) {
        this.recipientIbans = [];
        return;
      }
      try {
        const res = await api.post('/customer/search', {
          query: this.recipientQuery
        });
        this.recipientIbans = res.data;
      } catch (err) {
        this.recipientIbans = [];
        this.errorMessage = 'Error searching recipients.';
      }
    },
    selectSender(sender) {
      this.fromIban = sender.iban;
    },
    selectRecipient(recipient) {
      this.toIban = recipient.iban;
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
          accountType: 'CHECKING'
        };
        await api.post('/employee/transfer', payload);
        this.successMessage = 'Transfer successful!';
        this.fromQuery = '';
        this.recipientQuery = '';
        this.senderIbans = [];
        this.recipientIbans = [];
        this.fromIban = '';
        this.toIban = '';
        this.amount = null;
      } catch (err) {
        this.errorMessage = err.response?.data.message || 'Transfer failed. Please try again.';
      }
    }
  },
  watch: {
    fromQuery(newVal) {
      clearTimeout(this.debounceTimeout);
      this.debounceTimeout = setTimeout(() => {
        this.fetchSenderIbans();
      }, 300);
    },
    recipientQuery(newVal) {
      clearTimeout(this.debounceTimeout);
      this.debounceTimeout = setTimeout(() => {
        this.fetchRecipientIbans();
      }, 300);
    }
  }
};
</script>

<style scoped>
.search-results-box {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.search-results-box table {
  margin: 0;
}
</style>
