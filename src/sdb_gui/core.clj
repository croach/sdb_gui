(ns sdb_gui.core
  (:use (seesaw core mig))
  (:require [sdb_gui.accounts :as accounts]
            [sdb_gui.sdb :as sdb])
  (:import (javax.swing JSplitPane))
  (:import (com.explodingpixels.macwidgets SourceList
                                           SourceListModel
                                           SourceListItem
                                           SourceListCategory
                                           SourceListControlBar
                                           MacWidgetFactory
                                           MacIcons)))

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
             :items [(button :text "Cancel")
                     (button :text "Connect")]) "span"]]))

(defn update-account
  "Add a new AWS account"
  [mainframe]
  (let [content-panel (select mainframe [:#content-panel])]
    (show-card! content-panel :account)))


(defn create-source-list
  [control-bar]
  (let [category (SourceListCategory. "Accounts")
        model    (doto (SourceListModel.)
                   (.addCategory category))
        accounts (accounts/get-account-names)]
    (doseq [account-name accounts]
      (.addItemToCategory model (SourceListItem. account-name) category))
    (doto (SourceList. model)
      (.installSourceListControlBar control-bar))))

(defn create-control-bar
  [mainframe]
  (doto (SourceListControlBar.)
    (.createAndAddButton
     MacIcons/PLUS
     (action :handler (fn [e] (update-account mainframe))))
    (.createAndAddButton
     MacIcons/MINUS
     (action :handler (fn [e] (println "Removing an account"))))))

(defn create-content-panel []
  (card-panel
   :id :content-panel
   :items
   [[(flow-panel) :blank]
    [(edit-account-panel) :account]]))

(defn create-split-pane
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