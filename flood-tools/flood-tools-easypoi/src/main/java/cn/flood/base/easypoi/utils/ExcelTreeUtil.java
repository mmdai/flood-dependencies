package cn.flood.base.easypoi.utils;


import cn.flood.base.easypoi.entity.ColEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * excel poi 处理tree结构的数据 工具类
 */
public class ExcelTreeUtil {
    
    /**
     * 传入的id 必须存在list集合里
     * 获取某节点的深度
     * @param list
     * @param id   根节点
     * @param step 当前节点级别
     * @return
     */
    public static int getTreeStep(List<ColEntity> list, String id, int step) {
        if ("".equals(id) || null == id) return step;
        for (ColEntity cc : list) {
            if (id.equals(cc.getId())) {
                int temp = step + 1;
                return getTreeStep(list, cc.getPid(), temp);
            }
        }
        return step;
    }

    /**
     * 遍历所有数据 获取树最大的深度
     * @param list
     * @return
     */
    public static int getMaxStep(List<ColEntity> list) {
        List<Integer> nums = new ArrayList<>();
        for (ColEntity cc : list) {
            nums.add(getTreeStep(list, cc.getId(), 0));
        }
        return Collections.max(nums);
    }

    /**
     * 获取最底部子节点的个数 所有叶子节点个数
     * @param list
     * @param did
     * @return
     */
    public static int getDownChildren(List<ColEntity> list, String did) {
        int sum = 0;
        for (ColEntity cc : list) {
            if (did.equals(cc.getPid())) {
                sum++;
                //判断该节点 是否有子节点
                if (hasChild(list, cc)) {
                    sum += getDownChildren(list, cc.getId()) - 1;
                }
            }
        }
        return sum;
    }

    /**
     * 获取父节点
     * @param list 所有的list数据，一条一条
     * @param did  当前节点id
     * @return
     */
    public static ColEntity getParentCol(List<ColEntity> list, String did) {
        for (ColEntity cc : list) {
            if (did != null && did.equals(cc.getId())) {
                return cc;
            }
            if (did == null && did == cc.getId()) {
                return cc;
            }
        }
        return new ColEntity() {{
            setCol(0);
            setRow(0);
        }};
    }

    /**
     * 获取兄弟节点个数 这个必须是有排序的
     * @param list   所有的list数据，一条一条
     * @param ColEntity 当前节点信息
     * @return
     */
    public static int getBrotherChilNum(List<ColEntity> list, ColEntity ColEntity) {
        int sum = 0;
        for (ColEntity cc : list) {
            if (ColEntity.getId().equals(cc.getId())) {
                break;
            }
            if (!ColEntity.getPid().equals(cc.getPid())) {
                continue;
            }
            int temp = getDownChildren(list, cc.getId());
            if (temp == 0 || temp == 1) {
                sum++;
            } else {
                sum += temp;
            }
        }
        return sum;
    }

    /**
     * 根据某节点的第几层的父节点id
     * @param list 所有的list数据，一条一条
     * @param id   当前节点id
     * @param step 第几层(深度 从零开始)
     * @return
     */
    public static String getStepParentId(List<ColEntity> list, String id, int step) {
        String f_id = null;
        for (ColEntity cc : list) {
            if (id.equals(cc.getId())) {
                int cstep = getTreeStep(list, cc.getId(), 0);
                if (step == cstep) {
                    return id;
                }
                int fstep = getTreeStep(list, cc.getPid(), 0);
                if (step == fstep) {
                    f_id = cc.getPid();
                    break;
                } else {
                    getStepParentId(list, cc.getPid(), step);
                }
            }
        }
        return f_id;
    }

    /**
     * 判断是否有子节点
     * @param list 遍历的数据
     * @param node 某个节点
     * @return
     */
    public static boolean hasChild(List<ColEntity> list, ColEntity node) {
        return getChildList(list, node).size() > 0;
    }

    /**
     * 得到子节点列表
     * @param list 遍历的数据
     * @param node 某个节点
     * @return
     */
    public static List<ColEntity> getChildList(List<ColEntity> list, ColEntity node) {
        List<ColEntity> nodeList = new ArrayList<>();
        Iterator<ColEntity> it = list.iterator();
        while (it.hasNext()) {
            ColEntity n = (ColEntity) it.next();
            if (n.getPid() != null && n.getPid().equals(node.getId())) {
                nodeList.add(n);
            }
        }
        return nodeList;
    }

    /**
     * 使用递归方法建树
     * @param treeNodes
     */
    public static List<ColEntity> buildByRecursive(List<ColEntity> treeNodes, String rootID) {
        List<ColEntity> trees = new ArrayList<>();
        boolean flag = false;
        boolean sflag = false;
        for (ColEntity treeNode : treeNodes) {
            if ((rootID == null && rootID == treeNode.getId())) {
                flag = true;
            }
            if (rootID != null && rootID.equals(treeNode.getId())) {
                flag = true;
            }
            if (flag) {
                trees.add(findChildren(treeNode, treeNodes));
                flag = false;
            }
        }
        if (trees.size() <= 0) {
            for (ColEntity treeNode : treeNodes) {
                if ((rootID == null && rootID == treeNode.getPid())) {
                    sflag = true;
                }
                if (rootID != null && rootID.equals(treeNode.getPid())) {
                    sflag = true;
                }
                if (sflag) {
                    trees.add(findChildren(treeNode, treeNodes));
                    sflag = false;
                }
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    public static ColEntity findChildren(ColEntity treeNode, List<ColEntity> treeNodes) {
        for (ColEntity it : treeNodes) {
            if (treeNode.getId().equals(it.getPid())) {
                if (treeNode.getCellList() == null) {
                    treeNode.setCellList(new ArrayList<>());
                }
                treeNode.getCellList().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }
}
