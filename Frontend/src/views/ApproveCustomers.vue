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
            <button class="btn btn-success btn-sm" @click="approveCustomer(customer.id)">Approve</button>
            <button class="btn btn-danger btn-sm" @click="unapproveCustomer(customer.id)">Decline</button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="!loading && customers.length === 0">No unapproved customers.</p>
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
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage=
            error.response.data.message || " Showing Unapproved customers failed. Please try again.";
        } else {
          this.errorMessages = "Network error. Please check your connection.";
            }
      } finally {
        this.loading = false;
      }
    },
    async approveCustomer(id) {
      try {
        await api.post(`/employee/customers/${id}/approve`);
        this.successMessage = 'Customer approved.';
        this.customers = this.customers.filter(c => c.id !== id);
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage=
            error.response.data.message || "Approving failed. Please try again.";
        } else {
          this.errorMessages = "Network error. Please check your connection.";
            }
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
