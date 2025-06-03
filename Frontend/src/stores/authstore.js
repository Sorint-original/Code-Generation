import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const token = localStorage.getItem('token')
    let isValid = false
    let role = null

    if (token) {
      try {
        const decoded = jwtDecode(token)
        const currentTime = Date.now() / 1000
        isValid = decoded.exp > currentTime
        if (isValid) {
          role = decoded.role
        }
      } catch (e) {
        console.error("Invalid token:", e)
      }
    }

    return {
      token,
      role,
      isAuthenticated: isValid
    }
  },

  actions: {
    login(token) {
      try {
        const decoded = jwtDecode(token)
        const currentTime = Date.now() / 1000
        const isValid = decoded.exp > currentTime

        if (!isValid) throw new Error("Token is expired")

        this.token = token
        this.role = decoded.role
        this.isAuthenticated = true

        localStorage.setItem('token', token)
        localStorage.setItem('role', this.role)
      } catch (e) {
        console.error("Login failed:", e.message)
        this.logout()
      }
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