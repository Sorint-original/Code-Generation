<script setup>
  import { ref } from 'vue'
  import api from '@/api/api';


  const showButtons = ref(true)
  const withDrawButton = ref(false)
  const depositButton = ref(false)
  const amount = ref('')


  function withdraw() {
    showButtons.value = false;
    withDrawButton.value = true;
  }

  function deposit(){
    showButtons.value = false;
    depositButton.value = true;
  }

  async function confirm() {
      const payload = {
    amount: amount.value
  };

  try {
    let response;

    if (withDrawButton.value) {
      response = await api.post('/atm/withdraw', payload);
    } else if (depositButton.value) {
      response = await api.post('/atm/deposit', payload);
    } else {
      throw new Error("No transaction type selected.");
    }

    alert(response.data);
  } catch (err) {
    alert(err.response?.data || 'Transaction failed. Please try again.');
  }

  // Reset UI
  amount.value = '';
  showButtons.value = true;
  withDrawButton.value = false;
  depositButton.value = false;
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