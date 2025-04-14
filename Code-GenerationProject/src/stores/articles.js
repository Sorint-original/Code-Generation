import { defineStore } from "pinia";

export const useArticlesStore = defineStore("articles", {
  state: () => ({
    readLater: [],
  }),

  getters: {
    readLaterCount: (state) => state.readLater.length,
  },

  actions: {
    addToReadLater(article) {
      if (!this.isInReadLater(article.id)) {
        this.readLater.push(article);
      }
    },

    removeFromReadLater(article) {
      this.readLater = this.readLater.filter((a) => a.id !== article.id);
    },

    clearReadLater() {
      this.readLater = [];
    },

    isInReadLater(articleId) {
      return this.readLater.some((article) => article.id === articleId);
    },
  },
});
