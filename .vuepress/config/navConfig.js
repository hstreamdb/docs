const version = process.env.VERSION
const prefixEN = `/docs/en/${version ? `${version}/` : ''}`
const prefixZH = `/docs/zh/${version ? `${version}/` : ''}`

module.exports = {
  zh: [
    { text: '文档', link: prefixZH, target: '_self', rel: '' },
    { text: '博客', link: 'https://www.emqx.com/zh/blog/category/hstream' },
    { text: '社区', link: 'https://hstream.io/zh/community', target: '_self' },
  ],
  en: [
    { text: 'Docs', link: prefixEN, target: '_self', rel: '' },
    { text: 'Blog', link: 'https://www.emqx.com/en/blog/category/hstream' },
    { text: 'Community', link: 'https://hstream.io/community', target: '_self' },
  ],
}
