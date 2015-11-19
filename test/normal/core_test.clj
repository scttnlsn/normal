(ns normal.core-test
  (:require [normal.core :as n]
            [clojure.test :refer :all]))

(def User
  {:entity :users})

(def Comment
  {:entity :comments})

(def Post
  {:entity :posts
   :relationships {:author User
                   :comments [Comment]}})

(def user1 {:id 1
            :name "Foo"
            :email "foo@example.com"})

(def user2 {:id 2
            :name "Bar"
            :email "bar@example.com"})

(def comment1 {:id 11
               :body "This is comment A"})

(def comment2 {:id 22
               :body "This is comment B"})

(def post1 {:id 111
            :title "Some Post"
            :author user1
            :comments [comment1 comment2]})

(def post2 {:id 222
            :title "Another Post"
            :author user2})

(deftest normalize
  (let [[id entities] (n/normalize User user1)]
    (is (= id 1))
    (is (= entities {:users {1 user1}}))))

(deftest normalize-seq
  (let [[ids entities] (n/normalize [User] [user1 user2])]
    (is (= ids [1 2]))
    (is (= entities {:users {1 user1 2 user2}}))))

(deftest normalize-duplicates
  (let [[ids entities] (n/normalize [User] [user1 user1])]
    (is (= ids [1 1]))
    (is (= entities {:users {1 user1}}))))

(deftest normalize-nested
  (let [[ids entities] (n/normalize [Post] [post1 post2])]
    (is (= ids [111 222]))
    (is (= entities {:posts {111 (-> post1
                                     (assoc :comments [11 22])
                                     (assoc :author 1))
                             222 (-> post2
                                     (assoc :author 2))}
                     :comments {11 comment1
                                22 comment2}
                     :users {1 user1
                             2 user2}}))))

(deftest normalize-id
  (let [[id entities] (n/normalize {:entity :tests
                                    :id (fn [data] [(:foo data) (:baz data)])}
                                   {:foo :bar :baz :qux})]
    (is (= id [:bar :qux]))
    (is (= entities {:tests {[:bar :qux] {:foo :bar :baz :qux}}}))))
