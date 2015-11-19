# normal

[![Build Status](https://travis-ci.org/scttnlsn/normal.svg)](https://travis-ci.org/scttnlsn/normal)

Normalize nested data according to a relationship schema.

## Install

Add the following dependency to your `project.clj` file:

```clojure
[normal "0.1.0"]
```

## Example

```clojure
(ns example
  (:require [normal.core :as n]))

;; define schema

(def User
  {:entity :users})

(def Comment
  {:entity :comments})

(def Post
  {:entity :posts
   :relationships {:author User
                   :comments [Comment]}})

;; nested data

(def posts [{:id "p1"
             :title "Some Post"
             :author {:id "u1"
                      :name "Scott"
                      :email "scott@scottnelson.co"}
             :comments [{:id "c1"
                         :text "Lorem ipsum"}
                        {:id "c2"
                         :text "Dolor sit amet"}]}
            {:id "p2"
             :title "Another Post"
             :author {:id "u1"
                      :name "Scott"
                      :email "scott@scottnelson.co"}}])

;; normalize

(n/normalize [Post] posts)
```

The `normalize` function will return the following data:

```clojure
[["p1" "p2"] {:comments {"c1" {:id "c1"
                               :text "Lorem ipsum"}
                         "c2" {:id "c2"
                               :text "Dolor sit amet"}}
              :posts {"p1" {:id "p1"
                            :title "Some Post"
                            :author "u1"
                            :comments ["c1" "c2"]}
                      "p2" {:id "p2"
                            :title "Another Post"
                            :author "u1"}}
              :users {"u1" {:id "u1"
                            :name "Scott"
                            :email "scott@scottnelson.co"}}}]
```
