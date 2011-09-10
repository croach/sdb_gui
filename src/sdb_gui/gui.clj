(ns sdb_gui.gui
  (:import (javax.swing JFrame JSplitPane))
  (:import (javax.swing JTextArea))
  (:import (java.awt BorderLayout))
  (:import (com.explodingpixels.macwidgets SourceList
                                           SourceListModel
                                           SourceListItem
                                           SourceListCategory
                                           SourceListControlBar
                                           MacWidgetFactory
                                           MacIcons))
  (:import (com.jgoodies.forms.layout FormLayout)))

;; (defmacro on-action [component event & body]
;;   `(. ~component addActionListener
;;       (proxy [java.awt.event.ActionListener] []
;;         (actionPerformed [~event] ~@body))))

(defn create-source-list []
  (let [category (SourceListCategory. "Category")
        model    (doto (SourceListModel.)
                   (.addCategory category)
                   (.addItemToCategory (SourceListItem. "Item 1") category)
                   (.addItemToCategory (SourceListItem. "Item 2") category)
                   (.addItemToCategory (SourceListItem. "Item 3") category)
                   (.addItemToCategory (SourceListItem. "Item 4") category)
                   (.addItemToCategory (SourceListItem. "Item 5") category))]
    (SourceList. model)))

(defn create-gui []
  (let [frame       (JFrame. "SDB Viewer")
        control-bar (SourceListControlBar.)
        source-list (create-source-list)
        split-pane  (. MacWidgetFactory createSplitPaneForSourceList
                       source-list
                       (JTextArea.))]
    (doto control-bar
      (.createAndAddButton MacIcons/PLUS nil)
      (.createAndAddButton MacIcons/MINUS nil)
      (.installDraggableWidgetOnSplitPane split-pane))
    (.installSourceListControlBar source-list control-bar)
    (.setDividerLocation split-pane 200)
    (doto frame
      (.add split-pane BorderLayout/CENTER)
      (.setSize 600 400)
      (.setVisible true))
    frame))



;; (defn create-gui []
;;   (let [control-bar (doto (SourceListControlBar.)
;;                       (.createAndAddButton MacIcons/PLUS nil)
;;                       (.createAndAddButton MacIcons/MINUS nil))
;;         source-list (doto (create-source-list)
;;                       (.installSourceListControlBar control-bar))
;;         split-pane  (doto (. MacWidgetFactory createSplitPaneForSourceList
;;                              source-list
;;                              (JTextArea.))
;;                       (.setDividerLocation 200))
;;         frame       (doto (JFrame. "Source List Frame")
;;                       (.add split-pane BorderLayout/CENTER)
;;                       (.setSize 600 400)
;;                       (.setVisible true))]
;;     (.installDraggableWidgetOnSplitPane control-bar split-pane)))
