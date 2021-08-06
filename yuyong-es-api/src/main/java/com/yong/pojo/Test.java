package com.yong.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.zip.ZipInputStream;

/**
 * @Classname Test
 * @Description TODO
 * @Date 2021/8/4 15:24
 * @Created by rs
 */
public class Test {
    /**
     *
     * @author: Harry.Shaw
     * @desc: 使用zip进行解压缩
     * @date: 2015年6月11日 下午4:55:47
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    @SuppressWarnings("restriction")
    public static final String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed = null;
        try {
            byte[] compressed = new sun.misc.BASE64Decoder()
                    .decodeBuffer(compressedStr);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString("UTF-8");
        } catch (IOException e) {
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }



//    public static void main(String[] args) {
//        Thread th1= new Thread("custom"){
//            @Override
//            public  void run(){
//                while (true) {
//                    try {
//                        int i = 10;
//                        for(int j = 1; j < 100; j++) {
//                            i = i/(j % 10);
//                        }
//                    } catch (Exception e) {
//                        System.out.println(e);
//                        System.out.println(Thread.currentThread().getName());
//                        Thread.currentThread().interrupt();
//                    }
//                }
//            }
//
//        };
//        th1.start();
////        while (true) {
////            try {
////                int i = 10;
////                for(int j = 1; j < 100; j++) {
////                    i = i/(j % 10);
////                }
////            } catch (Exception e) {
////                System.out.println("xxxxx"+e);
////                System.out.println(Thread.currentThread().getName());
////                Thread.currentThread().interrupt();
////            }
////        }
//    }

    public static void main(String[] args) {
        TreeNode treeNode1 = new TreeNode(2);
        TreeNode treeNode2 = new TreeNode(3);
        TreeNode treeNode3 = new TreeNode(1, treeNode1,treeNode2);
        zigzagLevelOrder(treeNode3);

    }

    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ans = new LinkedList<List<Integer>>();
        if (root == null) {
            return ans;
        }

        Queue<TreeNode> nodeQueue = new LinkedList<TreeNode>();
        nodeQueue.offer(root);
        boolean isOrderLeft = true;

        while (!nodeQueue.isEmpty()) {
            Deque<Integer> levelList = new LinkedList<Integer>();
            int size = nodeQueue.size();
            for (int i = 0; i < size; ++i) {
                TreeNode curNode = nodeQueue.poll();
                if (isOrderLeft) {
                    levelList.offerLast(curNode.val);
                } else {
                    levelList.offerFirst(curNode.val);
                }
                if (curNode.left != null) {
                    nodeQueue.offer(curNode.left);
                }
                if (curNode.right != null) {
                    nodeQueue.offer(curNode.right);
                }
            }
            ans.add(new LinkedList<Integer>(levelList));
            isOrderLeft = !isOrderLeft;
        }

        return ans;
    }



}
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}