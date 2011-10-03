(ns sdb_gui.core
  (:use (seesaw core mig))
  (:require [sdb_gui.accounts :as accounts])
            ; [sdb_gui.sdb      :as sdb])
  (:require [cemerick.rummage :as sdb])
  (:import (javax.swing JSplitPane))
  (:import (com.explodingpixels.macwidgets SourceList
                                           SourceListModel
                                           SourceListItem
                                           SourceListCategory
                                           SourceListControlBar
                                           SourceListSelectionListener
                                           MacWidgetFactory
                                           MacIcons)))
                                           
(def clients (atom {}))
    
(defn get-client
    "Returns an SDB client if one already exists, otherwise, it creates
     a new client, caches it, and returns the newly created client object."
    [account-name]
    (if-let [client (get @clients account-name)]
        client
        (let [account        (accounts/get-account account-name)
              aws-access-key (:aws-access-key account)
              aws-secret-key (:aws-secret-key account)
              client         (sdb/create-client aws-access-key aws-secret-key)]
            (swap! clients assoc account-name client)
            client)))
            
(defn edit-account-panel []
  (mig-panel
   :constraints ["wrap, center, center"
                 "[shrink, right] 10 [left]"
                 ""]
   :items [["Name"] [(text :id :account-name) "width 300!"]
           ["AWS Access Key"] [(text :id :aws-access-key) "width 300!"]
           ["AWS Secret Key"] [(text :id :aws-secret-key) "width 300!"]
           [(flow-panel
             :hgap 0
             :vgap 0
             :align :right
             :items [(button :text "Cancel" :id :cancel)
                     (button :text "Connect"
                             :id :ok)]) "span"]]))

(defn update-account
  "Add a new AWS account"
  [mainframe]
  (let [panel  (edit-account-panel)
        dialog (custom-dialog :parent mainframe
                              :modal? true
                              :title "Account Info"
                              :content (edit-account-panel))]
    (.setDefaultButton (.getRootPane dialog)
                       (select panel [:#ok]))
    (show! (pack! dialog))))

(defn get-domains 
  "Returns a list of domains associated with the given account."
  [account-name]
  (let [client      (get-client account-name)
        domains     (sdb/list-domains client)
        item-counts (for [domain domains] 
                        (:itemCount (sdb/domain-metadata client domain)))]
    (zipmap domains item-counts)))
    
(defn load-items 
    "Loads the items for the given domain into the items table"
    [domain-name]
    (println (str "The domain selected was " domain-name)))
                        
(defn create-source-list
  "Creates the source list (aka, sidebar tree view) and populates it
   with all of the user's AWS accounts"
  [control-bar]
  (let [category (SourceListCategory. "Accounts")
        model    (doto (SourceListModel.)
                   (.addCategory category))
        accounts (accounts/get-account-names)]
    (doseq [account-name accounts]
        (let [account-item (SourceListItem. account-name)]
            (.addItemToCategory model account-item category)
            (doseq [[domain-name item-count] (get-domains account-name)]
                (let [item (SourceListItem. domain-name)]
                    (.setCounterValue item item-count)
                    (.addItemToItem model item account-item)))))
    (doto (SourceList. model)
      (.installSourceListControlBar control-bar)
      (.setFocusable false)
      (.addSourceListSelectionListener 
          (proxy [SourceListSelectionListener] []
              (sourceListItemSelected [item] 
                  (load-items (.getText item))))))))

(defn create-control-bar
  "Creates the control bar along the bottom of the source list"
  [mainframe]
  (doto (SourceListControlBar.)
    (.createAndAddButton
     MacIcons/PLUS
     (action :handler (fn [e] (update-account mainframe))))
    (.createAndAddButton
     MacIcons/MINUS
     (action :handler (fn [e] (println "Removing an account"))))))

(defn create-content-panel []
  "Creates the main content panel of the application which is a card layout
   panel holding several different views"
  (card-panel
   :id :content-panel
   :items
   [[(flow-panel) :blank]
    [(edit-account-panel) :account]]))

(defn create-split-pane
  "Creates the split pane that holds the main content panel and the source
   list"
  [source-list content-panel]
  (doto (. MacWidgetFactory createSplitPaneForSourceList
           source-list
           content-panel)
    (.setDividerLocation 250)))


(defn create-main-frame []
  (frame
   :title    "SDB Viewer"
   :size     [700 :by 400]
   :on-close :exit))

(defn create-gui []
  (let [mainframe     (create-main-frame)
        control-bar   (create-control-bar mainframe)
        source-list   (create-source-list control-bar)
        content-panel (create-content-panel)
        split-pane    (create-split-pane source-list content-panel)]
    (.installDraggableWidgetOnSplitPane control-bar split-pane)
    (.setContentPane mainframe split-pane)
    mainframe))

(defn -main [& args]
  (show!
   (create-gui)))
