<template>
  <main class="page">
    <slot name="top" />
    <LoadingPage v-show="isLoading" />
    <Content v-show="!isLoading" class="theme-default-content" />
    <!-- <PageEdit /> -->
    <slot name="bottom" />

    <PageNav v-bind="{ sidebarItems }" />
  </main>
</template>

<script>
// import PageEdit from '@theme/components/PageEdit.vue'
import PageNav from '@theme/components/PageNav.vue'
import LoadingPage from '../components/LoadingPage'

export default {
  components: {
    // PageEdit,
    PageNav,
    LoadingPage,
  },
  props: ['sidebarItems'],
  data() {
    return {
      isLoading: false,
    }
  },
  mounted() {
    this.$router.beforeEach((to, from, next) => {
      if (to.path !== from.path) {
        this.isLoading = true
      }
      next()
    })

    this.$router.afterEach(() => {
      setTimeout(() => {
        this.isLoading = false
      }, 500)
    })
  },
}
</script>

<style lang="stylus">
@require '../styles/wrapper.styl'

.page
  padding-bottom 2rem
  display block
</style>
