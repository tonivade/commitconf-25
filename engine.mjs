// engine.mjs (ES modules)
import markdownItHighlightLines from 'markdown-it-highlight-lines'

export default ({ marp }) => marp.use(markdownItHighlightLines)