package ai_3_2014210066;
import java.util.*;

public class Node {
    static final int MAX = 15;
	private int[][] cell = new int[MAX][MAX]; //판
	private int[] position = {0,0}; //방금 둔 수의 위치
	private int[] bestPosition = {0,0}; //update해갈 best Position
	
	public LinkedList<MyObject> costSquares = new LinkedList<MyObject>(); //search space
	private Node winningSequence; // 3차에선 사용하지 않음
    private Node leftChild;
    private Node rightSibling;
    
    public Node(int[][] cell) {
    	for( int i=0; i<MAX; i++){
    		for (int j=0; j<MAX; j++){
    			this.cell[i][j] = cell[i][j];
    		}
    	}
    }
 
    public void setCell(int[][] cell) {
    	for( int i=0; i<MAX; i++){
    		for (int j=0; j<MAX; j++){
    			this.cell[i][j] = cell[i][j];
    		}
    	}
    }
 
    public int[][] getCell() {
        return cell;
    }
    public void setCellPoint(int i,int j,int k){
    	this.cell[i][j] = k;
    }

    // 배열의 index를 넘어가면, 상대방이 돌을 두고 있는 것으로 인식한다.
    // 이렇게 함으로써 main문의 renjurule함수 등을 보다 편리하게 사용가능하다.
    
    public int getPoint(int i, int j, boolean pyr){
    	// i랑 j가 0보다 작거나, 14보다 크면 무조건 반대	
    	if (i<0 || j<0 || i>=MAX || j>=MAX){
    		if (pyr){ return 1; }
    		else { return -1; }
    	}
    	return cell[i][j];	    	
    }
    //마찬가지다.
    public int getPoint(int i, int j){
    	// i랑 j가 0보다 작거나, 14보다 크면 무조건 반대	
    	if (i<0 || j<0 || i>=MAX || j>=MAX){
    		return 3;
    	}
    	return cell[i][j];	    	
    }

    
    public void setPosition(int i, int j) {
    	this.position[0] = i;
        this.position[1] = j; 
    }
    
    public void setBestPosition(int i, int j) {

    	this.bestPosition[0] = i;
        this.bestPosition[1] = j; //중복검사 필요함. 
        
    }
    
    //돌을 두었을 때 그 주변에 있는 좌표들을 탐색할 수 있도록 추가해준다.
    public void setSearchSquare(int i, int j){
    	
       if (i-1>=0){
       	if(cell[i-1][j]==0){
       		MyObject tempArr = new MyObject(i-1,j);
       		costSquares.add(tempArr);
       	}
           if(j-1>=0){
           	if(cell[i-1][j-1]==0){
           		MyObject tempArr = new MyObject(i-1,j-1);
           		costSquares.add(tempArr);
           	}
           }
           if(j+1<MAX){
           	if(cell[i-1][j+1]==0){
           		MyObject tempArr = new MyObject(i-1,j+1);
           		costSquares.add(tempArr);
           	}
           }
       }
       if (i+1<MAX){
       	if(cell[i+1][j]==0){
       		MyObject tempArr = new MyObject(i+1,j);
       		costSquares.add(tempArr);
       	}
           if(j-1>=0){
           	if(cell[i+1][j-1]==0){
           		MyObject tempArr = new MyObject(i+1,j-1);
           		costSquares.add(tempArr);
           	}
           }
           if(j+1<MAX){
           	if(cell[i+1][j+1]==0){
           		MyObject tempArr = new MyObject(i+1,j+1);
           		costSquares.add(tempArr);
           	}
           }
       }
       
       if(j+1<MAX){
       	if(cell[i][j+1]==0){
       		MyObject tempArr = new MyObject(i,j+1);
       		costSquares.add(tempArr);
       	}
       }
       if(j-1>=0){
       	if(cell[i][j-1]==0){
       		MyObject tempArr = new MyObject(i,j-1);
       		costSquares.add(tempArr);
       	}
       }
       
       LinkedList<MyObject> finalList = new LinkedList<MyObject>(new HashSet<MyObject>(costSquares));
       costSquares = finalList;
       //중복된 값을 제거한다. hashset은 중복을 포함하지 않기 때문이다.
   }
 
    public int[] getPosition() {
        return position;
    }
    public int[] getBestPosition() {
        return bestPosition;
    }
    
    public void setWinningSequence(Node winningSequence){
    	this.winningSequence = winningSequence;
    }
    
    public Node getWinningSequence(){
    	return winningSequence;
    }
 
    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }
 
    public Node getLeftChild() {
        return leftChild;
    }
 
    public void setRightSibling(Node rightSibling) {
        this.rightSibling = rightSibling;
    }
 
    public Node getRightSibling() {
        return rightSibling;
    }
}