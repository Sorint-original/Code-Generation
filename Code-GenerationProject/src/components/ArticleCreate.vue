<script>
import ArticleForm from "./ArticleForm.vue";
import axios from "axios";
import { API_ENDPOINTS } from "@/config";

/**
 * @component ArticleCreate
 * @description A component that handles the creation of new articles.
 * It provides a form interface and handles the submission process to create new articles.
 */
export default {
  data() {
    return {
      article: null, // The article data being created
      isLoading: false, // Loading state flag
      error: null, // Error message storage
      articleId: this.$route.params.id, // ID from route params (if applicable)
      success: null, // Success message storage
    };
  },
  components: {
    ArticleForm,
  },
  methods: {
    /**
     * Creates a new article with the provided data
     * @param {Object} articleData - The new article data
     */
    async createArticle(articleData) {
      try {
        this.isLoading = true;
        await axios.post(API_ENDPOINTS.articles, articleData);
        this.success = "Article created";
      } catch (error) {
        console.error(error);
        this.error = error.message || "An error occurred";
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<template>
  <div v-if="error" class="alert alert-danger" role="alert">
    {{ error }}
  </div>
  <div v-if="success" class="alert alert-success" role="alert">
    {{ success }}
  </div>
  <div v-if="isLoading">Loading...</div>
  <ArticleForm v-else @submitForm="createArticle" />
</template>

<style lang="css" scoped></style>
