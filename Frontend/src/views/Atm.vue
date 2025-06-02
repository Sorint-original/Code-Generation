<script setup>
  import { ref } from 'vue'

  const showButtons = ref(true)
  const amount = ref('')

  function withdraw() {
    showButtons.value = false
  }

  async function confirmWithdraw() {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('User not authenticated')
      return
    }

    const response = await fetch('/atm/withdraw', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        token: token,
        amount: parseFloat(amount.value)
      })
    })

    const result = await response.text()
    alert(result)

    // Reset UI
    amount.value = ''
    showButtons.value = true
  }
</script>

<template>
  <div>
    <img class="login-logo mx-auto d-block mb-4 w-25" src="../assets/img/May 14, 2025, 09_37_41 PM.png" alt="Banking App Logo">
  </div>
  <div class="screen d-flex justify-content-between" style="margin-top: 150px;">
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
    </template>

    <template v-else>
      <div class="mx-auto" style="width: 300px;">
        <label for="amount" class="form-label" style="font-size:1.5rem;">Enter amount to withdraw:</label>
        <input
          id="amount"
          v-model="amount"
          type="number"
          class="form-control form-control-lg"
          placeholder="Amount"
        />
        <button
          class="btn btn-success mt-3 w-100"
          style="font-size:1.5rem;"
          @click="confirmWithdraw"
        >
          Confirm
        </button>
      </div>
    </template>
  </div>
</template>


<style>
html, body, #app {
  height: 100%;
  margin: 0;
  overflow: hidden;
  background-color: #09C7FB;
}

.screen {
  padding: 2rem;
}
</style> 