<template>

    <div class="container">
        <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">Test Page</h2>
            <p>This is a test page.</p>

            <button @click="logout()">Logout</button>
            
        </div>
        </div>
    </div>
</template>

<script>
import { useAuthStore } from "@/stores/authstore";

export default {
  name: "TestPage",
  data() {
  },
  created() {
    this.authStore = useAuthStore();
  },
  methods: {
    logout() {
      this.authStore.logout();
    },
  },
};

</script>

<style>
</style>