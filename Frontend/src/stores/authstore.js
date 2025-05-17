// src/stores/authStore.js
import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    role: localStorage.getItem('role') || null,
    isAuthenticated: !!localStorage.getItem('token')
  }),

  actions: {
    login(token) {
      this.token = token
      this.isAuthenticated = true

      // Decode token to extract role
      const decoded = jwtDecode(token)
      this.role = decoded.role

      // Save to localStorage
      localStorage.setItem('token', token)
      localStorage.setItem('role', this.role)
    },

    logout() {
      this.token = null
      this.role = null
      this.isAuthenticated = false

      localStorage.removeItem('token')
      localStorage.removeItem('role')
    }
  }
})
