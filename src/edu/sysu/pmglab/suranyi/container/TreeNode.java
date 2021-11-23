package edu.sysu.pmglab.suranyi.container;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author suranyi
 * @description 树结构
 */

public class TreeNode<T> implements Iterable<TreeNode<T>> {
    /**
     * 树节点
     */
    T data;

    /**
     * 父节点
     */
    TreeNode<T> parent;

    /**
     * 子节点
     */
    SmartList<TreeNode<T>> children = new SmartList<>();

    public TreeNode(T data) {
        this.data = data;
    }

    /**
     * 获取当前节点的数据
     */
    public T getData() {
        return this.data;
    }

    /**
     * 判断是否为根节点 (根节点无父节点)
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断是否为叶节点 (叶节点无父节点)
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 添加一个子节点
     */
    public TreeNode<T> addChileNode(T childData) {
        TreeNode<T> childNode = new TreeNode<>(childData);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    /**
     * 添加一个子节点
     */
    public TreeNode<T>[] addChileNode(T[] childDatas) {
        TreeNode<T>[] out = new TreeNode[childDatas.length];

        for (int i = 0; i < childDatas.length; i++) {
            TreeNode<T> childNode = new TreeNode<>(childDatas[i]);
            childNode.parent = this;
            this.children.add(childNode);
            out[i] = childNode;
        }

        return out;
    }

    /**
     * 添加一个子节点
     */
    public TreeNode<T> addChileNode(TreeNode<T> childNode) {
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    /**
     * 将当前节点设置为 top 节点
     */
    public void setToTopNode() {
        this.parent = null;
    }

    /**
     * 获取当前节点的层
     */
    public int getLevel() {
        if (this.isRoot()) {
            return 0;
        } else {
            return parent.getLevel() + 1;
        }
    }

    /**
     * 获取最大深度 (当选择了子节点时，也仅检查子树的深度)
     */
    public int getMaxLevel() {
        return getMaxLevel0() - this.getLevel();
    }

    private int getMaxLevel0() {
        int level = this.getLevel();
        for (TreeNode<T> node : this) {
            if (node.isLeaf()) {
                if (node.getLevel() > level) {
                    level = node.getLevel();
                }
            } else {
                int nodeMaxLevel = node.getMaxLevel0();
                if (nodeMaxLevel > level) {
                    level = nodeMaxLevel;
                }
            }
        }

        return level;
    }

    /**
     * 获取父节点
     */
    public TreeNode<T> getParent() {
        return this.parent;
    }

    /**
     * 获取当前节点的子节点
     */
    public SmartList<TreeNode<T>> getChildren() {
        return this.children;
    }

    /**
     * 获取当前节点的子节点
     */
    public SmartList<TreeNode<T>> getBrothers() {
        if (this.parent == null) {
            return null;
        } else {
            return this.parent.getChildren();
        }
    }

    /**
     * 通过节点数据获取当前节点的子节点
     */
    public TreeNode<T> getChildren(T data) {
        for (TreeNode<T> node : this.children) {
            if (node.getData().equals(data)) {
                return node;
            }
        }
        return null;
    }

    /**
     *  通过节点数据获取当前节点的子孙节点
     */
    public TreeNode<T> getDescendant(T data) {
        if (this.data.equals(data)) {
            return this;
        }

        for (TreeNode<T> node : this.children) {
            if (node.isLeaf()) {
                if (node.getData().equals(data)) {
                    return node;
                }
            } else {
                // 非叶节点, 说明有子节点
                TreeNode<T> descendant = node.getDescendant(data);
                if (descendant != null) {
                    return descendant;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前节点的子节点
     */
    public TreeNode<T>[] getPathOf(T data) {
        if (this.data.equals(data)) {
            return new TreeNode[]{this};
        }

        for (TreeNode<T> node : this.children) {
            if (node.isLeaf()) {
                if (node.getData().equals(data)) {
                    return new TreeNode[]{this, node};
                }
            } else {
                // 非叶节点, 说明有子节点
                TreeNode<T>[] descendantPaths = node.getPathOf(data);
                if (descendantPaths != null) {
                    TreeNode<T>[] finalDescendantPaths = new TreeNode[descendantPaths.length + 1];
                    finalDescendantPaths[0] = this;
                    System.arraycopy(descendantPaths, 0, finalDescendantPaths, 1, descendantPaths.length);
                    return finalDescendantPaths;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return "[" + this.data.toString() + "]";
        } else {
            String temp = Arrays.deepToString(this.children.toArray());
            return "[" + (this.data.toString() + ", " + temp.substring(1, temp.length() - 1)) + "]";
        }
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new Iterator<TreeNode<T>>() {
            int seek = 0;

            @Override
            public boolean hasNext() {
                return this.seek < children.size();
            }

            @Override
            public TreeNode<T> next() {
                return children.get(this.seek++);
            }
        };
    }
}