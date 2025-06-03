<template>
  <div class="limit-form d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow-lg" style="width: 400px;">
      <h3 class="text-center mb-4">Change Daily Limit</h3>

      <form @submit.prevent="submitLimitChange">
        <div class="mb-3">
          <label for="iban" class="form-label">Account IBAN</label>
          <input v-model="iban" type="text" class="form-control" id="iban" required />
        </div>

        <div class="mb-3">
          <label for="limit" class="form-label">New Daily Limit (€)</label>
          <input v-model="dailyLimit" type="number" class="form-control" id="limit" step="0.01" required />
        </div>

        <button type="submit" class="btn w-100 text-white" style="background: linear-gradient(to right, #93FB9D, #09C7FB);">
          Submit Change
        </button>

        <p v-if="message" :class="messageColor + ' mt-3 text-center'">{{ message }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import api from "@/api/api";

export default {
  name: "DailyLimitChangeForm",
  data() {
    return {
      iban: "",
      dailyLimit: "",
      message: "",
      messageColor: "",
    };
  },
  methods: {
    async submitLimitChange() {
      try {
        const response = await api.post("/employee/change-limit", {
          iban: this.iban,
          dailyLimit: parseFloat(this.dailyLimit),
        });
        this.message = response.data.message || "Limit changed successfully!";
        this.messageColor = "text-success";
      } catch (error) {
        this.message = error.response?.data?.message || "Failed to change limit.";
        this.messageColor = "text-danger";
      }
    },
  },
};
</script>

<style scoped>
.limit-form {
  background: linear-gradient(to right, #93FB9D, #09C7FB);
}
</style>
