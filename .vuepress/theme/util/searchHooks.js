export default {
  async processSuggestions(suggestions, queryString, queryTerms) {
    if (queryString) {
      // add a suggestion to start a search in an external service
      const lang = location.pathname.indexOf('/zh/') !== -1 ? 'zh' : 'en'
      if (lang === 'zh') {
        suggestions = suggestions.filter((item) => {
          return item.path.indexOf('/zh/') !== -1
        })
      } else {
        suggestions = suggestions.filter((item) => {
          return item.path.indexOf('/zh/') === -1
        })
      }
    }
    return suggestions
  },

  async onGoToSuggestion(index, suggestion, queryString, queryTerms) {
    // e.g. create an analytics event

    // return true if you want to prevent default navigation
    return true
  },
}
