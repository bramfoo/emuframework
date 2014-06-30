/*
 * $Revision$ $Date$
 * $Author$
 * $header:
 * Copyright (c) 2009-2011 Tessella plc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information about this project, visit
 *   http://www.keep-project.eu/
 *   http://emuframework.sourceforge.net/
 * or contact us via email:
 *   blohman at users.sourceforge.net
 *   dav_m at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * Developed by:
 *   Tessella plc <www.tessella.com>
 *   Koninklijke Bibliotheek <www.kb.nl>
 *   KEEP <www.keep-project.eu>
 * Project Title: Core Emulation Framework (Core EF)$
 */
package eu.keep.gui.config;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Map;

public class ConfigTreeModel extends DefaultTreeModel {

    private Map<String, List<Map<String, String>>> configMap;

    public ConfigTreeModel(TreeNode root) {
        super(root);
        configMap = null;
    }

    public Map<String, List<Map<String, String>>> getMap() {
        return configMap;
    }

    public void load(Map<String, List<Map<String, String>>> map) {
        configMap = map;
        refresh();
    }

    private void refresh() {

        if(configMap == null) return;

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)super.getRoot();
        root.removeAllChildren();

        for (Map.Entry<String, List<Map<String, String>>> entry : configMap.entrySet()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());

            for (Map<String, String> values : entry.getValue()) {
                for (Map.Entry<String, String> kv : values.entrySet()) {
                    DefaultMutableTreeNode k = new DefaultMutableTreeNode(kv.getKey());
                    DefaultMutableTreeNode v = new DefaultMutableTreeNode(kv.getValue());
                    k.add(v);
                    node.add(k);
                }
            }

            root.insert(node, 0);
        }
    }

    public void save(TreePath path, String newValue) {

        Object[] nodes = path.getPath();

        String key1 = (String)((DefaultMutableTreeNode)nodes[1]).getUserObject();
        String key2 = (String)((DefaultMutableTreeNode)nodes[2]).getUserObject();
        String oldValue = (String)((DefaultMutableTreeNode)nodes[3]).getUserObject();

        List<Map<String, String>> mapList = configMap.get(key1);

        Map<String, String> toAdjust = null;
        boolean foundMap = false;

        for(Map<String, String> map : mapList) {
            toAdjust = map;
            for(Map.Entry<String, String> entry : map.entrySet()) {
                if(entry.getKey().equals(key2) && entry.getValue().equals(oldValue)) {
                    foundMap = true;
                    break;
                }
            }
            if(foundMap) {
                break;
            }
        }

        if(toAdjust != null) {
            toAdjust.put(key2, newValue);
            ((DefaultMutableTreeNode)nodes[3]).setUserObject(newValue);
            refresh();
        }
    }
}
