(ns quilt.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [quilt.core-test]))

(doo-tests 'quilt.core-test)
