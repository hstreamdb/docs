<template>
  <div class="theme-container">
    <div class="theme-default-content not-find">
      <img :src="$withBase('/images/404.png')" alt="404" />
      <h1 class="not-find-title">{{ msg }}</h1>

      <a :href="backHomeLink">
        <div class="btn back-home">
          {{ backHome }}
        </div>
      </a>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      backHomeLink: 'https://nanomq.io/zh/dosc/latest/',
      backHome: '返回首页',
      msg: '抱歉，你访问的页面不存在。',
      msgs: [
        `There's nothing here.`,
        `How did we get here?`,
        `That's a Four-Oh-Four.`,
        `Looks like we've got some broken links.`,
      ],
    }
  },

  mounted() {
    if (this.$route.path.indexOf('/en/') !== -1) {
      this.msg = this.msgs[Math.floor(Math.random() * this.msgs.length)]
      this.backHome = 'Take me home'
      this.backHomeLink = 'https://nanomq.io/dosc/latest/'
    }
  },
}
</script>

<style lang="scss">
@import '../styles/scss/base.scss';
</style>
