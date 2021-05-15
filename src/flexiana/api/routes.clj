(ns flexiana.api.routes
    (:require [io.pedestal.http :as http]
      [io.pedestal.http.route :as route]
      [flexiana.backend.scramble :as service]))

(defn ok [body]
      {:status 200 :body body})

(defn bad-request [message]
      {:status 502 :body message})

(defn validate-request [{:keys [pieces puzzle]}]
      (cond
        (and (empty? pieces) (empty? puzzle)) (bad-request (str "pieces and puzzle cannot by empty\n"))
        (empty? pieces) (bad-request (str "pieces cannot by empty\n"))
        :else (bad-request (str "puzzle cannot by empty\n"))))

(defn process-request [pieces puzzle]
      (cond
        (or (empty? pieces) (empty? puzzle)) nil
        (service/scramble? pieces puzzle) "Its possible"
        :else "Not Possible"))

(defn respond-hello [request]
      (let [pieces (get-in request [:query-params :pieces])
            puzzle (get-in request [:query-params :puzzle])
            input {:pieces pieces :puzzle puzzle}
            resp (process-request pieces puzzle)]
           (if resp
             (ok resp)
             (validate-request input))))

(def routes
  (route/expand-routes
    #{["/greet" :get respond-hello :route-name :greet]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})

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