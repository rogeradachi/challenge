(ns flexiana.api.routes-test
    (:require
      [clojure.test :refer :all]
      [io.pedestal.test :refer :all]
      [io.pedestal.http :as http]
      [io.pedestal.http.route :as route]
      [flexiana.api.routes :as s]))

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet s/service-map)))

(def url-for
  "Test url generator."
  (route/url-for-routes (route/expand-routes s/routes)))

(deftest service-test
         (testing "Hello world"
                  (is (= 200
                         (:status (response-for service :get "/hello")))))
         (testing "Challenge success"
                  (let [response (response-for service :get (url-for :scramble
                                                                     :query-params {:pieces "ASC" :puzzle "A"}))]
                       (is (= 200
                              (:status response)))

                       (is (= "Possible"
                              (:body response)))))

         (testing "Challenge fail"
                  (let [response (response-for service :get (url-for :scramble
                                                                     :query-params {:pieces "ASC" :puzzle "D"}))]
                       (is (= 200
                              (:status response)))

                       (is (= "Not Possible"
                              (:body response)))))

         (testing "Empty query params"
                  (let [response (response-for service :get (url-for :scramble
                                                                     :query-params {:pieces "" :puzzle "D"}))]
                       (is (= 502
                              (:status response)))

                       (is (= "pieces cannot by empty"
                              (:body response))))

                  (let [response (response-for service :get (url-for :scramble
                                                                     :query-params {:pieces "AFDS" :puzzle ""}))]
                       (is (= 502
                              (:status response)))

                       (is (= "puzzle cannot by empty"
                              (:body response))))

                  (let [response (response-for service :get (url-for :scramble
                                                                     :query-params {:pieces "" :puzzle ""}))]
                       (is (= 502
                              (:status response)))

                       (is (= "pieces and puzzle cannot by empty"
                              (:body response))))))