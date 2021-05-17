(ns flexiana.api.routes
    (:require [io.pedestal.http :as http]
      [io.pedestal.http.route :as route]
      [flexiana.backend.scramble :as service]))

(def ok 200)
(def bad-request 502)

(defn http-response [status-code body]
      {:status status-code :body body})

(defn validate-request-params [{{:keys [pieces puzzle]}     :query-params}]
      (-> (or (empty? pieces) (empty? puzzle))
          not))

(defn bad-request-handler [{{:keys [pieces puzzle]}     :query-params}]
      (cond
        (and (empty? pieces) (empty? puzzle)) (http-response bad-request (str "pieces and puzzle cannot by empty\n"))
        (empty? pieces) (http-response bad-request  (str "pieces cannot by empty\n"))
        :else (http-response bad-request  (str "puzzle cannot by empty\n"))))

(defn process-request [pieces puzzle]
      (if (service/scramble? pieces puzzle)
        "Possible"
        "Not Possible"))

(defn scramble-challenge [{{:keys [pieces puzzle]}     :query-params
                            :as                         request}]
      (if (validate-request-params request)
        (http-response ok (process-request pieces puzzle))
        (bad-request-handler request)))

(def routes
  (route/expand-routes
    #{["/greet" :get scramble-challenge :route-name :greet]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890
   ::http/allowed-origins ["http://localhost:3449"]})

(defn start []
      (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
      (reset! server
              (http/start (http/create-server
                            (assoc service-map
                                   ::http/join? false)))))

(defn stop-dev []
      (http/stop @server))

(defn restart []
      (stop-dev)
      (start-dev))