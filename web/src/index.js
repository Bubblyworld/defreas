// Codemirror embedded editor.
import * as CodeMirror from 'codemirror';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint';

// Language server plugin for Codemirror.
import 'lsp-editor-adapter/lib/codemirror-lsp.css';
import { LspWsConnection, CodeMirrorAdapter } from 'lsp-editor-adapter';

// CSS selector for the editor html component.
const editorSelector = '#editor';

let editor = CodeMirror(
  document.querySelector(editorSelector),
  {
    value: 'hello world',
    gutters: ['CodeMirror-lsp']
  }
);

