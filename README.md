# quilt

Quilt is a ClojureScript app designed to teach kids how to program.

It lets kids make basic drawings by writing code, either in a visual editor where they select functions and fill in parameters, or in a source editor where they can actually type in the code.

Actually, it's harder to explain than it is to just show you. Give it a whirl at:

http://jmglov.net/quilt/

## Stuff I need to add

In no particular order:

* More shapes!
  * Triangle
* More colours
* Display coordinates on mouseover
* Lo-res mode (chunky pixels) for younger students
* Help text (mouseover?)
* Give decent error messages when eval fails in the source editor
* Nicer styling
* Scripted mode for teaching that will show kids where to click, step by step, to make a drawing
* Cloud load and save so kids can show off their drawings outside of class
* Readonly mode so friends can look but not touch

If there's something you want to see, [open an issue](https://github.com/jmglov/quilt/issues)!

If you just want to chat about teaching programming or Clojure or ClojureScript or whatever, hit me up on Twitter. I'm, as you might expect, [@jmglov](https://twitter.com/jmglov). :)

## Development Mode

### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl "(do (use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

```
lein clean
lein doo phantom test once
```

The above command assumes that you have [phantomjs](https://www.npmjs.com/package/phantomjs) installed. However, please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many other JS environments (chrome, ie, safari, opera, slimer, node, rhino, or nashorn).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
