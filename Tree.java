package ai_3_2014210066;

public class Tree {
	// 부모 노드에 자식 노드 추가
    public static void add(Node parent, Node child) {
        // 부모 노드의 자식 노드가 없다면
        if(parent.getLeftChild() == null){
            parent.setLeftChild(child);
        }
        // 부모 노드의 자식 노드가 있다면
        else {
            // 자식 노드의 형제로 노드로 추가
            Node temp = parent.getLeftChild();
            while(temp.getRightSibling() != null){
                temp = temp.getRightSibling();
            }
            temp.setRightSibling(child);
        }
    }
    
}
