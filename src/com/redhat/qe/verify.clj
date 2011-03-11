(ns com.redhat.qe.verify
  (:require [clojure.contrib.logging :as log])
  (:use [clojure.string :only [split]])
  (:import [java.util.logging Logger Level]
           [com.redhat.qe.auto.testng LogMessageUtil$Style]))

(defn ^{:private true} local-bindings
  "Produces a map of the names of local bindings to their values."
  [env]
  (let [symbols (map key env)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))


(defmacro verify
  "Evaluates expr and either logs what was evaluated, or throws an exception if it does not evaluate to logical true."
  [x]
  (let [bindings (local-bindings &env)]
    `(let [noerr# (atom true)
           res# (try ~x (catch Exception e# (do (reset! noerr# false) e#)))
           sep#  (System/getProperty "line.separator")
           form# '~x
           loc# (-> (Thread/currentThread) .getStackTrace second .getClassName (split #"\$"))
           clazz# (str *ns*)
           used-bindings# (select-keys ~bindings (distinct (flatten form#)))
           msg# (apply str (if (and @noerr# res#) "Verified: " "Verification failed: ") (pr-str form#) sep#
                       (map (fn [[k# v#]] (str "\t" k# " : " v# sep#)) 
                            used-bindings#))]
       (if (and @noerr# res#) (.logp (Logger/getLogger (first loc#))
                                    (Level/INFO)
                                    (first loc#)
                                    (second loc#)
                                    msg#
                                    (LogMessageUtil$Style/Asserted))
           (let [err# (AssertionError. msg#)]
             (throw (if (and res# (not @noerr#))
                      (.initCause err# res#)
                      err#)))))))
