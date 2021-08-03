<template>
  <div>
    <div class="theme-container" :class="pageClasses" @touchstart="onTouchStart" @touchend="onTouchEnd">
      <Navbar v-if="shouldShowNavbar" @toggle-sidebar="toggleSidebar" />

      <div class="sidebar-mask" @click="toggleSidebar(false)" />
      <section class="main-box">
        <div class="columns is-multiline is-mobile">
          <Sidebar :items="sidebarItems" @toggle-sidebar="toggleSidebar">
            <template #top>
              <VersionPick />
            </template>
            <template #bottom>
              <slot name="sidebar-bottom" />
            </template>
          </Sidebar>
          <div class="column col-page">
            <Home v-if="$page.frontmatter.home" />
            <Page v-else :sidebar-items="sidebarItems">
              <template #top>
                <slot name="page-top" />
              </template>
              <template #bottom>
                <Edit />
              </template>
            </Page>
          </div>
        </div>
      </section>
    </div>
    <Footer />
  </div>
</template>

<script>
import Navbar from '@theme/components/Navbar.vue'
import Page from '@theme/components/Page.vue'
import Edit from '@theme/components/Edit.vue'
import Sidebar from '@theme/components/Sidebar.vue'
import { resolveSidebarItems } from '../util'
import VersionPick from '../components/VersionPick'
import Footer from '../components/Footer'

export default {
  name: 'Layout',

  components: {
    Navbar,
    Page,
    Edit,
    Sidebar,
    VersionPick,
    Footer,
  },

  data() {
    return {
      isSidebarOpen: false,
    }
  },

  watch: {
    isSidebarOpen: function(val) {
      if (val === true) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = 'visible'
      }
    },
  },

  computed: {
    shouldShowNavbar() {
      const { themeConfig } = this.$site
      const { frontmatter } = this.$page
      if (frontmatter.navbar === false || themeConfig.navbar === false) {
        return false
      }
      return this.$title || themeConfig.logo || themeConfig.repo || themeConfig.nav || this.$themeLocaleConfig.nav
    },

    shouldShowSidebar() {
      const { frontmatter } = this.$page
      return !frontmatter.home && frontmatter.sidebar !== false && this.sidebarItems.length
    },

    sidebarItems() {
      return resolveSidebarItems(this.$page, this.$page.regularPath, this.$site, this.$localePath)
    },

    pageClasses() {
      const userPageClass = this.$page.frontmatter.pageClass
      return [
        {
          'no-navbar': !this.shouldShowNavbar,
          'sidebar-open': this.isSidebarOpen,
          'no-sidebar': !this.shouldShowSidebar,
        },
        userPageClass,
      ]
    },
  },

  mounted() {
    this.$router.afterEach(() => {
      this.isSidebarOpen = false
    })
    const searchInput = document.querySelector('.search-box > input')
    searchInput.addEventListener(
      'focus',
      () => {
        if (document.body.offsetWidth <= 720) {
          this.isSidebarOpen = false
          document.body.style.overflow = 'hidden'
        }
      },
      false,
    )
    searchInput.addEventListener(
      'blur',
      () => {
        if (document.body.offsetWidth <= 720) {
          document.body.style.overflow = 'visible'
        }
      },
      false,
    )
  },

  methods: {
    toggleSidebar(to) {
      this.isSidebarOpen = typeof to === 'boolean' ? to : !this.isSidebarOpen
      this.$emit('toggle-sidebar', this.isSidebarOpen)
    },

    // side swipe
    onTouchStart(e) {
      this.touchStart = {
        x: e.changedTouches[0].clientX,
        y: e.changedTouches[0].clientY,
      }
    },

    onTouchEnd(e) {
      const dx = e.changedTouches[0].clientX - this.touchStart.x
      const dy = e.changedTouches[0].clientY - this.touchStart.y
      if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 40) {
        if (dx > 0 && this.touchStart.x <= 80) {
          this.toggleSidebar(true)
        } else {
          this.toggleSidebar(false)
        }
      }
    },
  },
}
</script>

<style lang="scss">
@import '../styles/scss/base.scss';
</style>
