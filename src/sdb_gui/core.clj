(ns sdb_gui.core
  (:use [sdb_gui.gui :only (create-gui)])
  (:use [sdb_gui.sdb]))

(defn -main [& args]
  (let [gui (create-gui)]))