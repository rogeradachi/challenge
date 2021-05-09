(ns flexiana.backend.scramble_test
  (:require [flexiana.backend.scramble :as scr]
            [clojure.test :refer [deftest is testing]]))

(deftest proposed-scenarios
  (testing "First scenario"
    (let [puzzle "rekqodlw"
          compose "world"]
      (is (true? (scr/scramble? puzzle compose)))))

  (testing "Second scenario"
    (let [puzzle "cedewaraaossoqqyt"
          compose "codewars"]
      (is (true? (scr/scramble? puzzle compose)))))

  (testing "Last scenario"
    (let [puzzle "katas"
          compose "steak"]
      (is (false? (scr/scramble? puzzle compose))))))