<template>
  <div class="py-2">
    <h3>Unapproved Customers</h3>

    <div v-if="loading" class="alert alert-info">Loading customers...</div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <table v-if="customers.length" class="table table-hover mt-3">
      <thead class="table-light">
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Phone</th>
          <th>BSN</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="customer in customers" :key="customer.id">
          <td>{{ customer.firstName }} {{ customer.lastName }}</td>
          <td>{{ customer.email }}</td>
          <td>{{ customer.phoneNumber }}</td>
          <td>{{ customer.bsnNumber }}</td>
          <td>
            <button class="btn btn-success btn-sm" @click="openApprovalModal(customer)">Approve</button>
            <button class="btn btn-danger btn-sm" @click="unapproveCustomer(customer.id)">Decline</button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="!loading && customers.length === 0">No unapproved customers.</p>

    <!-- ✅ Approval Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Set Limits for Approval</h5>
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
            <button class="btn btn-success" @click="submitApproval">Approve</button>
          </div>
              <div v-if="modalErrorMessage" class="alert alert-danger">{{ modalErrorMessage }}</div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';

export default {
  name: 'ApproveCustomers',
  data() {
    return {
      customers: [],
      loading: false,
      errorMessage: '',
      successMessage: '',
      modalErrorMessage: '',
      form: {
        selectedCustomerId: null,
        dailyLimit: null,
        absoluteLimit: null
      }
    };
  },
  methods: {
    async fetchUnapprovedCustomers() {
      this.loading = true;
      this.errorMessage = '';
      try {
        const response = await api.get('/employee/unapproved-customers');
        this.customers = response.data;
      } catch (error) {
        if (error.response?.data?.message) {
          this.errorMessage = error.response.data.message;
        } else {
          this.errorMessage = 'Showing unapproved customers failed. Please try again.';
        }
      } finally {
        this.loading = false;
      }
    },

    openApprovalModal(customer) {
      this.form.selectedCustomerId = customer.id;
      this.form.dailyLimit = null;
      this.form.absoluteLimit = null;

      const modal = new bootstrap.Modal(document.getElementById('approveModal'));
      modal.show();
    },

    async submitApproval() {
      const { selectedCustomerId, dailyLimit, absoluteLimit } = this.form;

      if (dailyLimit == null || absoluteLimit == null) {
        this.modalErrorMessage = 'Please enter both daily and absolute limits.';
        return;
      }

      try {
        await api.post(`/employee/customers/approve`, {
          customerId: selectedCustomerId,
          dailyTransferLimit: dailyLimit,
          absoluteTransferLimit: absoluteLimit
        });

        this.successMessage = 'Customer approved.';
        this.modalErrorMessage = '';
        this.errorMessage = '';
        this.customers = this.customers.filter(c => c.id !== selectedCustomerId);
        this.form = { selectedCustomerId: null, dailyLimit: null, absoluteLimit: null };

        bootstrap.Modal.getInstance(document.getElementById('approveModal')).hide();
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Approval failed. Please try again.';
        this.modalErrorMessage = this.errorMessage;
      }
    },

    async unapproveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/decline`);
        this.successMessage = 'Customer declined.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (err) {
        this.errorMessage = 'Failed to decline customer.';
      }
    }
  },
  mounted() {
    this.fetchUnapprovedCustomers();
  }
};
</script>

<style scoped>
.table th,
.table td {
  vertical-align: middle;
}
</style>
