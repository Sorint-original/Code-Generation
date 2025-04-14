<script>
export default {
  props: {
    initialArticle: {
      type: Object,
      default: () => ({
        id: null,
        title: "",
        content: "",
        author: "",
        posted_at: new Date().toISOString(),
      }),
    },
  },
  data() {
    return {
      article: { ...this.initialArticle },
    };
  },
  computed: {
    formattedPostedAt() {
      return this.article.posted_at ? this.article.posted_at.slice(0, 16) : "";
    },
  },
  methods: {
    updatePostedAt(event) {
      this.article.posted_at = new Date(event.target.value).toISOString();
    },
    submitForm() {
      this.$emit("submitForm", { ...this.article });
    },
  },
};
</script>

<template>
  <form @submit.prevent="submitForm" class="container mt-3">
    <div class="mb-3">
      <label for="title" class="form-label">Title:</label>
      <input
        v-model="article.title"
        type="text"
        id="title"
        class="form-control"
        required
      />
    </div>

    <div class="mb-3">
      <label for="content" class="form-label">Content:</label>
      <textarea
        v-model="article.content"
        id="content"
        class="form-control"
        required
      ></textarea>
    </div>

    <div class="mb-3">
      <label for="author" class="form-label">Author:</label>
      <input
        v-model="article.author"
        type="text"
        id="author"
        class="form-control"
        required
      />
    </div>

    <div class="mb-3">
      <label for="posted_at" class="form-label">Posted At:</label>
      <input
        v-model="formattedPostedAt"
        type="datetime-local"
        id="posted_at"
        class="form-control"
        required
        @input="updatePostedAt"
      />
    </div>

    <button type="submit" class="btn btn-primary">
      {{ article.id ? "Update" : "Create" }} Article
    </button>
  </form>
</template>
