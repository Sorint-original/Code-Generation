<script>
import { useArticlesStore } from "@/stores/articles";

/**
 * @component ReadLaterTable
 * @description Displays a table of articles saved for later reading.
 * Users can view article details and remove articles from their reading list.
 */
export default {
  name: "ReadLaterTable",
  setup() {
    const store = useArticlesStore();
    return {
      store,
    };
  },
  computed: {
    /**
     * Get the list of articles saved for later reading
     * @returns {Article[]} Array of saved articles
     */
    articles() {
      return this.store.readLater;
    },
  },
  methods: {
    /**
     * Formats a date string into a localized, human-readable format
     * @param {string} date - The date string to format
     * @returns {string} The formatted date string
     */
    formatDate(date) {
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },

    /**
     * Removes an article from the read later list
     * @param {Article} article - The article to remove
     */
    removeArticle(article) {
      this.store.removeFromReadLater(article);
    },
  },
};
</script>

<template>
  <div class="container mt-4">
    <h2 class="mb-4">Read Later List</h2>
    <div v-if="articles.length === 0" class="alert alert-info">
      No articles saved for later reading.
    </div>
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
            <td>{{ formatDate(article.posted_at) }}</td>
            <td>
              <div class="btn-group" role="group">
                <RouterLink
                  :to="`/article/${article.id}`"
                  class="btn btn-primary btn-sm"
                  >View</RouterLink
                >
                <button
                  @click="removeArticle(article)"
                  class="btn btn-danger btn-sm"
                  title="Remove from read later list"
                >
                  Remove
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="mt-3">
        <button
          @click="store.clearReadLater"
          class="btn btn-outline-danger"
          :disabled="articles.length === 0"
        >
          Clear All
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-group .btn {
  margin-right: 0.25rem;
}
</style>
