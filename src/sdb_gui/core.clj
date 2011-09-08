(ns sdb_gui.core
  (:import (javax.swing JFrame JPanel JButton JOptionPane JLabel))
  (:import (com.explodingpixels.macwidgets SourceList
                                           SourceListModel
                                           SourceListItem
                                           SourceListCategory)))

(defmacro on-action [component event & body]
  `(. ~component addActionListener
      (proxy [java.awt.event.ActionListener] []
        (actionPerformed [~event] ~@body))))

(defn source-list []
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
  (let [sl      (source-list)
        frame   (doto (JFrame. "Source List Frame")
                  (.setSize 400 400)
                  (.setContentPane (.getComponent sl))
                  (.setVisible true))]
        frame))

(defn -main [& args]
  (hello-app))