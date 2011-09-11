(ns sdb_gui.sdb
  (:require [cemerick.rummage :as sdb]))

(.setLevel (java.util.logging.Logger/getLogger "com.amazonaws")
           java.util.logging.Level/WARNING)

(def aws-access-key "AKIAILR6IMPTLBPTT2YA")
(def aws-secret-key "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")
(def client (sdb/create-client aws-access-key aws-secret-key))