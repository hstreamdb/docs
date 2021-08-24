<template>
  <!-- <RouterLink v-if="isInternal" class="nav-link" :to="link" @focusout.native="focusoutAction">
    {{ item.text }}
  </RouterLink> -->
  <!-- 对站内文档跳转特殊处理 -->
  <a v-if="isDocsLink" :href="link" class="nav-link router-link-active">
    {{ item.text }}
    <!-- <OutboundLink v-if="isBlankTarget" /> -->
  </a>

  <a v-else :href="link" class="nav-link external" :target="target" :rel="rel" @focusout="focusoutAction">
    {{ item.text }}
    <!-- <OutboundLink v-if="isBlankTarget" /> -->
  </a>
</template>

<script>
import { isExternal, isMailto, isTel, ensureExt } from '../util'

export default {
  name: 'NavLink',

  props: {
    item: {
      required: true,
    },
  },

  computed: {
    link() {
      return ensureExt(this.item.link)
    },

    exact() {
      if (this.$site.locales) {
        return Object.keys(this.$site.locales).some(rootLink => rootLink === this.link)
      }
      return this.link === '/'
    },

    isNonHttpURI() {
      return isMailto(this.link) || isTel(this.link)
    },

    isBlankTarget() {
      return this.target === '_blank'
    },

    isInternal() {
      return !isExternal(this.link) && !this.isBlankTarget
    },

    isDocsLink() {
      if (this.link.indexOf('/docs') !== -1) {
        return true
      }
      return false
    },

    docType() {
      return this.$themeConfig.gitHubConfig[this.$lang].docType
    },

    target() {
      if (this.isNonHttpURI) {
        return null
      }
      if (this.item.target) {
        return this.item.target
      }
      return isExternal(this.link) ? '_blank' : ''
    },

    rel() {
      if (this.isNonHttpURI) {
        return null
      }
      if (this.item.rel === false) {
        return null
      }
      if (this.item.rel) {
        return this.item.rel
      }
      return this.isBlankTarget ? 'noopener noreferrer' : null
    },
  },

  methods: {
    focusoutAction() {
      this.$emit('focusout')
    },
  },
}
</script>
