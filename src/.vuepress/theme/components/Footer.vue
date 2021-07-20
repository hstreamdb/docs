<template>
  <footer id="footer">
    <div class="theme-container">
      <div>
        <div class="columns is-gapless is-vcentered">
          <div class="column">
            <div class="lang-change">
              <button
                type="button"
                aria-label="Languages"
                class="dropdown-title"
                @click.stop="changeLangFlag = !changeLangFlag"
              >
                <img :src="$withBase('/images/language.png')" />
                <span class="title">
                  {{ $lang === 'zh-CN' ? '中文' : 'English' }}
                </span>
                <span class="arrow down"></span>
                <ul :class="{ 'dropdown-menu': true, 'is-active': changeLangFlag }">
                  <li
                    :class="['dropdown-item', { 'is-active': $lang === 'en' }]"
                    @click="changeLanguage('/docs/latest/')"
                  >
                    English
                  </li>
                  <li
                    :class="['dropdown-item', { 'is-active': $lang === 'zh-CN' }]"
                    @click="changeLanguage('/zh/docs/latest/')"
                  >
                    中文
                  </li>
                </ul>
              </button>
            </div>
            <ul>
              <li v-for="(col, title) in column" :key="title">
                <a :href="col.link">{{ col.title }}</a>
              </li>
            </ul>
          </div>
          <div id="form-mail" class="column form-mail">
            <div>
              <p :class="{ 'sub-tips': true, 'is-error': isError, 'is-success': isSuccess }">
                {{ message }}
              </p>
              <input
                v-model="emailAddress"
                id="inputEmail"
                type="email"
                required
                name="inputEmail"
                :class="{
                  'form-control': true,
                  'error-input': isError,
                  'success-input': isSuccess,
                }"
                :placeholder="$lang === 'zh-CN' ? '邮箱地址' : 'Email Address'"
                @focus="clearError"
              />
              <button class="button sub-btn" @click="newsLetter">
                {{ $lang === 'zh-CN' ? '订阅' : 'Subscribe' }}
              </button>
            </div>
          </div>
        </div>
        <div class="record-information columns is-gapless is-vcentered">
          <div class="column">
            <p>© 2021 EMQ Technologies Co., Ltd. All rights reserved</p>
          </div>
          <div class="follow-us column">
            <a
              v-for="(followItem, index) in followList"
              :key="index"
              :href="followItem.link"
              target="_blank"
              rel="noopener"
            >
              <img :src="$withBase(followItem.img)" />
            </a>
            <a class="wechat" v-if="$lang === 'zh-CN'" href="javascript:;">
              <img :src="$withBase('/images/wechat.png')" />
              <img class="qr-code" src="https://static.emqx.net/images/new-mails/qr_code.png" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer',
  data() {
    return {
      changeLangFlag: false,
      emailAddress: '',
      isError: false,
      isSuccess: false,
      message: '',
    }
  },
  computed: {
    column() {
      const { footerConfig } = this.$themeConfig
      return footerConfig[this.$lang].column
    },
    followList() {
      const { footerConfig } = this.$themeConfig
      return footerConfig[this.$lang].followList
    },
  },

  methods: {
    changeLanguage(url) {
      this.$router.push(url)
    },

    newsLetter() {
      const pattern = /^([A-Za-z0-9_\-.])+@([A-Za-z0-9_\-.])+\.([A-Za-z]{2,4})$/
      const isValid = pattern.test(this.emailAddress)
      const language = this.$lang === 'zh-CN' ? 'cn' : 'en'
      if (isValid) {
        const data = {
          email: this.emailAddress,
          language,
          source: 'nanomq',
        }
        this.$axios
          .post(`/api/${language}/subscriptions`, data)
          .then(({ status, data }) => {
            if (status === 200 && data) {
              this.message =
                this.$lang === 'zh-CN'
                  ? '谢谢您的订阅！我们将会实时为您提供最新的资讯。'
                  : 'Thanks for subscribing to the newsletter.'
              this.isSuccess = true
              this.isError = false
            }
          })
          .catch((_error) => {
            this.message = this.$lang === 'zh-CN' ? '该邮件已订阅' : 'Email has exist'
            this.isSuccess = false
            this.isError = true
          })
      } else {
        this.message = this.$lang === 'zh-CN' ? 'Email 格式错误' : 'Not a valid email address'
        this.isSuccess = false
        this.isError = true
      }
    },

    clearError() {
      this.isError = false
      this.isSuccess = false
      this.message = ''
    },
  },

  mounted() {
    this.$nextTick(() => {
      document.querySelector('.lang-change').addEventListener(
        'click',
        () => {
          this.changeLangFlag = false
        },
        false,
      )
      document.addEventListener('click', () => {
        this.changeLangFlag = false
      })
    })
  },
}
</script>
