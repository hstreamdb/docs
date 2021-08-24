const version = process.env.VERSION || 'latest'
const docsBranch = process.env.DOCSBRANCH || 'master'

module.exports = {
  'zh-CN': {
    docName: 'HStreamDB 文档',
    docType: 'hstream',
    editLinkText: '编辑此页',
    feedbackLinkText: '反馈问题',
    docsRepo: 'hstreamdb/docs',
    docsBranch,
    docsDir: 'docs/zh',
    version,
  },
  en: {
    docName: 'HStreamDB Docs',
    docType: 'hstream',
    editLinkText: 'Edit this page',
    feedbackLinkText: 'Request docs changes',
    docsRepo: 'hstreamdb/docs',
    docsBranch,
    docsDir: 'docs/en',
    version,
  },
}
