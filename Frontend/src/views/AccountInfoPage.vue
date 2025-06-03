<template>
  <div class="account-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 800px; max-height: 90vh; overflow-y: auto;">
      <h2 class="text-center fw-bold mb-4">Account Information</h2>

      <!-- Customer Details -->
      <div v-if="accountInfo" class="mb-4">
        <h5 class="fw-semibold">Customer Information</h5>
        <div class="row">
          <div class="col-md-6">
            <p><strong>First Name:</strong> {{ accountInfo.firstName }}</p>
            <p><strong>Last Name:</strong> {{ accountInfo.lastName }}</p>
            <p><strong>Username:</strong> {{ accountInfo.username }}</p>
          </div>
          <div class="col-md-6">
            <p><strong>Email:</strong> {{ accountInfo.email }}</p>
            <p><strong>Phone:</strong> {{ accountInfo.phone }}</p>
          </div>
        </div>
      </div>

      <!-- Accounts Table -->
      <div v-if="accountInfo && accountInfo.bankAccounts.length">
        <h5 class="fw-semibold">Bank Accounts</h5>
        <table class="table table-striped">
          <thead class="table-light">
            <tr>
              <th>IBAN</th>
              <th>Type</th>
              <th>Status</th>
              <th class="text-end">Balance</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="account in accountInfo.bankAccounts" :key="account.iban">
              <td>{{ account.iban }}</td>
              <td>{{ account.type }}</td>
              <td>{{ account.status }}</td>
              <td class="text-end">€{{ account.amount.toFixed(2) }}</td>
            </tr>
          </tbody>
        </table>

        <div class="text-end fw-bold fs-5">
          Total Balance: €{{ totalBalance.toFixed(2) }}
        </div>
      </div>

      <!-- Loading or Error -->
      <div v-if="loading" class="text-center mt-4">Loading...</div>
      <div v-if="error" class="text-danger mt-3">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api/api' // your axios instance
import { useAuthStore } from '@/stores/authstore' // assumes you store userId here

const accountInfo = ref(null)
const loading = ref(true)
const error = ref(null)

const auth = useAuthStore()
const userId = auth.userId // or however you store logged-in user ID

const totalBalance = computed(() => {
  return accountInfo.value?.bankAccounts.reduce((sum, acc) => sum + acc.amount, 0) || 0
})

onMounted(async () => {
  try {
    const response = await api.get(`/account/info`)
    accountInfo.value = response.data
  } catch (err) {
    error.value = 'Failed to load account information.'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.account-page {
  background: linear-gradient(to right, #93FB9D, #09C7FB);
}
</style>