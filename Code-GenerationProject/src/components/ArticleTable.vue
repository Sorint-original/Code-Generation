<script>
import axios from "axios";
import { API_ENDPOINTS } from "@/config";
import { useArticlesStore } from "@/stores/articles";

/**
 * @component ArticleTable
 * @description A component that displays a paginated table of articles with edit functionality.
 * The table shows article titles, authors, posting dates, and edit links.
 */
export default {
  setup() {
    const store = useArticlesStore();
    return {
      store,
    };
  },
  data() {
    return {
      articles: [], // Array of article objects
      isLoading: true, // Loading state flag
      error: null, // Error message storage
      currentPage: 1, // Current page number for pagination
    };
  },
  async mounted() {
    await this.loadArticles();
  },
  methods: {
    /**
     * Fetches articles from the API for the current page
     */
    async loadArticles() {
      // asynchronously load articles
      // handle loading and error states
      try {
        // set loading state to true
        this.isLoading = true;
        // clear error messages
        this.error = null;
        // clear existing articles
        this.articles = [];
        const results = await axios.get(
          `${API_ENDPOINTS.articles}?page=${this.currentPage}`
        );
        this.articles = results.data;
      } catch (error) {
        console.error(error);
        this.error = error.message || "An error occurred";
      } finally {
        this.isLoading = false;
      }
    },
    /**
     * Increments the page number and loads the next page of articles
     */
    async loadNextPage() {
      this.currentPage++;
      await this.loadArticles();
    },
    /**
     * Decrements the page number and loads the previous page of articles
     */
    async loadPreviousPage() {
      this.currentPage--;
      await this.loadArticles();
    },

    /**
     * Deletes an article with the given ID
     * @param {number} id - The ID of the article to delete
     */
    async deleteArticle(id) {
      console.log(`Deleting article with ID: ${id}`);
      console.error(new Error("Method not implemented"));
    },

    /**
     * Toggles whether an article is saved for later reading
     * @param {Article} article - The article to toggle
     */
    toggleReadLater(article) {
      if (this.store.isInReadLater(article.id)) {
        this.store.removeFromReadLater(article);
      } else {
        this.store.addToReadLater(article);
      }
    },

    /**
     * Checks if an article is in the read later list
     * @param {number} articleId - The ID of the article to check
     * @returns {boolean} True if the article is saved for later
     */
    isInReadLater(articleId) {
      return this.store.isInReadLater(articleId);
    },
  },
};
</script>

<template>
  <div v-if="error" class="alert alert-danger" role="alert">
    {{ error }}
  </div>
  <div v-if="isLoading">Loading...</div>
  <div v-else>
    <table class="table">
      <thead>
        <tr>
          <th>Title</th>
          <th>Author</th>
          <th>Posted At</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="article in articles" :key="article.id">
          <td>
            <RouterLink
              :to="`/article/${article.id}`"
              class="text-decoration-none"
            >
              {{ article.title }}
            </RouterLink>
          </td>
          <td>{{ article.author }}</td>
          <td>{{ article.posted_at }}</td>
          <td>
            <div class="btn-group" role="group">
              <RouterLink
                type="button"
                class="btn btn-primary btn-sm"
                :to="`/articles-edit/${article.id}`"
                >Edit</RouterLink
              >
              <button
                @click="toggleReadLater(article)"
                class="btn btn-sm"
                :class="{
                  'btn-success': !isInReadLater(article.id),
                  'btn-warning': isInReadLater(article.id),
                }"
                :title="
                  isInReadLater(article.id)
                    ? 'Remove from read later'
                    : 'Save for later'
                "
              >
                {{ isInReadLater(article.id) ? "Saved" : "Save for Later" }}
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click="deleteArticle(article.id)"
              >
                Delete
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <nav aria-label="...">
      <ul class="pagination">
        <li :class="{ 'page-item': true, disabled: currentPage === 1 }">
          <a @click="loadPreviousPage" class="page-link" href="#">Previous</a>
        </li>
        <li :class="{ 'page-item': true, disabled: articles.length < 10 }">
          <a @click="loadNextPage" class="page-link" href="#">Next</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<style lang="css" scoped>
.btn-group .btn {
  margin-right: 0.25rem;
}
</style>
