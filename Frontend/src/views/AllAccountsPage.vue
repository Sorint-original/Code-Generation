<template>
  <div class="employee-page d-flex justify-content-center vh-100">
    <div class="card p-4 w-100 mx-5" style="max-width: 1200px; max-height: 90vh; overflow-y: auto;">
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/api' // your axios instance

const accounts = ref([])
const error = ref(null)
const loadingIbans = ref([])

const fetchAccounts = async () => {
  try {
    const response = await api.get('employee/account/all')
    accounts.value = response.data
  } catch (err) {
    error.value = 'Failed to load accounts.'
    console.error(err)
  }
}

const closeAccount = async (iban) => {
  try {
    loadingIbans.value.push(iban)
    await api.put(`/employee/account/close/${iban}`)

    // update local status
    const acc = accounts.value.find(a => a.iban === iban)
    if (acc) acc.status = 'BLOCKED'
  } catch (err) {
    error.value = `Failed to close account ${iban}`
    console.error(err)
  } finally {
    loadingIbans.value = loadingIbans.value.filter(i => i !== iban)
  }
}

onMounted(fetchAccounts)
</script>

<style scoped>

</style>