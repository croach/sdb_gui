(ns sdb_gui.gui
  (:use (seesaw core mig))
  (:use (sdb_gui accounts))
  (:import (javax.swing JSplitPane))
  (:import (com.explodingpixels.macwidgets SourceList
                                           SourceListModel
                                           SourceListItem
                                           SourceListCategory
                                           SourceListControlBar
                                           MacWidgetFactory
                                           MacIcons)))

(defn create-source-list []
  (let [category (SourceListCategory. "Accounts")
        model    (doto (SourceListModel.)
                   (.addCategory category))
        accounts (get-account-names)]
    (doseq [account-name accounts]
      (.addItemToCategory model (SourceListItem. account-name) category))
    (SourceList. model)))

(defn edit-account-panel []
  (mig-panel
   :constraints ["wrap, center, center"
                 "[shrink, right] 10 [left]"
                 ""]
   :items [["Name"] [(text :id :account-name) "width 300!"]
           ["AWS Access Key"] [(text :id :aws-access-key) "width 300!"]
           ["AWS Secret Key"] [(text :id :aws-secret-key) "width 300!"]]))

(defn create-gui []
  (let [control-bar (SourceListControlBar.)
        source-list (create-source-list)
        split-pane  (. MacWidgetFactory createSplitPaneForSourceList
                       source-list
                       (edit-account-panel))
        mainframe   (frame
                     :title    "SDB Viewer"
                     :content  split-pane
                     :size     [700 :by 400]
                     :on-close :exit)]
    (doto control-bar
      (.createAndAddButton
       MacIcons/PLUS (action
                      :handler (fn [e] (println "Adding an account"))))
      (.createAndAddButton MacIcons/MINUS nil)
      (.installDraggableWidgetOnSplitPane split-pane))
    (.installSourceListControlBar source-list control-bar)
    (.setDividerLocation split-pane 250)
    (show! mainframe)))