(ns caas.core-test
  (:use midje.sweet)
  (:use [caas.core]))


(facts "a request to /authenticate authenticates a user and returns a token"
  (let [handler (ANY "/authenticate" authenticate-user)
         response (handler (request :get "/"))]
         response => OK
         response => (body "Hello World!")
         response => (content-type "text/plain;charset=UTF-8")
         ))
  (fact "it normally returns the first element"
    (first-element [1 2 3] :default) => 1
    (first-element '(1 2 3) :default) => 1)

  ;; I'm a little unsure how Clojure types map onto the Lisp I'm used to.
  (fact "default value is returned for empty sequences"
    (first-element [] :default) => :default
    (first-element '() :default) => :default
    (first-element nil :default) => :default
    (first-element (filter even? [1 3 5]) :default) => :default))
