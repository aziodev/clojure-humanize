(ns clojure.contrib.humanize-test
  (:require [clojure.test :refer :all]
            [clojure.contrib.humanize :refer :all]
            [clojure.math.numeric-tower :refer :all]))

(deftest intcomma-test
  (testing "Testing intcomma function with expected data."
    (doseq [[testnum result] [[100, "100"], [1000, "1,000"],
                              [10123, "10,123"], [10311, "10,311"],
                              [1000000, "1,000,000"], [-100, "-100"],
                              [-10123 "-10,123"], [-10311 "-10,311"],
                              [-1000000, "-1,000,000"]]]
      (is (= (intcomma testnum) result)))))

(deftest ordinal-test
  (testing "Testing ordinal function with expected data."
    (doseq [[testnum result] [[1,"1st"], [ 2,"2nd"],
                              [ 3,"3rd"], [ 4,"4th"],
                              [ 11,"11th"],[ 12,"12th"],
                              [ 13,"13th"], [ 101,"101st"],
                              [ 102,"102nd"], [ 103,"103rd"],
                              [111, "111th"]]]
      (is (= (ordinal testnum) result)))))

(deftest intword-test
  (testing "Testing intword function with expected data."
    (doseq [[testnum result format] [[100 "100.0"]
                                     [ 1000000 "1.0 million"]
                                     [ 1200000 "1.2 million"]
                                     [ 1290000 "1.3 million"]
                                     [ 1000000000 "1.0 billion"]
                                     [ 2000000000  "2.0 billion"]
                                     [ 6000000000000 "6.0 trillion"]
                                     [1300000000000000 "1.3 quadrillion"]
                                     [3500000000000000000000 "3.5 sextillion"]
                                     [8100000000000000000000000000000000 "8.1 decillion"]
                                     [1230000 "1.23 million" "%.2f"]
                                     [(expt 10 101) "10.0 googol"]
                                     ]]
      ;; default argument
      (let [format (if (nil? format) "%.1f" format)]
        (is (= (intword testnum
                        :format format
                        )
               result))))))

(deftest filesize-test
  (testing "Testing filesize function with expected data."
    (doseq [[testsize result binary format] [[300, "300.0B"]
                                             [3000, "3.0KB"]
                                             [3000000, "3.0MB"]
                                             [3000000000, "3.0GB"]
                                             [3000000000000, "3.0TB"]
                                             [3000, "2.9KiB", true]
                                             [3000000, "2.9MiB", true]
                                             [(* (expt 10 26) 30), "3000.0YB"]
                                             [(* (expt 10 26) 30), "2481.5YiB", true]

                                             ]]
      ;; default argument
      (let [binary (boolean binary)
            format (if (nil? format) "%.1f" format)]
        (is (= (filesize testsize
                         :binary binary
                         :format format
                         )
               result))))))

(deftest truncate-test
  (testing "truncate should not return a string larger than give length."
    (let [string "asdfghjkl" ]
      (is (= (count (truncate string 7)) 7))
      (is (= (count (truncate string 7 "1234")) 7))
      (is (= (count (truncate string 100)) (count string)))))

  (testing "testing truncate with expected data."
    (let [string "abcdefghijklmnopqrstuvwxyz"]
      (is (= (truncate string 14) "abcdefghijk..."))
      (is (= (truncate string 14 "...kidding") "abcd...kidding")))))

(deftest oxford-test
  (let [items ["apple", "orange", "banana", "pear", "pineapple", "strawberry"]]
    (testing "should return an empty string when given an empty list."
      (is (= (oxford []) "")))

    (testing "should return a string version of a list that has only one value."
      (is (= (oxford [(items 0)]) (items 0))))

    (testing "should return items separated by `and' when given a list of values"
      (is (= (oxford (take 2 items)) (str (items 0) " and " (items 1))))
      (is (= (oxford (take 3 items)) (str (items 0) ", "
                                          (items 1) " and " (items 2))))
      (is (= (oxford (take 4 items)) (str (items 0) ", "
                                          (items 1) ", "
                                          (items 2) " and " (items 3)))))

    (testing "should truncate a large list of items with proper pluralization"
      (is (= (oxford (take 5 items)) (str (items 0) ", "
                                          (items 1) ", "
                                          (items 2) ", "
                                          (items 3) " and " 1 " other")))
      (is (= (oxford (take 5 items)
                     :maximum-display 2)
             (str (items 0) ", "
                  (items 1) " and " 3 " others"))))

    (testing "should accept custom trucation strings"
      (let [truncate-noun "fruit"]
        (is (oxford (take 5 items)
                    :truncate-noun truncate-noun)
            (str (items 0) ", "
                 (items 1) ", "
                 (items 2) " and " 2 " other " (pluralize 2 truncate-noun)))
        (is (oxford (take 3 items)
                    :truncate-string truncate-noun)
            (str (items 0) ", "
                 (items 1) " and " (items 2)))))))
