(ns sdb_gui.sdb
    (:import (com.amazonaws.services.simpledb AmazonSimpleDBClient))
    (:import (com.amazonaws.auth BasicAWSCredentials))
    (:import (com.amazonaws.services.simpledb.model PutAttributesRequest
                                                    GetAttributesRequest
                                                    ReplaceableAttribute)))

(.setLevel (java.util.logging.Logger/getLogger "com.amazonaws")
           java.util.logging.Level/WARNING)

(defn create-client
   "Creates an instance of an AmazonSimpleDBClient with the given credentials"
   [aws-access-key aws-secret-key]
   (AmazonSimpleDBClient. (BasicAWSCredentials. aws-access-key aws-secret-key)))

(defn list-domains
   "Returns a seq of domains associated with the given client"
   [client]
   (.getDomainNames (.listDomains client)))

(defn put-attributes
   ""
   [client domain-name item-name attrs]
   (let [attributes (for [[k v] attrs] (ReplaceableAttribute. (name k) v false))
         request    (PutAttributesRequest. domain-name item-name attributes)]
       (.putAttributes client request)))

(defn get-attributes
   ""
   [client domain-name item-name]
   (let [request (GetAttributesRequest. domain-name item-name)]
      (for [attribute (.getAttributes (.getAttributes client request))]
           [(.getName attribute) (.getValue attribute)])))


