(ns music-as-data.web
  (:require [music-as-data.core :as c]
            [net.cgrand.enlive-html :as html])
  (:use [net.cgrand.moustache :only [app]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.params :only [wrap-params]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]]
        [ring.util.response :only [response file-response redirect not-found]]
        [ring.adapter.jetty :only [run-jetty]]))

(html/deftemplate plain "plain.html"
  [{:keys [error trace status composition]}]
  [:#i_composition] (html/content composition)
  [:#l_composition] (if (and (:playing status) composition)
                      (html/content composition)
                      (html/substitute ""))
  [:#i_error] (if error (html/content error) (html/substitute ""))
  [:#i_trace] (if trace (html/content (clojure.string/join "\n" trace))
                  (html/substitute ""))
  )

(defn show
  [item]
   (let [item (if (string? item)
                {:composition
                 (str ((keyword item) c/playlist))}
                item)]
    (fn [req] (response (plain item)))))

(defn process [{{:keys [composition]} :params :as req}]
  (let [res {:composition composition}
        res (try
              (do (c/play (read-string composition)) (assoc res :status :playing))
              (catch Throwable t (assoc res
                                   :error (.getMessage t)
                                   :trace (.getStackTrace t))))]
      (println (str "After Processing composition : " res))
    ((show res) req)))

(def routes
  (app (wrap-file "resources")
       (wrap-params)
       (wrap-keyword-params)
       [""] {:post process :get (show nil)}
       ["stop" &] (fn [req] (c/stop) (redirect "/"))
       [item &] (show item)
       [&] (constantly (redirect "/"))))

(defn start [ & [port & options]]
  (run-jetty (var routes) {:port (or port 8080) :join? false}))

(defn -main []
  (start))
