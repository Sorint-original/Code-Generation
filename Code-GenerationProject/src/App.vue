<script>
import { useArticlesStore } from "@/stores/articles";

export default {
  setup() {
    const store = useArticlesStore();
    return {
      store,
    };
  },
};
</script>
<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <RouterLink to="/" class="navbar-brand">Article App</RouterLink>
      <div class="navbar-nav ms-auto">
        <RouterLink class="nav-item nav-link" to="/article"
          >Add New Article</RouterLink
        >
        <RouterLink class="nav-item nav-link" to="/read-later">
          Read Later
          <span v-if="store.readLaterCount" class="badge bg-primary ms-1">
            {{ store.readLaterCount }}
          </span>
        </RouterLink>
        <RouterLink class="nav-item nav-link" to="/auth"
          >Login/Register</RouterLink
        >
        <RouterLink class="nav-item nav-link" to="/profile">Profile</RouterLink>
      </div>
    </div>
  </nav>
  <div class="container">
    <main>
      <RouterView />
    </main>
  </div>
</template>
