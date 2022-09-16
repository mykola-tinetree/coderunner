(ns coderunner
  (:require [clojure.java.shell :refer [sh]])
  (:gen-class
    :methods [^:static [handler [String] String]])
  )

(defn- execute-clojure
  [code context]
  )

(defn- execute-python
  [code {:keys [prefix indentation suffix print] :or {indentation 0}}]
  (try
    (sh "python3" :in (str (when prefix (clojure.string/join "\n" (map str (if (vector? prefix) prefix [prefix]))))
                           (clojure.string/join
                             (map
                               #(apply str (concat ["\n"] (repeat indentation "\t") [%]))
                               (clojure.string/split-lines code)))
                           (when suffix
                             (clojure.string/join
                               (map (partial str "\n") (if (vector? suffix) suffix [suffix]))))
                           (when print
                             (clojure.string/join
                               (map
                                 #(str "\nprint(" (clojure.string/escape % char-escape-string) ")")
                                 (if (vector? print) print [print]))))))
    (catch Exception _ nil))
  )

(defn execute
  [{:keys [language code context]}]
  ((case language :clojure execute-clojure :python execute-python) code context)
  )

(defn -handler [s]
  (-> s read-string execute str)
  )
