const version = process.env.VERSION
const prefixEN = `/docs/en/${version ? `${version}/` : ''}`
const prefixZH = `/docs/zh/${version ? `${version}/` : ''}`

module.exports = {
  'zh-CN': {
    column: [
      {
        title: 'GitHub',
        link: 'https://github.com/hstreamdb/hstream/',
      },
      {
        title: '文档',
        link: prefixZH,
      },
      {
        title: '博客',
        link: 'https://www.emqx.com/zh/blog/category/hstream',
      },
      {
        title: '社区',
        link: 'https://hstream.io/zh/community',
      },
    ],
    followList: [
      { img: '/docs-images/github.png', link: 'https://github.com/hstreamdb/hstream/' },
      {
        img: '/docs-images/youtube.png',
        link: 'https://www.youtube.com/channel/UCir_r04HIsLjf2qqyZ4A8Cg',
      },
      { img: '/docs-images/weibo.png', link: 'https://weibo.com/emqtt' },
      { img: '/docs-images/linkedin.png', link: 'https://www.linkedin.com/company/emqtech' },
      { img: '/docs-images/bilibili.png', link: 'https://space.bilibili.com/522222081' },
    ],
  },
  en: {
    column: [
      {
        title: 'GitHub',
        link: 'https://github.com/hstreamdb/hstream/',
      },
      {
        title: 'Docs',
        link: prefixEN,
      },
      {
        title: 'Blog',
        link: 'https://www.emqx.com/en/blog/category/hstream',
      },
      {
        title: 'Community',
        link: 'https://hstream.io/community',
      },
    ],
    followList: [
      { img: '/docs-images/github.png', link: 'https://github.com/hstreamdb/hstream/' },
      { img: '/docs-images/twitter.png', link: 'https://twitter.com/EMQTech' },
      {
        img: '/docs-images/youtube.png',
        link: 'https://www.youtube.com/channel/UC5FjR77ErAxvZENEWzQaO5Q',
      },
      { img: '/docs-images/linkedin.png', link: 'https://www.linkedin.com/company/emqtech' },
    ],
  },
}
