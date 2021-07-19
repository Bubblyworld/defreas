[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<p align="center">
  <a href="https://github.com/Bubblyworld/defreas">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">DefReas>

  <p align="center">
    A platform for reasoning over defeasible knowledge bases, brought to you by <a href="https://www.cair.org.za/about">CAIR</a>.
    <br />
    <a href="https://github.com/Bubblyworld/defreas">View Demo</a>
    ·
    <a href="https://github.com/Bubblyworld/defreas/issues">Report Bug</a>
    ·
    <a href="https://github.com/Bubblyworld/defreas/issues">Request Feature</a>
  </p>
</p>

<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About The Project
DefReas is a web-based tool for defining knowledge bases in a variety of defeasible logics and running reasoning tasks over them. Currently the project is in the prototyping phase, and is largely unusable.

### Built With
* [Scala](https://scala-lang.org/)
* [Scala Build Tool](https://www.scala-sbt.org/index.html)
* [Scala Parser Combinators](https://github.com/scala/scala-parser-combinators)
* [HTTP4S Http Server](https://http4s.org/)
* [LSP4J Language Server](https://github.com/eclipse/lsp4j)
* [CodeMirror Embedded Editor](https://codemirror.net)
* [CodeMirror Language Server Plugin](https://github.com/wylieconlon/lsp-editor-adapter)

## Getting Started
DefReas has the following moving parts:
1. A HTTP server for serving the web interface.
2. An API server for running reasoning tasks and compilation checks.
3. An LSP server for autocompletion and error messages in the embedded web editor.

The servers are implemented in Scala using the SBT build tool, with the LSP server run behind [a reverse proxy](https://github.com/wylieconlon/jsonrpc-ws-proxy). The web interface is a simple javascript application containing an embedded CodeMirror editor.

### Prerequisites
You'll need the following software installed to run a DefReas instance.
* [npm](https://www.npmjs.com/)
* [scalac](https://scala-lang.org)
* [sbt](https://www.scala-sbt.org/index.html)

### Installation
1. Clone the repo:
   ```sh
   git clone https://github.com/Bubblyworld/defreas.git && cd defreas
   ```
2. Install sbt dependencies:
   ```
   sbt compile
   ```
3. Install npm dependencies:
   ```sh
   cd web && npm install && cd -
   ```

## Usage
To use DefReas, you'll need to run two separate processes:
1. Run the HTTP server:
   ```sh
   sbt "run web"
   ```
2. Run the language server proxy:
   ```sh
   cd web && npm run deploy-lsp-proxy
   ```
   
The web interface can now be accessed at https://localhost:8080.

## Roadmap
* Support defeasible KLM, ALC and Datalog knowledge bases.
* Support satisfaction checking and entailment under well-known entailment relations like Rational Closure.

## License
Distributed under the MIT License.

## Contact
Guy Paterson-Jones - email@example.com
Project Link: [https://github.com/your_username/repo_name](https://github.com/your_username/repo_name)

<!-- Link variables -->
[contributors-shield]: https://img.shields.io/github/contributors/Bubblyworld/defreas.svg?style=for-the-badge
[contributors-url]: https://github.com/Bubblyworld/defreas/graphs/contributors
[issues-shield]: https://img.shields.io/github/issues/Bubblyworld/defreas.svg?style=for-the-badge
[issues-url]: https://github.com/Bubblyworld/defreas/issues
[license-shield]: https://img.shields.io/github/license/Bubblyworld/defreas.svg?style=for-the-badge
[license-url]: https://github.com/Bubblyworld/defreas/blob/master/LICENSE.txt
[product-screenshot]: images/screenshot.png
