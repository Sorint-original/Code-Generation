<template>
  <div class="dashboard-container">
    <div v-if="errorMessage" class="alert alert-danger text-center mt-5">
      {{ errorMessage }}
    </div>

    <!-- ✅ Approved Customer Dashboard -->
    <div v-else-if="statusMessage && isApproved" class="dashboard-content">
      <!-- Logout Button -->
      <div class="d-flex justify-content-end mb-3 me-3">
        <button class="btn btn-outline-danger" v-on="{ click: logout }">
          Logout
        </button>
      </div>

      <h2 class="mb-3 text-center">{{ statusMessage }}</h2>

      <!-- Navigation Tabs -->
      <ul class="nav nav-tabs justify-content-center mb-4">
        <li class="nav-item">
          <button
            class="nav-link"
            :class="{ active: activeTab === 'account' }"
            @click="activeTab = 'account'"
          >
            Account Info
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link"
            :class="{ active: activeTab === 'transactions' }"
            @click="activeTab = 'transactions'"
          >
            Transactions
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link"
            :class="{ active: activeTab === 'transfer' }"
            @click="activeTab = 'transfer'"
          >
            Transfer
          </button>
        </li>
      </ul>

      <div class="tab-content">
        <AccountInfo v-if="activeTab === 'account'" />
        <TransactionHistory v-else-if="activeTab === 'transactions'" />
        <TransactionForm v-else-if="activeTab === 'transfer'" />
      </div>
    </div>

    <div v-else-if="statusMessage && !isApproved" class="status-message-container">
      <div class="status-message-card">
        <img
          v-if="statusMessage.toLowerCase().includes('pending')"
          src="../assets/img/pending-icon.png"
          alt="Pending Icon"
          class="status-icon"
        />
        <img
          v-else
          src="../assets/img/denied-icon.jpg"
          alt="Denied Icon"
          class="status-icon"
        />
        <h3 class="status-heading">{{ statusMessage }}</h3>
        <p class="status-description">
          {{
            statusMessage.toLowerCase().includes('pending')
              ? 'Please wait for an employee to review your account. You will receive an update soon.'
              : 'If you believe this is a mistake, please contact customer support.'
          }}
        </p>
      </div>
    </div>

    <!-- ⏳ Loading -->
    <div v-else class="text-center mt-5">
      <p>Loading your dashboard...</p>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import { useAuthStore } from '@/stores/authstore';
import AccountInfo from './AccountInfoPage.vue';
import TransactionHistory from './TransactionsList.vue';
import TransactionForm from './TransactionForm.vue';

export default {
  name: 'CustomerDashboard',
  components: {
    AccountInfo,
    TransactionHistory,
    TransactionForm,
  },
  data() {
    return {
      errorMessage: '',
      statusMessage: '',
      isApproved: false,
      activeTab: 'account',
    };
  },
  methods: {
    logout() {
      useAuthStore().logout();
      this.$router.push('/login');
    },
    async fetchDashboardData() {
      try {
        const response = await api.get('/customer/dashboard');
        const message = response.data.message;
        this.statusMessage = message;
        this.isApproved = message.toLowerCase().includes('welcome');
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage =
            error.response.data.message || 'Failed to load dashboard data.';
        } else {
          this.errorMessage = 'Network error. Please check your connection.';
        }
      }
    },
  },
  mounted() {
    this.fetchDashboardData();
  },
};
</script>

<style scoped>
.dashboard-container {
  padding: 2rem;
  background: linear-gradient(to right, #93FB9D, #09C7FB);

}
.dashboard-content {
  border: 1px solid #e1e1e1;
  padding: 2rem;
  border-radius: 10px;
  background-color: #ffffff;
}

.status-message-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.status-message-card {
  text-align: center;
  background-color: #f9f9f9;
  padding: 3rem 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 500px;
}

.status-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 1rem;
}

.status-heading {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
}

.status-description {
  color: #666;
  font-size: 1rem;
}

.nav-tabs .nav-link.active {
  background-color: #ffffff;
  color: #09C7FB;
  border-color: #dee2e6 #dee2e6 #fff;
  font-weight: 600;
}
</style>
