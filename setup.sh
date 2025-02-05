#!/usr/bin/env bash
cd slides
git clone https://github.com/hakimel/reveal.js --depth 1 --branch 5.1.0
rm -rf reveal.js/.git
git clone https://github.com/denehyg/reveal.js-menu --depth 1 --branch 2.1.0 reveal.js/plugin/menu
rm -rf reveal.js/plugin/menu/.git
cd reveal.js && npm i && npm run build && cd ../
mkdir highlight.js
curl -sLo highlight.js/highlight.min.js https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.7.3/highlight.min.js
curl -sLo highlight.js/atom-one-light.min.css https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.7.3/styles/atom-one-light.min.css