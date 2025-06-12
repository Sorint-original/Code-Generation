<script setup>
import { onMounted, ref } from 'vue'
import api from '@/api/api'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authstore'

const showAccounts = ref(true)
const showButtons = ref(false)
const withDrawButton = ref(false)
const depositButton = ref(false)
const amount = ref('')
const accounts = ref([])
const selectedAccount = ref(null)
const loading = ref(false)
const successMessage = ref('')

const router = useRouter()
const authStore = useAuthStore()

function withdraw() {
  showButtons.value = false
  withDrawButton.value = true
  depositButton.value = false
}

function deposit() {
  showButtons.value = false
  depositButton.value = true
  withDrawButton.value = false
}

function selectAccount(account) {
  selectedAccount.value = account
  showAccounts.value = false
  showButtons.value = true
  withDrawButton.value = false
  depositButton.value = false
  amount.value = ''
}

function backToAccounts() {
  showAccounts.value = true
  showButtons.value = false
  withDrawButton.value = false
  depositButton.value = false
  selectedAccount.value = null
  amount.value = ''
  loading.value = false
  successMessage.value = ''
}

function backToSelection() {
  showAccounts.value = false
  showButtons.value = true
  withDrawButton.value = false
  depositButton.value = false
  amount.value = ''
  loading.value = false
  successMessage.value = ''
}

async function confirm() {
  if (!selectedAccount.value) {
    alert('No account selected')
    return
  }
  if (!amount.value || Number(amount.value) <= 0) {
    alert('Please enter a valid amount')
    return
  }
  const payload = {
    amount: Number(amount.value),
    iban: selectedAccount.value.iban
  }

  loading.value = true
  successMessage.value = ''
  try {
    let response
    if (withDrawButton.value) {
      response = await api.post('/atm/withdraw', payload)
    } else if (depositButton.value) {
      response = await api.post('/atm/deposit', payload)
    } else {
      throw new Error('No transaction type selected.')
    }
    // Show success message
    successMessage.value = response.data || 'Withdraw successful!'
  } catch (err) {
    alert(err.response?.data || 'Transaction failed. Please try again.')
  } finally {
    loading.value = false
  }
}

const fetchAccounts = async () => {
  try {
    const response = await api.get('/account/info')
    accounts.value = response.data.bankAccounts || []
  } catch (err) {
    alert('Failed to load accounts')
  }
}

onMounted(fetchAccounts)

function logout() {
  authStore.logout()
  router.push('/atm/login')
}
</script>

<template>
  <div>
    <img class="login-logo mx-auto d-block mb-4 w-25" src="../assets/img/logo_light.png" alt="Banking App Logo" />
  </div>

  <div class="text-center mb-4">
    <button class="btn btn-danger px-4 py-2" @click="logout">
      Logout
    </button>
  </div>

  <div v-if="selectedAccount && showButtons" class="text-center mb-4">
    <label class="balance-label">
      Balance: {{ selectedAccount.amount }}
    </label>
  </div>

  <template v-if="showAccounts">
    <div class="d-flex justify-content-center align-items-center" style="min-height: 60vh;">
      <div>
        <div v-for="account in accounts" :key="account.iban" class="mb-3">
          <button
            class="btn btn-primary w-100"
            style="height:70px; font-size:1.2rem; min-width: 300px;"
            @click="selectAccount(account)"
          >
            {{ account.iban }}  {{account.type}}
          </button>
        </div>
      </div>
    </div>
  </template>

  <div v-if="!showAccounts" class="screen d-flex justify-content-between" style="margin-top: 150px;">
    <template v-if="showButtons">
      <button
        class="btn btn-primary w-40"
        style="height:70px; font-size:2rem;"
        @click="withdraw"
      >
        Withdraw money
      </button>
      <button
        class="btn btn-primary w-40"
        style="height:70px; font-size:2rem;"
        @click="deposit"
      >
        Deposit money
      </button>
      <button
        class="btn btn-secondary w-40 ms-3"
        style="height:70px; font-size:1.2rem;"
        @click="backToAccounts"
      >
        Back to account selection
      </button>
    </template>

    <template v-else>
      <div class="mx-auto" style="width: 300px;">
        <template v-if="loading">
          <div class="text-center">
            <div class="spinner-border text-primary mb-3" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <div v-if="withDrawButton">Withdrawing money...</div>
            <div v-else>Depositing money...</div>
          </div>
        </template>
        <template v-else-if="successMessage">
          <div class="alert alert-success text-center" role="alert">
            {{ successMessage }}
          </div>
          <div class="d-flex flex-column gap-2">
            <button class="btn btn-secondary" @click="backToSelection">
              Go to Home Screen
            </button>
          </div>
        </template>
        <template v-else>
          <template v-if="withDrawButton">
            <label for="amount" class="form-label" style="font-size:1.5rem;">Enter amount to withdraw:</label>
          </template>
          <template v-else>
            <label for="amount" class="form-label" style="font-size:1.5rem;">Enter amount to deposit:</label>
          </template>
          <input
            id="amount"
            v-model.number="amount"
            type="number"
            class="form-control form-control-lg"
            placeholder="Amount"
          />
          <button
            class="btn btn-success mt-3 w-100"
            style="font-size:1.5rem;"
            @click="confirm"
          >
            Confirm
          </button>
          <button
            class="btn btn-secondary mt-2 w-100"
            style="font-size:1rem;"
            @click="backToSelection"
          >
            Back
          </button>
        </template>
      </div>
    </template>
  </div>
</template>

<style scoped>
html, body, #app {
  height: 100%;
  margin: 0;
  overflow: hidden;
}

.form-label {
  color: #fff;
}

.screen {
  padding: 2rem;
}

.balance-label {
  color: #fff;
  font-size: 3rem;
}
</style>