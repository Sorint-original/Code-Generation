<script>
import axios from "axios";
import { API_ENDPOINTS } from "@/config";
import Notification from "./Notification.vue";
import Loading from "./Loading.vue";

/**
 * @component ArticlePage
 * @description A component that displays the full details of an article.
 * It fetches the article data based on the route parameter and displays
 * it in a card layout with title, author, posting date, and content.
 * Includes loading states and error handling using shared components.
 */
export default {
  name: "ArticlePage",
  components: {
    Notification,
    Loading,
  },
  data() {
    return {
      /** @type {Object|null} The article data containing id, title, content, author, and posted_at */
      article: null,
      /** @type {boolean} Flag indicating if the article is being loaded */
      isLoading: true,
      /** @type {string|null} Error message to display if article loading fails */
      error: null,
    };
  },
  /**
   * Lifecycle hook that fetches the article data when the component is mounted
   * Uses the route parameter id to fetch the specific article
   * @async
   */
  async mounted() {
    try {
      const articleId = this.$route.params.id;
      const response = await axios.get(
        `${API_ENDPOINTS.articles}/${articleId}`
      );
      this.article = response.data;
    } catch (error) {
      console.error(error);
      this.error =
        error.message || "An error occurred while loading the article";
    } finally {
      this.isLoading = false;
    }
  },
  methods: {
    /**
     * Formats a date string into a localized, human-readable format
     * @param {string} date - The date string to format
     * @returns {string} The formatted date string (e.g., "March 12, 2024")
     */
    formatDate(date) {
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
  },
};
</script>

<template>
  <div class="container mt-5">
    <Notification
      v-if="error"
      :isError="true"
      @close="error = null"
      class="mb-4"
    >
      {{ error }}
    </Notification>
    <Loading v-if="isLoading" />
    <div v-else-if="article" class="row">
      <div class="col-lg-8 mx-auto">
        <article class="card">
          <div class="card-body">
            <h1 class="card-title mb-3">{{ article.title }}</h1>
            <div class="d-flex justify-content-between align-items-center mb-4">
              <div class="text-muted">
                <small>
                  By <span class="fw-bold">{{ article.author }}</span>
                  <span class="mx-2">•</span>
                  {{ formatDate(article.posted_at) }}
                </small>
              </div>
              <div class="badge bg-secondary">ID: {{ article.id }}</div>
            </div>
            <div class="card-text article-content">
              {{ article.content }}
            </div>
          </div>
        </article>
      </div>
    </div>
    <div v-else class="alert alert-warning" role="alert">Article not found</div>
  </div>
</template>

<style scoped>
/**
 * Styles for the article content to improve readability
 * Increases line height, font size, and preserves whitespace
 */
.article-content {
  line-height: 1.8;
  font-size: 1.1rem;
  white-space: pre-wrap;
}
</style>
