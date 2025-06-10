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
            <th class="text-center">Actions</th>
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
                class="btn btn-sm btn-danger me-2"
                :disabled="account.status === 'BLOCKED' || loadingIbans.includes(account.iban)"
                @click="closeAccount(account.iban)"
              >
                {{ loadingIbans.includes(account.iban) ? 'Closing...' : 'Close' }}
              </button>
              <button
                class="btn btn-sm btn-primary"
                @click="openLimitModal(account)"
                data-bs-toggle="modal"
                data-bs-target="#approveModal"
              >
                Change Limit
              </button>
            </td>
          </tr>
        </tbody>
      </table>



    <!-- Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Change Transfer Limits</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label for="dailyLimit" class="form-label">Daily Transfer Limit (€)</label>
              <input
                v-model.number="form.dailyLimit"
                type="number"
                class="form-control"
                id="dailyLimit"
                min="0"
              />
            </div>
            <div class="mb-3">
              <label for="absoluteLimit" class="form-label">Absolute Transfer Limit (€)</label>
              <input
                v-model.number="form.absoluteLimit"
                type="number"
                class="form-control"
                id="absoluteLimit"
                min="0"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button class="btn btn-success" @click="submitLimitChange">Save</button>
          </div>
          <div v-if="modalMessage" :class="['alert', modalMessageColor, 'm-3']">{{ modalMessage }}</div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/api/api';

const accounts = ref([]);
const error = ref(null);
const loadingIbans = ref([]);
const form = ref({
  iban: '',
  dailyLimit: 0,
  absoluteLimit: 0
});
const modalMessage = ref('');
const modalMessageColor = ref('');

const fetchAccounts = async () => {
  try {
    const response = await api.get('employee/account/all');
    accounts.value = response.data;
  } catch (err) {
    error.value = 'Failed to load accounts.';
    console.error(err);
  }
};

const closeAccount = async (iban) => {
  try {
    loadingIbans.value.push(iban);
    await api.put(`/employee/account/close/${iban}`);
    const acc = accounts.value.find(a => a.iban === iban);
    if (acc) acc.status = 'BLOCKED';
  } catch (err) {
    error.value = `Failed to close account ${iban}`;
    console.error(err);
  } finally {
    loadingIbans.value = loadingIbans.value.filter(i => i !== iban);
  }
}

onMounted(fetchAccounts)


const openLimitModal = (account) => {
  form.value.iban = account.iban;
  form.value.dailyLimit = account.dailyTransferLimit;
  form.value.absoluteLimit = account.absoluteTransferLimit;
  modalMessage.value = '';
};

const submitLimitChange = async () => {
  try {
    await api.post("/employee/change-limit", {
      iban: form.value.iban,
      dailyLimit: parseFloat(form.value.dailyLimit),
      absoluteLimit: parseFloat(form.value.absoluteLimit)
    });
    modalMessage.value = "Limit changed successfully";
    modalMessageColor.value = "alert-success";
    // Optionally, update frontend state
    const acc = accounts.value.find(a => a.iban === form.value.iban);
    if (acc) {
      acc.dailyTransferLimit = form.value.dailyLimit;
      acc.absoluteTransferLimit = form.value.absoluteLimit;
    }
  } catch (error) {
    modalMessage.value = error.response?.data?.message || "Failed to change limit.";
    modalMessageColor.value = "alert-danger";
  }
};

onMounted(fetchAccounts);
</script>

<style scoped>

</style>