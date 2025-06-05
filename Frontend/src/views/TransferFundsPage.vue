<template>
  <div class="transfer-page d-flex justify-content-center align-items-center min-vh-100">
    <div class="card p-5 shadow-lg transfer-card">
      <img
        src="../assets/img/May 14, 2025, 09_37_41 PM.png"
        alt="Banking App Logo"
        class="mx-auto d-block mb-4 logo-img"
      />

      <h3 class="text-center fw-bold mb-4">Transfer Funds</h3>

      <form @submit.prevent="transferFunds" class="needs-validation">
        <div class="mb-3">
          <label for="sourceIban" class="form-label">From Account (IBAN)</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-box-arrow-in-right"></i></span>
            <input
              v-model="sourceIban"
              id="sourceIban"
              type="text"
              class="form-control"
              placeholder="NLxxBANKxxxx..."
              required
            />
          </div>
        </div>

        <div class="mb-3">
          <label for="destinationIban" class="form-label">To Account (IBAN)</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-box-arrow-right"></i></span>
            <input
              v-model="destinationIban"
              id="destinationIban"
              type="text"
              class="form-control"
              placeholder="NLxxBANKxxxx..."
              required
            />
          </div>
        </div>

        <div class="mb-4">
          <label for="amount" class="form-label">Amount (€)</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-currency-euro"></i></span>
            <input
              v-model.number="amount"
              id="amount"
              type="number"
              class="form-control"
              placeholder="0.00"
              step="0.01"
              min="0.01"
              required
            />
          </div>
        </div>

        <button type="submit" class="btn btn-success w-100 fw-semibold shadow-sm">
          Transfer Now
        </button>

        <div v-if="successMessage" class="alert alert-success mt-4" role="alert">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="alert alert-danger mt-4" role="alert">
          {{ errorMessage }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '@/api/api'
import { useAuthStore } from '@/stores/authstore'

const sourceIban = ref('')
const destinationIban = ref('')
const amount = ref(0)
const successMessage = ref('')
const errorMessage = ref('')

// Extract user email from JWT token
function getUserEmail() {
  const authStore = useAuthStore()
  const token = authStore.token
  if (!token) return null

  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.sub // Assuming 'sub' contains the email
  } catch (e) {
    console.error('Failed to decode token:', e)
    return null
  }
}

async function transferFunds() {
  try {
    const email = getUserEmail()

    await api.post('/employee/transfer', {
      fromAccountIban: sourceIban.value,
      toAccountIban: destinationIban.value,
      amount: amount.value,
      initiatorEmail: email,
      accountType: 'CHECKING'
    })

    successMessage.value = 'Transfer completed successfully.'
    errorMessage.value = ''
    sourceIban.value = ''
    destinationIban.value = ''
    amount.value = 0
  } catch (err) {
    successMessage.value = ''
    errorMessage.value =
      err.response?.data?.message || 'Transfer failed. Please check the IBANs and balance.'
    console.error('Transfer error:', err)
  }
}
</script>

<style scoped>
.transfer-page {
  padding: 20px;
}
.transfer-card {
  width: 100%;
  max-width: 500px;
  border-radius: 1rem;
  background-color: #ffffff;
}
.logo-img {
  max-width: 120px;
}
.input-group-text {
  background-color: #e6f9f1;
  border-right: 0;
}
.form-control {
  border-left: 0;
}
</style>