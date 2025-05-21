<template>
  <div class="container-fluid min-vh-100 bg-light">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-3 bg-white shadow-sm p-3">
        <h4 class="mb-4">Employee Dashboard</h4>
        <ul class="nav flex-column nav-pills">
          <li class="nav-item mb-2">
            <button class="btn w-100" :class="{ 'btn-primary': activePage === 'unapproved' }" @click="activePage = 'unapproved'">
              Approve Customers
            </button>
          </li>
          <!-- Add more nav items here later -->
        </ul>
      </div>

      <!-- Main Content -->
      <div class="col-md-9 p-4">
        <div v-if="activePage === 'unapproved'">
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
                  <button class="btn btn-success btn-sm" @click="approveCustomer(customer.id)">Approve</button>
                  <button class="btn btn-danger btn-sm" @click="unapproveCustomer(customer.id)">Decline</button>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="!loading && customers.length === 0">No unapproved customers.</p>
        </div>

        <!-- Placeholder for future pages -->
        <div v-else>
          <p>Select a section from the sidebar.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';

export default {
  name: 'EmployeeDashboard',
  data() {
    return {
      activePage: 'unapproved',
      customers: [],
      loading: false,
      errorMessage: '',
      successMessage: ''
    };
  },
  methods: {
    async fetchUnapprovedCustomers() {
      this.loading = true;
      this.errorMessage = '';
      try {
        const response = await api.get('/employee/unapproved-customers');
        this.customers = response.data;
      } catch (err) {
        this.errorMessage = 'Failed to load customers.';
      } finally {
        this.loading = false;
      }
    },
    async approveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/approve`);
        this.successMessage = 'Customer approved.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (err) {
        this.errorMessage = 'Failed to approve customer.';
      }
    },

    async unapproveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/decline`);
        this.successMessage = 'Customer unapproved.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (err) {
        this.errorMessage = 'Failed to unapprove customer.';
      }
    }

  },
  mounted() {
    this.fetchUnapprovedCustomers();
  }
};
</script>

<style scoped>
.container-fluid {
  background-color: #f8f9fa;
}
.nav .btn {
  text-align: left;
}
.nav .btn:hover {
  background-color: #e2e6ea;
}
</style>
