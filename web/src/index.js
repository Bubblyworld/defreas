// Codemirror embedded editor.
import * as CodeMirror from 'codemirror';
import 'codemirror/lib/codemirror.css';
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
    value: 'A & B',
    gutters: ['CodeMirror-lsp'],
    lineNumbers: true,
  }
);

// Configuration for the language server proxy.
let connectionOptions = {
  // LSP proxy, can be run with npm scripts from the 'web' folder.
  serverUri: 'ws://localhost:8081/defreas',
  documentText: () => editor.getValue(),
  languageId: 'defreas',

  // Document URIs for our language server, currently irrelevant as it uses
  // a virtual file system for editing, different one per process.
  rootUri: 'file:///defreas/web',
  documentUri: 'file:///defreas/web/knowledge_base.defreas',
};

// Connection to the language server proxy.
let conn = new LspWsConnection(connectionOptions)
  .connect(new WebSocket(connectionOptions.serverUri));

// Configuration on the underlying editor.
let adapter = new CodeMirrorAdapter(conn, {
  quickSuggestionsDelay: 100,
}, editor);
