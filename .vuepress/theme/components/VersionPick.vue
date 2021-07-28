<template>
  <div class="version-pick">
    <div class="dropdown-wrapper">
      <button type="button" aria-label="Languages" class="dropdown-title">
        <span class="title">
          {{ currentVersion }}
        </span>
        <span class="arrow down"></span>
      </button>
      <!-- <ul class="version-dropdown">
        <li v-for="(version, index) in versions" :key="index" class="dropdown-item">
          <a
            :href="`${versionLink}${version.value}/`"
            :class="{ 'version-link': true, active: version.label.indexOf(currentVersion) !== -1 }"
          >
            {{ version.label }}
          </a>
        </li>
      </ul> -->
    </div>
  </div>
</template>

<script>
import SearchBox from '@SearchBox'

export default {
  name: 'VersionPick',

  components: {
    SearchBox,
  },

  data() {
    return {
      versions: [],
    }
  },

  computed: {
    gitHubConfig() {
      const gitHubConfig = this.$themeConfig.gitHubConfig[this.$lang]
      return gitHubConfig
    },
    currentVersion() {
      return this.gitHubConfig.version
    },
    docType() {
      const { docType } = this.gitHubConfig
      return docType
    },
    versionLink() {
      const { path } = this.$localeConfig
      return `${path}${this.docType}/`
    },
  },

  methods: {
    async getVersions() {
      const currentDocType = this.$themeConfig.gitHubConfig[this.$lang].docType
      const { status, data } = await this.$axios.get(`https://docs.emqx.io/api/${currentDocType}_versions`)
      if (status === 200 && data) {
        this.versions = data
      }
    },
  },

  created() {
    // this.getVersions()
  },
}
</script>
