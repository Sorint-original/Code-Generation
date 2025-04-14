<script>
import ArticleForm from "./ArticleForm.vue";
import axios from "axios";
import { API_ENDPOINTS } from "@/config";
import Notification from "./Notification.vue";
import Loading from "./Loading.vue";

/**
 * @component ArticleUpdate
 * @description A component that handles the updating of existing articles.
 * It fetches the article data, displays it in a form, and handles the update submission.
 */
export default {
  data() {
    return {
      article: null, // The article data being edited
      isLoading: true, // Loading state flag
      error: null, // Error message storage
      articleId: this.$route.params.id, // ID of the article from route params
      success: null, // Success message storage
    };
  },
  components: {
    ArticleForm,
    Notification,
    Loading,
  },
  /**
   * Fetches the article data when the component is mounted
   */
  async mounted() {
    try {
      this.isLoading = true;
      const response = await axios.get(
        `${API_ENDPOINTS.articles}/${this.articleId}`
      );
      this.article = response.data;
    } catch (error) {
      this.error = error.message || "An error occurred";
    } finally {
      this.isLoading = false;
    }
  },
  methods: {
    /**
     * Updates an article with the provided data
     * @param {Object} articleData - The updated article data
     */
    async updateArticle(articleData) {
      try {
        this.isLoading = true;
        await axios.put(
          `${API_ENDPOINTS.articles}/${this.articleId}`,
          articleData
        );
        this.success = "Article updated";
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
  <Notification
    v-if="error"
    :isError="true"
    @close="
      () => {
        error = '';
      }
    "
    >{{ error }}</Notification
  >
  <Notification
    v-if="success"
    :isError="false"
    @close="
      () => {
        success = '';
      }
    "
    >{{ success }}</Notification
  >
  <Loading v-if="isLoading" />
  <ArticleForm
    v-if="article"
    :initialArticle="article"
    @submitForm="updateArticle"
  />
</template>

<style lang="css" scoped></style>
