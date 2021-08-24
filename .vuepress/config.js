const navConfig = require('./config/navConfig')
const footerConfig = require('./config/footerConfig')
const gitHubConfig = require('./config/githubConfig')
const directory = require('../directory.json')
const version = process.env.VERSION
const prefixEN = `/docs/en/${version ? `${version}/` : ''}`
const prefixZH = `/docs/zh/${version ? `${version}/` : ''}`

const { removePlugin, PLUGINS } = require('@vuepress/markdown')
const fs = require('fs')
const { path } = require('@vuepress/shared-utils')

module.exports = {
  host: '0.0.0.0',
  head: [
    ['link', { rel: 'shortcut icon', type: 'image/x-icon', href: '/favicon.ico' }],
    [
      'meta',
      {
        name: 'viewport',
        content: 'width=device-width, initial-scale=1.0,minimum-scale=1.0, maximum-scale=1.0, user-scalable=no',
      },
    ],
    ['meta', { property: 'og:site_name', content: 'hstream.io' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:url', content: 'https://hstream.io/' }],
    ['meta', { property: 'og:title', content: 'HStreamDB Docs' }],
    ['meta', { property: 'og:image', content: 'https://hstream.io/logo-512.png' }],
  ],
  plugins: [
    [
      '@vuepress/active-header-links',
      {
        sidebarLinkSelector: '.sidebar-link',
        headerAnchorSelector: '.header-anchor',
      },
    ],
    '@vuepress/back-to-top',
    '@vuepress/medium-zoom',
    '@vuepress/nprogress',
    [
      '@vuepress/google-analytics',
      {
        ga: 'UA-177396745-1',
      },
    ],
    [
      'fulltext-search',
      {
        // provide the contents of a JavaScript file
        hooks: fs.readFileSync(path.resolve(__dirname, './theme/util/searchHooks.js')),
      },
    ],
    [
      '@snowdog/vuepress-plugin-pdf-export',
      {
        outputFileName: `${gitHubConfig.en.docName} ${gitHubConfig.en.version}.pdf`,
        pageOptions: {
          margin: { top: 32, right: 48, bottom: 64, left: 48 },
          displayHeaderFooter: true,
          footerTemplate: `
              <div style="width: 100%; margin: 16px 48px; font-size: 9px; text-align: center;">
                <span>${gitHubConfig.en.docName} ${gitHubConfig.en.version}</span>
              </div>
          `,
        },
      },
    ],
    [
      'vuepress-plugin-code-copy',
      {
        color: '#613EDA',
      },
    ],
    [
      '@vuepress/last-updated',
      {
        transformer: (timestamp, lang) => {
          return new Date(timestamp).toLocaleDateString()
        },
      },
    ],
    [
      'sitemap',
      {
        hostname: 'https://hstream.io',
        exclude: ['/404.html'],
        outFile: 'sitemap_docs.xml',
      },
    ],
    [
      'vuepress-plugin-container',
      {
        type: 'tip',
        defaultTitle: {
          '/docs/en/': 'TIP',
          '/docs/zh/': '提示',
        },
      },
    ],
    [
      'vuepress-plugin-container',
      {
        type: 'warning',
        defaultTitle: {
          '/docs/en/': 'WARING',
          '/docs/zh/': '注意',
        },
      },
    ],
    [
      'vuepress-plugin-container',
      {
        type: 'danger',
        defaultTitle: {
          '/docs/en/': 'DANGER',
          '/docs/zh/': '警告',
        },
      },
    ],
  ],
  themeConfig: {
    locales: {
      '/': {
        lang: 'en',
        lastUpdated: 'last updated',
        selectText: 'Languages',
        label: 'English',
        ariaLabel: 'Languages',
        editLinkText: 'Edit this page on GitHub',
        nav: navConfig.en,
        sidebarDepth: 1,
        sidebar: {
          [prefixEN]: directory.en,
        },
      },
      '/docs/zh/': {
        lang: 'zh-CN',
        lastUpdated: '更新时间',
        selectText: '选择语言',
        label: '中文',
        editLinkText: '在 GitHub 上编辑此页',
        nav: navConfig.zh,
        sidebarDepth: 1,
        sidebar: {
          [prefixZH]: directory.zh,
        },
      },
    },
    // 默认值是 true 。设置为 false 来禁用所有页面的 下一篇 链接
    nextLinks: true,
    // 默认值是 true 。设置为 false 来禁用所有页面的 上一篇 链接
    preLinks: true,
    // 左上放 logo
    logo: '/logo.png',
    // 页面滚动
    smoothScroll: true,
    // GitHub设置
    gitHubConfig: gitHubConfig,
    // 页脚设置
    footerConfig: footerConfig,
    baseUrl: {
      en: prefixEN,
      zh: prefixZH,
    },
    // 搜索最大数量
    searchMaxSuggestions: 30,
  },
  postcss: [require('autoprefixer')],
  sass: { indentedSyntax: true },
  locales: {
    '/': {
      lang: 'en',
      title: 'HStreamDB Docs',
      description: 'HStreamDB Docs',
      url: prefixEN,
    },
    '/docs/zh/': {
      lang: 'zh-CN',
      title: 'HStreamDB Docs',
      description: 'HStreamDB 使用文档',
      url: prefixZH,
    },
  },
  markdown: {
    lineNumbers: true,
    chainMarkdown(config) {
      removePlugin(config, PLUGINS.EMOJI)
    },
  },
  chainWebpack: (webpackConfig, isServer) => {
    webpackConfig.module
      .rule('compile')
      .test(/\.js$/)
      .include.add(/@vuepress/)
      .add(/.temp/)
      .add(/docs/)
      .add(/packages/)
      .end()
      .use('babel')
      .loader('babel-loader')
      .options({
        presets: [
          [
            '@vue/babel-preset-app',
            {
              useBuiltIns: 'entry',
            },
          ],
        ],
      })
  },
}
