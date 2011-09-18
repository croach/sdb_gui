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
                                           MacIcons))
    (:gen-class))

(def mainframe (frame
                :title    "SDB Viewer"
                :size     [700 :by 400]
                :on-close :exit))


(defn create-source-list []
  (let [category (SourceListCategory. "Accounts")
        model    (doto (SourceListModel.)
                   (.addCategory category))
        accounts (accounts/get-account-names)]
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
           ["AWS Secret Key"] [(text :id :aws-secret-key) "width 300!"]
           [(flow-panel
             :hgap 0
             :vgap 0
             :align :right
             :items [(button :text "Cancel")
                     (button :text "Connect")]) "span"]]))

(defn update-account
     "Add a new AWS account"
  []
  (let [content-panel (select mainframe [:#content-panel])]
    (show-card! content-panel :account)))


(defn create-control-bar []
  (doto (SourceListControlBar.)
    (.createAndAddButton
     MacIcons/PLUS
     (action :handler (fn [e] (update-account))))
    (.createAndAddButton
     MacIcons/MINUS
     (action :handler (fn [e] (println "Removing an account"))))))

(defn create-gui [mainframe]
  (let [control-bar (create-control-bar)
        source-list (create-source-list)
        split-pane  (. MacWidgetFactory createSplitPaneForSourceList
                       source-list
                       (card-panel
                        :id :content-panel
                        :items
                        [[(flow-panel) :blank]
                         [(edit-account-panel) :account]]))]
    (.installDraggableWidgetOnSplitPane control-bar split-pane)
    (.installSourceListControlBar source-list control-bar)
    (.setDividerLocation split-pane 250)
    (.setContentPane mainframe split-pane)
    mainframe))


(defn -main [& args]
  (invoke-later
   (show!
    (create-gui mainframe))))