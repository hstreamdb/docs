import axios from 'axios'
import pageComponents from '@internal/page-components'

export default ({
  Vue, // the version of Vue being used in the VuePress app
  options, // the options for the root Vue instance
  router, // the router instance for the app
  siteData, // site metadata
}) => {
  Vue.prototype.$axios = axios

  // add redirect
  const { baseUrl } = siteData.themeConfig
  router.addRoute({ path: '/docs/en/', redirect: `${baseUrl.en}start/quickstart-with-docker.html` })
  router.addRoute({ path: '/docs/zh/', redirect: `${baseUrl.zh}start/quickstart-with-docker.html` })

  // fix unable to scroll to anchor
  if (typeof document === 'undefined') return
  document.onreadystatechange = () => {
    if (document.readyState === 'complete') {
      const { hash } = location
      const decoded = decodeURIComponent(hash)
      if (hash !== decoded) {
        document.querySelector(decoded).scrollIntoView()
      }
    }
  }

  for (const [name, component] of Object.entries(pageComponents)) {
    Vue.component(name, component)
  }
}
