import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const token = localStorage.getItem('token')
    let isValid = false
    let role = null
    let userId = null

    if (token) {
      try {
        const decoded = jwtDecode(token)
        const currentTime = Date.now() / 1000
        isValid = decoded.exp > currentTime
        if (isValid) {
          role = decoded.role
          userId = decoded.id
        }
      } catch (e) {
        console.error("Invalid token:", e)
      }
    }

    return {
      token,
      role,
      userId,
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
        this.userId = decoded.id
        this.isAuthenticated = true

        localStorage.setItem('token', token)
        localStorage.setItem('role', this.role)
        localStorage.setItem('id', this.userId)
      } catch (e) {
        console.error("Login failed:", e.message)
        this.logout()
      }
    },

    logout() {
      this.token = null
      this.role = null
      this.userId = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('role')
    }
  }
})
