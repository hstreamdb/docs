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
  router.addRoute({ path: '/docs/en', redirect: '/docs/en/start/quickstart-with-docker' })
  router.addRoute({ path: '/docs/zh', redirect: '/docs/zh/start/quickstart-with-docker' })

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
