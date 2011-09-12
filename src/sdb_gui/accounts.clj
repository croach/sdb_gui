(ns sdb_gui.preferences
  (:import '(java.util.prefs Preferences)))

(def root     (.node (Preferences/userRoot) "sdb_gui"))
(def accounts (.node root "accounts"))

(defn get-account-names
  "Returns a vector of all AWS account names associated with the current user."
  []
  (vec (.childrenNames accounts)))

(defn get-account
  "Returns a single account as a map"
  [account-name]
  (if (.nodeExists accounts account-name)
    (let [account (.node accounts account-name)
          items   #(for [k (.keys %)] [(keyword k) (.get % k nil)])]
      (-> (->> account
               items                     ;; extract [k v] pairs from account
               flatten                   ;; flatten pairs into a single list
               (apply hash-map))         ;; turn flattened list into a map
          (assoc :name account-name))))) ;; add the account name to the map

(defn get-accounts
  "Returns a list of all accounts (as maps) for the current user"
  []
  (for [account-name (get-account-names)] (get-account account-name)))

(defn add-account
  "Adds a new account for the current user with the given info"
  [account-name & attrs]
  (let [account (.node accounts account-name)
        info    (apply hash-map attrs)]
    (doseq [[k v] info]
      (.put account (name k) v))))

(defn remove-account
  "Removes the given account from the current user's preferences."
  [account-name]
  (let [account (.node accounts account-name)]
    (.removeNode account)))