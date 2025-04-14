<script>
import { API_ENDPOINTS } from "@/config";
import axios from "axios";
import Notification from "./Notification.vue";
import Loading from "./Loading.vue";

export default {
  name: "Profile",
  components: {
    Notification,
    Loading,
  },
  data() {
    return {
      user: null,
      error: null,
      isLoading: true,
    };
  },
  async mounted() {
    try {
      const response = await axios.get(API_ENDPOINTS.auth + "/me");
      this.user = response.data;
    } catch (error) {
      console.error(error);
      this.error = error?.response?.data?.error || "Failed to load user data";
    } finally {
      this.isLoading = false;
    }
  },
};
</script>

<template>
  <div class="container py-4">
    <Loading v-if="isLoading" />
    <Notification
      v-if="error"
      :isError="true"
      @close="error = null"
      class="mt-3"
    >
      {{ error }}
    </Notification>
    <div v-if="user" class="card shadow">
      <div class="card-body">
        <h2 class="card-title">Profile</h2>
        <div class="mt-3">
          <p><strong>Email:</strong> {{ user.email }}</p>
          <p><strong>ID:</strong> {{ user.id }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
