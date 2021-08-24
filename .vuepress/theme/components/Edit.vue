<template>
  <div class="docs-operation page-nav">
    <div>
      <div class="doc-download improve-doc">
        <a :href="editLink" class="improve-doc" target="_blank" rel="noreferrer">
          {{ editLinkText }}
        </a>
      </div>
      <div class="doc-download feedback-doc">
        <a :href="feedbackLink" target="_blank" rel="noreferrer">
          {{ feedbackLinkText }}
        </a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RightSideBar',
  computed: {
    gitHubConfig() {
      const gitHubConfig = this.$themeConfig.gitHubConfig[this.$lang]
      return gitHubConfig
    },
    editLinkText() {
      return this.gitHubConfig.editLinkText
    },
    editLink() {
      const { docsRepo, docsBranch, docsDir } = this.gitHubConfig
      const prefix = Object.keys(this.$themeLocaleConfig.sidebar)[0]
      const docsPath = ('/' + this.$page.relativePath).replace(prefix, '')
      return `https://github.com/${docsRepo}/edit/${docsBranch}/${docsDir}/${docsPath}`
    },
    feedbackLinkText() {
      return this.gitHubConfig.feedbackLinkText
    },
    feedbackLink() {
      const { docsRepo, docsDir } = this.gitHubConfig
      const prefix = Object.keys(this.$themeLocaleConfig.sidebar)[0]
      const docsPath = ('/' + this.$page.relativePath).replace(prefix, '')
      const href = `https://hstream.io${this.$route.path}`
      return `https://github.com/${docsRepo}/issues/new?body=File:%20[${docsDir}/${docsPath}](${href})`
    },
  },
}
</script>

<style></style>
