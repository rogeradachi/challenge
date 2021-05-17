(ns flexiana.api.routes
    (:require [io.pedestal.http :as http]
      [io.pedestal.http.route :as route]
      [flexiana.backend.scramble :as service]))

(defn http-200-response [body]
      {:status 200 :body body})

(defn http-502-response [message]
      {:status 502 :body message})

(defn validate-request-params [{{:keys [pieces puzzle]}     :query-params}]
      (-> (or (empty? pieces) (empty? puzzle))
          not))

(defn bad-request [{{:keys [pieces puzzle]}     :query-params}]
      (cond
        (and (empty? pieces) (empty? puzzle)) (http-502-response (str "pieces and puzzle cannot by empty\n"))
        (empty? pieces) (http-502-response (str "pieces cannot by empty\n"))
        :else (http-502-response (str "puzzle cannot by empty\n"))))

(defn process-request [pieces puzzle]
      (if (service/scramble? pieces puzzle)
        "Possible"
        "Not Possible"))

(defn scramble-challenge [{{:keys [pieces puzzle]}     :query-params
                            :as                         request}]
      (if (validate-request-params request)
        (http-200-response (process-request pieces puzzle))
        (bad-request request)))

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