(ns normal.core)

(defn- zip
  "Merges corresponding (positional) elements from each
  sequential collection.

  Example:

    (zip [1 2] [3 4] [5 6]) => ([1 3 5] [2 4 6])
  "
  [& colls]
  (apply map vector colls))

(defn- zip-reduce
  "Applies the given reducing functions to the zipped
  collections in the order given.

  fs - reducing functions
  colls - collections with same cardinality as fs

  Example:

    (zip-reduce [+ *] [1 2] [3 4] [5 6]) => [9 48]
  "
  [fs & colls]
  (vec (map #(apply apply %)
            (zip fs (apply zip colls)))))

(defn- deep-merge
  "Recursively merge the given maps."
  [& maps]
  (if (every? map? maps)
    (apply merge-with deep-merge maps)
    (last maps)))

(declare normalize)

(defn- normalize-seq [schema coll]
  (reduce (partial zip-reduce [conj deep-merge])
          [[] {}]
          (map (partial normalize schema) coll)))

(defn- normalize-map [schema data]
  (let [{:keys [entity id relationships] :or {id :id relationships {}}} schema
        id-val (id data)]
    [id-val (reduce (fn [entities [key val]]
                      (let [[result child-entities] (normalize (get relationships key) val)]
                        (deep-merge (assoc-in entities [entity id-val key] result)
                                    child-entities)))
                    {}
                    data)]))

(defn normalize [schema data]
  (if schema
    (if (sequential? schema)
      (normalize-seq (first schema) data)
      (normalize-map schema data))
    [data {}]))
