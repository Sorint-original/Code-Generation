<template>
  <div class="login-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 400px;">
      <img class="login-logo mx-auto d-block mb-4" src="../assets/img/May 14, 2025, 09_37_41 PM.png" alt="Banking App Logo" />

      <form @submit.prevent="login">
        <div class="mb-3">
          <label for="email" class="form-label">Email address</label>
          <input v-model="email" type="email" class="form-control" id="email" required />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Password</label>
          <input v-model="password" type="password" class="form-control" id="password" required />
        </div>

        <button type="submit" class="btn w-100 text-white" style="background: linear-gradient(to right, #93FB9D, #09C7FB);">
          Login
        </button>

        <p v-if="errorMessages" class="text-danger mt-2">{{ errorMessages }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import api from "@/api/api";
import { useAuthStore } from "@/stores/authstore";
import { useRouter } from "vue-router";

export default {
  name: "AtmLoginPage",

  data() {
    return {
      email: "",
      password: "",
      errorMessages: "",
    };
  },

  created() {
    this.authStore = useAuthStore();
    this.router = useRouter();
  },

  methods: {
    async login() {
      try {
        const response = await api.post("/login", {
          email: this.email,
          password: this.password,
        });

        this.authStore.login(response.data.token);
        this.errorMessages = "";


        const role = this.authStore.role;
        if (role === "EMPLOYEE") {
          this.router.push("/employee");
        } else {
          this.router.push('/Atm');
        }
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessages =
            error.response.data.message || "Login failed. Please try again.";
        } else {
          this.errorMessages = "Network error. Please check your connection.";
        }

        console.error("Login failed:", error);
      }
    },
  },
};
</script>

<style scoped>
.login-page {
  background:(#09C7FB);
}
.login-logo {
  max-width: 200px;
  height: auto;
}
</style>
