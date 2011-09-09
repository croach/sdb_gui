(ns sdb_gui.core
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

(defmacro on-action [component event & body]
  `(. ~component addActionListener
      (proxy [java.awt.event.ActionListener] []
        (actionPerformed [~event] ~@body))))

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


(defn hello-app []
  (let [control-bar (doto (SourceListControlBar.)
                      (.createAndAddButton MacIcons/PLUS nil)
                      (.createAndAddButton MacIcons/MINUS nil))
        source-list (doto (create-source-list)
                      (.installSourceListControlBar control-bar))
        split-pane  (doto (. MacWidgetFactory createSplitPaneForSourceList
                             source-list
                             (JTextArea.))
                      (.setDividerLocation 200))
        frame       (doto (JFrame. "Source List Frame")
                      (.add split-pane BorderLayout/CENTER)
                      (.setSize 600 400)
                      (.setVisible true))]
    (.installDraggableWidgetOnSplitPane control-bar split-pane)))


(defn -main [& args]
  (hello-app))