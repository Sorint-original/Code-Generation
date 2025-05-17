<template>
  <div class="register-page d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 400px;">
      <img class="login-logo mx-auto d-block mb-4" src="../assets/img/May 14, 2025, 09_37_41 PM.png" alt="Banking App Logo" />

      <form @submit.prevent="register">
        <div class="mb-3" v-for="(label, field) in fields" :key="field">
          <label :for="field" class="form-label">{{ label }}</label>
          <input
            v-model="form[field]"
            :type="field === 'password' ? 'password' : field === 'email' ? 'email' : 'text'"
            :inputmode="field === 'email' ? 'email' : undefined"
            :autocomplete="field === 'email' ? 'email' : undefined"
            class="form-control"
            :id="field"
            required
          />
        </div>

        <button type="submit" class="btn w-100 text-white" style="background: linear-gradient(to right, #93FB9D, #09C7FB);">
          Register
        </button>

        <p v-if="errorMessage" class="text-danger mt-2">{{ errorMessage }}</p>
        <p v-if="successMessage" class="text-success mt-2">{{ successMessage }}</p>
      </form>
    </div>
  </div>
</template>


<script>
import api from "@/api/api";
import { useRouter } from "vue-router";
export default {
  name: "RegisterPage",
  data() {
    return {
      form: {
        firstName: "",
        lastName: "",
        userName: "",
        email: "",
        password: "",
        phoneNumber: "",
        bsnNumber: ""
      },
      errorMessage: "",
      successMessage: "",
      fields: {
        firstName: "First Name",
        lastName: "Last Name",
        userName: "Username",
        email: "Email",
        password: "Password",
        phoneNumber: "Phone Number",
        bsnNumber: "BSN Number"
      }
    };
  },
  methods: {
    async register() {
      try {
        const response = await api.post("/user/register", this.form);
        if (response.data.success) {
          this.successMessage = response.data.message;
          this.errorMessage = "";
          this.goToLogin();
        } else {
          this.errorMessage = response.data.message;
          this.successMessage = "";
        }
      } catch (error) {
        this.errorMessage = "Something went wrong. Please try again.";
        console.error("Registration error:", error);
      }
    },

    goToLogin() {
      this.$router.push("/login");
    }
  }
};
</script>

<style scoped>
.register-page {
  background: linear-gradient(to right, #93FB9D, #09C7FB);
}
.login-logo {
  max-width: 200px;
  height: auto;
}
</style>
